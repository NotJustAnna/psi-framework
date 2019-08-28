package net.notjustanna.psi.commands

import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.permissions.Permissions

/**
 * An [ICommand]'s category.
 */
interface ICategory {
    /**
     * The name of the category.
     */
    val categoryName: String

    val nsfw: Boolean

    interface Permission : ICategory {
        val permissions: Permissions
    }

    interface HelpDialog : ICategory {
        fun onHelp(def: BotDef, message: Message): Embed
    }

    interface HelpDialogProvider : ICategory {
        val helpHandler: HelpDialog
    }
}