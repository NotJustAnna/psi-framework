package net.notjustanna.core.bootstrap

import com.mewna.catnip.Catnip
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import net.notjustanna.core.BotDef
import net.notjustanna.core.commands.manager.CommandProcessor
import net.notjustanna.core.commands.manager.CommandRegistry
import net.notjustanna.core.logging.DiscordLogger
import net.notjustanna.libs.kodein.jit.installJit

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

        bind<DiscordLogger>() with singleton { DiscordLogger(def.consoleWebhook) }
        bind<ShutdownManager>() with singleton { ShutdownManager() }

        def.kodeinModule?.let { import(it, true) }
    }
}
