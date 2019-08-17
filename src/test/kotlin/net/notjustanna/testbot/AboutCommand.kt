package net.notjustanna.testbot

import net.notjustanna.psi.commands.Category
import net.notjustanna.psi.commands.Command
import net.notjustanna.psi.commands.ICommand
import net.notjustanna.psi.commands.context.CommandContext

@Command("about")
class AboutCommand : ICommand {
    override val category: Category? = null

    override fun CommandContext.call() {
        sendEmbed {
            description("<3 for using our bot.")
        }
    }
}
