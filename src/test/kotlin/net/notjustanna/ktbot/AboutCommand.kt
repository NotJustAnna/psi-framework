package net.notjustanna.ktbot

import net.notjustanna.psi.commands.Category
import net.notjustanna.psi.commands.Command
import net.notjustanna.psi.commands.ICategory
import net.notjustanna.psi.commands.ICommand
import net.notjustanna.psi.commands.context.CommandContext
import net.notjustanna.psi.exported.psi_version
import net.notjustanna.utils.extensions.lib.description

@Command("about", "thanks")
@Category("kt#info")
class AboutCommand(override val category: ICategory) : ICommand {
    override fun CommandContext.call() {
        sendEmbed {
            description(
                "This is an example Kotlin bot made using psi $psi_version.",
                "Thanks for using it <3."
            )
        }
    }
}
