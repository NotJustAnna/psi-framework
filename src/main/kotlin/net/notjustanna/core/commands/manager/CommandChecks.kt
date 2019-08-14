package net.notjustanna.core.commands.manager

import com.mewna.catnip.entity.message.Message
import net.notjustanna.core.commands.ICommand
import net.notjustanna.core.permissions.Permission

interface CommandChecks {
    fun runChecks(message: Message, command: ICommand, userPerms: Set<Permission>): Boolean
}