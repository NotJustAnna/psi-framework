package net.notjustanna.psi.commands

import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.commands.ICommand.CustomHandler.Result
import net.notjustanna.psi.commands.context.CommandContext
import net.notjustanna.psi.permissions.Permissions

interface ICommand {
    val category: ICategory?

    fun CommandContext.call()

    fun nsfw(): Boolean {
        return category?.nsfw ?: false
    }

    interface Discrete : ICommand {
        fun CommandContext.discreteCall(outer: String)
    }

    interface Permission : ICommand {
        val permissions: Permissions
    }

    interface ExceptionHandler : ICommand {
        fun handle(message: Message, t: Throwable)
    }

    interface HelpDialog {
        fun onHelp(def: BotDef, message: Message): Embed
    }

    interface HelpDialogProvider {
        val helpHandler: HelpDialog
    }

    interface CustomHandler : ICommand {
        enum class Result {
            IGNORE, HANDLED
        }

        fun CommandContext.customCall(command: String): Result
    }

    interface CustomDiscreteHandler : ICommand {
        fun CommandContext.customCall(command: String, outer: String): Result
    }
}