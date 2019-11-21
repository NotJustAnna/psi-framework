package net.notjustanna.psi.bootstrap

import com.mewna.catnip.Catnip
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
import net.notjustanna.psi.commands.manager.CommandRegistryImpl
import net.notjustanna.psi.executor.service.JavaThreadTaskExecutor
import net.notjustanna.psi.executor.service.TaskExecutorService
import net.notjustanna.psi.logging.DiscordLogger

/**
 * The framework's [Kodein] configurator.
 */
class PsiKodein(def: BotDef) : Kodein by Kodein(allowSilentOverride = true, init = {
    installJit()

    bind<Kodein>() with singleton { kodein }
    bind<DKodein>() with singleton { dkodein }

    bind<BotDef>() with instance(def)
    bind<Catnip>() with eagerSingleton { Catnip.catnip(def.catnipOptions) }

    bind<CommandRegistry>() with singleton { CommandRegistryImpl() }
    bind<CommandProcessor>() with singleton { CommandProcessor(instance()) }

    bind<TaskExecutorService>() with singleton { JavaThreadTaskExecutor.default }
    bind<ErrorHandler>() with singleton { ErrorHandler.Default }
    def.consoleWebhook?.let { bind<DiscordLogger>() with singleton { DiscordLogger(it) } }
    def.kodeinModule?.let { import(it, true) }
})