package net.notjustanna.core

import net.notjustanna.core.commands.Category
import net.notjustanna.core.commands.Command
import net.notjustanna.core.commands.ICommand
import net.notjustanna.core.commands.context.CommandContext

@Command("about")
class AboutCommand : ICommand {
    override val category: Category? = null

    override fun CommandContext.call() {
        sendEmbed {
            description("<3 for using our bot.")
        }
    }
}
