package net.notjustanna.testbot

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import net.notjustanna.psi.commands.Category
import net.notjustanna.psi.commands.Command
import net.notjustanna.psi.commands.ICommand
import net.notjustanna.psi.commands.context.CommandContext
import net.notjustanna.psi.commands.manager.CommandRegistry

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
