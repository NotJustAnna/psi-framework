package net.notjustanna.psi.commands

import net.notjustanna.psi.commands.help.Help
import net.notjustanna.psi.permissions.Permission

interface Category {
    val categoryName: String
    val help: Help?
    val nsfw: Boolean
    val permissions: List<Permission>
}