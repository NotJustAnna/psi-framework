package net.notjustanna.core.commands.help

import com.mewna.catnip.entity.message.Message
import net.notjustanna.core.BotDef
import net.notjustanna.core.commands.ICommand
import net.notjustanna.core.permissions.Permissions
import net.notjustanna.utils.extensions.lib.embed
import net.notjustanna.utils.extensions.lib.field
import java.awt.Color

class Help(
    val def: BotDef,
    d: BaseDescription,
    vararg val nodes: HelpNode
) : ICommand.HelpDialog {
    val names: List<String>?
    val title: String
    val color: Color
    val permissions: Permissions?
    val thumbnail: String

    init {
        when (d) {
            is CommandDescription -> {
                names = d.names
                title = d.title
                color = d.color ?: def.mainColor
                permissions = d.permissions
                thumbnail = d.thumbnail
            }
            is CategoryDescription -> {
                names = null
                title = d.title
                color = d.color ?: def.mainColor
                permissions = d.permissions
                thumbnail = d.thumbnail
            }
        }
    }

    override fun onHelp(message: Message) = embed {
        color(color)
        thumbnail(thumbnail)

        author(title, null, message.catnip().selfUser()?.effectiveAvatarUrl())
        footer(
            "Requested by ${message.member()!!.effectiveName()}",
            message.author().effectiveAvatarUrl()
        )

        if (permissions != null) {
            field("Permissions Required:", permissions.toString().capitalize())
        }

        if (names != null && names.size > 1) {
            field("Aliases:", names.asSequence().drop(1).joinToString("` `", "`", "`"))
        }

        for (node in nodes) when (node) {
            is Description -> field("Description:", node.value)
            is Usage -> field("Usage:", node.value(def.prefixes.first()))
            is Example -> field(
                "Example:",
                node.value(def.prefixes.first())
            )
            is Note -> field("Note:", node.value)
            is SeeAlso -> field("See Also:", node.value)

            is Field -> field(node.name, node.value)
        }
    }
}
