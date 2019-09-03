package net.notjustanna.psi.bootstrap

import com.mewna.catnip.Catnip
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import net.notjustanna.libs.kodein.jit.installJit
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.commands.manager.CommandProcessor
import net.notjustanna.psi.commands.manager.CommandRegistry
import net.notjustanna.psi.executor.service.JavaThreadTaskExecutor
import net.notjustanna.psi.executor.service.TaskExecutorService
import net.notjustanna.psi.logging.DiscordLogger

class BootstrapCreator(private val def: BotDef) {
    fun scanResult(): ScanResult {
        return ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .whitelistPackages("net.notjustanna", def.basePackage)
            .scan()
    }

    fun kodein(): Kodein {
        return Kodein {
            installJit()
            bind<Kodein>() with singleton { kodein }
            bind<DKodein>() with singleton { dkodein }

            bind<BotDef>() with instance(def)
            bind<Catnip>() with eagerSingleton { Catnip.catnip(def.catnipOptions) }

            bind<CommandRegistry>() with singleton { CommandRegistry() }
            bind<CommandProcessor>() with singleton { CommandProcessor(instance()) }
            bind<ShutdownManager>() with singleton { ShutdownManager() }

            bind<TaskExecutorService>() with singleton { JavaThreadTaskExecutor.default }
            bind<CatnipErrorHandler>() with singleton { SLF4JErrorHandler() }
            def.consoleWebhook?.let { bind<DiscordLogger>() with singleton { DiscordLogger(it) } }
            def.kodeinModule?.let { import(it, true) }
        }
    }
}