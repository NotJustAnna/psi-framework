package net.notjustanna.psi.commands

import net.notjustanna.psi.commands.help.HelpProvider

/**
 * An [ICommand]'s category.
 */
interface ICategory {
    /**
     * The name of the category.
     */
    val categoryName: String

    val nsfw: Boolean
        get() = false

    val help: HelpProvider?
        get() = this as? HelpProvider
}