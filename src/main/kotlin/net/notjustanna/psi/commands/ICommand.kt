package net.notjustanna.psi.commands

import com.mewna.catnip.entity.message.Message
import net.notjustanna.psi.commands.ICommand.CustomHandler.Result
import net.notjustanna.psi.commands.context.CommandContext
import net.notjustanna.psi.commands.help.HelpProvider
import net.notjustanna.psi.permissions.Permissions

interface ICommand {
    val category: ICategory?
        get() = null

    val permissions: Permissions
        get() = Permissions.none

    val nsfw: Boolean
        get() = category?.nsfw ?: false

    val help: HelpProvider?
        get() = this as? HelpProvider

    fun CommandContext.call()

    interface Discrete : ICommand {
        fun CommandContext.discreteCall(outer: String)
    }

    interface ExceptionHandler : ICommand {
        fun handle(message: Message, t: Throwable)
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