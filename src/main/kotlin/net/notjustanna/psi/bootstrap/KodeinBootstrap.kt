package net.notjustanna.psi.bootstrap

import com.mewna.catnip.Catnip
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import net.notjustanna.libs.kodein.jit.installJit
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.commands.manager.CommandProcessor
import net.notjustanna.psi.commands.manager.CommandRegistry
import net.notjustanna.psi.executor.TaskExecutorService
import net.notjustanna.psi.logging.DiscordLogger
import net.notjustanna.utils.PsiTaskExecutor

class KodeinBootstrap(private val def: BotDef, private val catnip: Catnip) {
    fun create() = Kodein {
        installJit()
        bind<Kodein>() with singleton { kodein }
        bind<DKodein>() with singleton { dkodein }

        bind<BotDef>() with instance(def)
        bind<Catnip>() with instance(catnip)

        bind<CommandRegistry>() with singleton { CommandRegistry() }
        bind<CommandProcessor>() with singleton { CommandProcessor(instance(), instance()) }
        bind<ShutdownManager>() with singleton { ShutdownManager() }

        bind<TaskExecutorService>() with singleton { PsiTaskExecutor }
        bind<CatnipErrorHandler>() with singleton { SLF4JErrorHandler() }
        def.consoleWebhook?.let { bind<DiscordLogger>() with singleton { DiscordLogger(it) } }
        def.kodeinModule?.let { import(it, true) }
    }
}
