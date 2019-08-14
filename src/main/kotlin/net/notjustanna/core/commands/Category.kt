package net.notjustanna.core.commands

import net.notjustanna.core.commands.help.Help
import net.notjustanna.core.permissions.Permission

interface Category {
    val categoryName: String
    val help: Help?
    val nsfw: Boolean
    val permissions: List<Permission>
}