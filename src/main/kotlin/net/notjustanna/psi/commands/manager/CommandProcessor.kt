package net.notjustanna.psi.commands.manager

import com.mewna.catnip.entity.guild.Member
import com.mewna.catnip.entity.message.Message
import com.mewna.catnip.entity.util.Permission.ADMINISTRATOR
import com.mewna.catnip.entity.util.Permission.SEND_MESSAGES
import io.reactivex.functions.Consumer
import mu.KLogging
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.commands.ICommand
import net.notjustanna.psi.commands.ICommand.CustomHandler.Result.HANDLED
import net.notjustanna.psi.commands.context.CommandContext
import net.notjustanna.psi.executor.service.TaskExecutorService
import net.notjustanna.psi.parser.Args
import net.notjustanna.psi.permissions.Permission
import net.notjustanna.utils.extensions.lang.anyOf
import net.notjustanna.utils.extensions.lang.limit
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
open class CommandProcessor(override val kodein: Kodein) : Consumer<Message>, KodeinAware {
    protected val def: BotDef by instance()
    protected val registry: CommandRegistry by instance()
    protected val tasks: TaskExecutorService by instance()

    var count: Long = 0
        private set

    override fun accept(message: Message) {
        val self = message.guild()?.selfMember() ?: return

        if (!anyOf(
                message.author().bot(),
                !self.hasPermissions(message.channel().asGuildChannel(), SEND_MESSAGES),
                !self.hasPermissions(ADMINISTRATOR)
            )) return

        tasks.queue("Cmd:${message.author().discordTag()}:${message.content().limit(32)}") {
            onMessage(message)
        }
    }

    private fun onMessage(message: Message) {
        val raw = message.content()

        for (prefix in def.prefixes) {
            if (raw.startsWith(prefix)) {
                message.nextCommand(raw.substring(prefix.length).trimStart(), null)
                return
            }
        }

        val customPrefixes = customPrefixes(message)

        for (prefix in customPrefixes) {
            if (raw.startsWith(prefix)) {
                message.nextCommand(raw.substring(prefix.length).trimStart(), null)
                return
            }
        }

        if (raw.startsWith('[') && raw.contains(']')) {
            val (cmdRaw, cmdOuter) = raw.substring(1).trimStart().split(']', limit = 2)

            for (prefix in def.prefixes) {
                if (cmdRaw.startsWith(prefix)) {
                    message.nextCommand(cmdRaw.substring(prefix.length).trimStart(), cmdOuter)
                    return
                }
            }

            for (prefix in customPrefixes) {
                if (cmdRaw.startsWith(prefix)) {
                    message.nextCommand(cmdRaw.substring(prefix.length).trimStart(), cmdOuter)
                    return
                }
            }
        }
    }

    private fun Message.nextCommand(rawContent: String, outer: String?) {
        if (!filterMessages(this)) return

        val permissions = resolvePermissions(member()!!)
        if (permissions.isEmpty()) return // blacklisted

        val args = Args(rawContent)
        val cmd = args.takeString().toLowerCase()

        val command = registry[cmd]?.let { if (outer == null) it else it as? ICommand.Discrete }
        val ctx = CommandContext(this, args, permissions)

        if (command != null) {
            if (!filterCommands(this, command, permissions)) return
            beforeCommand(this, cmd, command, permissions)
            logger.trace { "Executing: $cmd by ${author().discordTag()} at ${Date()}" }
            count++
            if (outer == null) {
                command.runCatching { ctx.call() }
                    .onFailure { runCatching { onCommandError(command, this, it) } }
            } else {
                (command as ICommand.Discrete).runCatching { ctx.discreteCall(outer) }
                    .onFailure { runCatching { onCommandError(command, this, it) } }
            }
        } else {
            if (outer == null) {
                if (
                    registry.lookup.keys.mapNotNull { it as? ICommand.CustomHandler }.any {
                        it.runCatching { ctx.customCall(cmd) }.getOrNull() == HANDLED
                    }
                ) return
            } else {
                if (
                    registry.lookup.keys.mapNotNull { it as? ICommand.CustomDiscreteHandler }.any {
                        it.runCatching { ctx.customCall(cmd, outer) }.getOrNull() == HANDLED
                    }
                ) return
            }

            customHandleCommands(this, cmd, args, outer, permissions)
        }
    }


    private fun onCommandError(command: ICommand, message: Message, t: Throwable) {
        when {
            t == CommandContext.ShowHelp -> {
                if (command is ICommand.HelpDialogProvider) {
                    message.channel().sendMessage(command.helpHandler.onHelp(def, message))
                    return
                }

                if (command is ICommand.HelpDialog) {
                    message.channel().sendMessage(command.onHelp(def, message))
                    return
                }

                handleException(command, message, t, null)
            }

            command is ICommand.ExceptionHandler -> {
                try {
                    command.handle(message, t)
                } catch (u: Exception) {
                    handleException(command, message, t, u)
                }
            }

            else -> {
                handleException(command, message, t, null)
            }
        }
    }

    // extra stuff

    private companion object : KLogging() {
        val dummyPermission = object : Permission {
            override val name = "Run Bot"
            override val description = "Override CommandProcessor#resolvePermissions to change this."
        }
    }

    // hooks

    protected open fun customPrefixes(message: Message): List<String> = emptyList()

    protected open fun filterMessages(message: Message): Boolean = true

    protected open fun resolvePermissions(member: Member): Set<Permission> = setOf(dummyPermission)

    protected open fun filterCommands(message: Message, command: ICommand, permissions: Set<Permission>) = true

    protected open fun beforeCommand(message: Message, cmd: String, command: ICommand, permissions: Set<Permission>) = Unit

    protected open fun customHandleCommands(
        message: Message, command: String, args: Args, outer: String?, permissions: Set<Permission>
    ) = Unit

    protected open fun handleException(command: ICommand, message: Message, throwable: Throwable, underlying: Throwable?) {
        underlying?.let(throwable::addSuppressed)
        logger.error(throwable) { "Error while executing $command" }
    }
}