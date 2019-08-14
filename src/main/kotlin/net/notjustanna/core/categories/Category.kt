package net.notjustanna.core.categories

import net.notjustanna.core.commands.help.Help
import net.notjustanna.core.permissions.Permission

abstract class Category(
    val categoryName: String,
    val help: Help? = null,
    val nsfw: Boolean = false,
    val permissions: List<Permission> = emptyList()
) {
    override fun toString() = "Category(name = $categoryName, permissions = $permissions)"
}