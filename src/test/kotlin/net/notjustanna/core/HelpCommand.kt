package net.notjustanna.core

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import net.notjustanna.core.commands.Category
import net.notjustanna.core.commands.Command
import net.notjustanna.core.commands.ICommand
import net.notjustanna.core.commands.context.CommandContext
import net.notjustanna.core.commands.manager.CommandRegistry

@Command("help")
class HelpCommand(override val kodein: Kodein) : ICommand, KodeinAware {
    override val category: Category? = null

    private val registry: CommandRegistry by instance()

    override fun CommandContext.call() {
        sendEmbed {
            description("**Commands**: ${registry.lookup.values.joinToString { "`${it.first()}`" }}")
        }
    }
}
