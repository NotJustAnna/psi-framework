package net.notjustanna.core.commands

import net.notjustanna.core.commands.manager.CommandRegistry

interface ICommandProvider {
    fun provide(r: CommandRegistry)
}
