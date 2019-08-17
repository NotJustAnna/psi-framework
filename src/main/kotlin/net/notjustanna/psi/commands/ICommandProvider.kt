package net.notjustanna.psi.commands

import net.notjustanna.psi.commands.manager.CommandRegistry

interface ICommandProvider {
    fun provide(r: CommandRegistry)
}
