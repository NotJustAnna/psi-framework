package net.notjustanna.testbot

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import net.notjustanna.psi.commands.Category
import net.notjustanna.psi.commands.Command
import net.notjustanna.psi.commands.ICategory
import net.notjustanna.psi.commands.ICommand
import net.notjustanna.psi.commands.context.CommandContext
import net.notjustanna.psi.commands.manager.CommandRegistry
import net.notjustanna.utils.extensions.lib.field

@Command("help")
@Category("info")
class HelpCommand(override val category: ICategory, override val kodein: Kodein) : ICommand, KodeinAware {
    override fun CommandContext.call() {
        val registry: CommandRegistry by instance()

        sendEmbed {
            registry.categorizedCommands().forEach { (key, value) ->
                field(
                    registry.name(key) ?: "null",
                    value.joinToString { "`${registry.names(it)?.first()}`" }
                )
            }
        }
    }
}
