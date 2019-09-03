package net.notjustanna.psi.commands

import net.notjustanna.psi.commands.help.HelpProvider
import net.notjustanna.psi.permissions.Permissions

/**
 * An [ICommand]'s category.
 */
interface ICategory {
    /**
     * The name of the category.
     */
    val categoryName: String

    val permissions: Permissions
        get() = Permissions.none

    val nsfw: Boolean
        get() = false

    val help: HelpProvider?
        get() = this as? HelpProvider
}