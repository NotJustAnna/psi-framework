package net.notjustanna.testbot

import net.notjustanna.psi.commands.Category
import net.notjustanna.psi.commands.Command
import net.notjustanna.psi.commands.ICategory
import net.notjustanna.psi.commands.ICommand
import net.notjustanna.psi.commands.context.CommandContext

@Command("about", "thanks")
@Category("info")
class AboutCommand(override val category: ICategory) : ICommand {
    override fun CommandContext.call() {
        sendEmbed {
            description("<3 for using our bot.")
        }
    }
}
