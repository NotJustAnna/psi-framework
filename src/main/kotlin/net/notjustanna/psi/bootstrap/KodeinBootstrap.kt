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
        // Install JIT Module
        installJit()

        // Self-references
        bind<Kodein>() with singleton { kodein }
        bind<DKodein>() with singleton { dkodein }

        // Instances
        bind<BotDef>() with instance(def)
        bind<CommandRegistry>() with singleton { CommandRegistry() }
        bind<CommandProcessor>() with singleton { CommandProcessor(instance(), instance()) }
        bind<Catnip>() with instance(catnip)
        bind<TaskExecutorService>() with singleton { PsiTaskExecutor }

        bind<DiscordLogger>() with singleton { DiscordLogger(def.consoleWebhook) }
        bind<ShutdownManager>() with singleton { ShutdownManager() }

        def.kodeinModule?.let { import(it, true) }
    }
}
