package net.notjustanna.psi.bootstrap

import club.minnced.jda.reactor.ReactiveEventManager
import club.minnced.jda.reactor.on
import io.github.classgraph.ClassGraph
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.sharding.ShardManager
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import net.notjustanna.libs.kodein.jit.installJit
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.PsiApplication
import net.notjustanna.psi.commands.RegistryPhase
import net.notjustanna.psi.commands.manager.CommandProcessor
import net.notjustanna.psi.commands.manager.CommandRegistry
import net.notjustanna.psi.commands.manager.CommandRegistryImpl
import net.notjustanna.psi.executor.service.JavaThreadTaskExecutor
import net.notjustanna.psi.executor.service.TaskExecutorService
import net.notjustanna.psi.executor.service.asJavaExecutor
import net.notjustanna.utils.extensions.lang.getValue
import reactor.core.Disposable
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

internal fun bootstrap(app: PsiApplication, def: BotDef, log: BootstrapLogger) {
    val events = ReactiveEventManager()
    val manager = def.builder.setEventManagerProvider { events }.build()

    // Build the Kodein as early as damn possible
    val kodein = Kodein {
        installJit()

        bind<Kodein>() with singleton { kodein }
        bind<DKodein>() with singleton { dkodein }

        bind<BotDef>() with instance(def)
        bind<PsiApplication>() with instance(app)
        bind<ShardManager>() with instance(manager)

        bind<CommandRegistry>() with singleton { CommandRegistryImpl() }
        bind<CommandProcessor>() with singleton { CommandProcessor(instance()) }

        bind<TaskExecutorService>() with singleton { JavaThreadTaskExecutor.default }
        bind<ErrorHandler>() with singleton { ErrorHandler.Default }
        def.kodeinModule?.let { import(it, true) }
    }

    val executor by kodein.instance<TaskExecutorService>()

    val scanResult by ClassGraph()
        .enableClassInfo()
        .enableAnnotationInfo()
        .whitelistPackages("net.notjustanna", def.basePackage)
        .scanAsync(executor.asJavaExecutor(), Runtime.getRuntime().availableProcessors())

    val disposables = ArrayList<Disposable>()
    app.registerShutdownHook { disposables.forEach { it.runCatching { dispose() } } }

    val errorHandler by kodein.instance<ErrorHandler>()

    disposables += events.on<MessageReceivedEvent>()
        .subscribe(kodein.direct.instance<CommandProcessor>(), Consumer(errorHandler::onCommandProcessor))

    with(RegistryBootstrap(scanResult, kodein)) {
        loadInjectors(RegistryPhase.PRE_INITIALIZATION)
        createCategories()
        loadInjectors(RegistryPhase.AFTER_CATEGORIES)
        createCommands()
        loadInjectors(RegistryPhase.AFTER_COMMANDS)
        createStandalones()
        loadInjectors(RegistryPhase.AFTER_EXECUTABLES)
    }

    scanResult.close()

    val splashes = def.splashes
    val mainCommand = def.prefixes.firstOrNull()?.let { def.mainCommandName?.let { name -> "$it$name" } }

    manager.shards.forEach { it.awaitReady() }

    executor.task(1, TimeUnit.MINUTES) {
        val text = sequenceOf(mainCommand, splashes.random()).filterNotNull().joinToString(" | ")

        if (text.isNotBlank()) {
            val activity = Activity.playing(text)

            for (jda in manager.shards.filter { it.status.isInit }) {
                jda.presence.setPresence(OnlineStatus.ONLINE, activity)
            }
        }
    }

    val registry by kodein.instance<CommandRegistry>()
    log.successful(manager.shardsTotal, registry.categoryCount(), registry.commandCount())
}