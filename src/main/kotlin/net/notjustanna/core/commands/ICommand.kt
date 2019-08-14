package net.notjustanna.core.commands

import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import net.notjustanna.core.commands.ICommand.CustomHandler.Result
import net.notjustanna.core.commands.context.CommandContext
import net.notjustanna.core.permissions.Permissions

interface ICommand {
    val category: Category?

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
        fun onHelp(message: Message): Embed
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