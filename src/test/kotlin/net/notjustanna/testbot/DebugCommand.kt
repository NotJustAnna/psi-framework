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
import net.notjustanna.utils.extensions.lang.limit
import net.notjustanna.utils.extensions.lib.field

@Command("debug")
@Category("enum#debug")
class DebugCommand(override val category: ICategory, override val kodein: Kodein) : ICommand, KodeinAware {
    override fun CommandContext.call() {
        val registry: CommandRegistry by instance()

        with(registry) {
            sendEmbed {
                field(
                    "commands",
                    commands.toString().limit(1024)
                )
                field(
                    "categories",
                    categories.toString().limit(1024)
                )
                field(
                    "commandLookup",
                    commandLookup.toString().limit(1024)
                )
                field(
                    "categoryNameLookup",
                    categoryNameLookup.toString().limit(1024)
                )
                field(
                    "categoryCommandsLookup",
                    categoryCommandsLookup.toString().limit(1024)
                )
            }
        }
    }
}
