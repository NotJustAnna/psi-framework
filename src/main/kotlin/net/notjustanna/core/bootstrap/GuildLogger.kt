package net.notjustanna.core.bootstrap

import com.mewna.catnip.entity.channel.GuildChannel
import com.mewna.catnip.entity.guild.Guild
import net.notjustanna.core.logging.DiscordLogger
import net.notjustanna.utils.Colors
import net.notjustanna.utils.extensions.lang.multiline
import net.notjustanna.utils.extensions.lang.plusAssign

class GuildLogger(url: String) : DiscordLogger(url) {
    fun onGuildJoin(guild: Guild) {
        embed {
            author("AruLog | New Server")
            color(Colors.discordGreen)
            thumbnail(guild.iconUrl())

            val builder = StringBuilder()

            builder += multiline(
                "\u25AB **Name**: ${guild.name()}",
                "**M/TC/VC**: ${guild.memberCount()}/${guild.channels().count(GuildChannel::isText)}/${guild.channels().count(
                    GuildChannel::isVoice
                )}",
                "\u25AB **Region**: `${guild.region()}`",
                "\u25AB **Owner**: ${guild.owner().user().discordTag()}"
            )

            if (guild.features().isNotEmpty()) {
                builder
                    .append("\n\u25AB **Features**: ")
                    .append(guild.features().joinToString("`, `", "`", "`"))
            }

            description(builder.toString())

            footer(
                "Count: ${guild.catnip().cache().guilds().size()} | ID: ${guild.id()}",
                guild.catnip().selfUser()!!.effectiveAvatarUrl()
            )
        }
    }

    fun onGuildLeave(guild: Guild) {
        embed {
            author("AruLog | Lost Server")
            color(Colors.discordRed)
            thumbnail(guild.iconUrl())

            val builder = StringBuilder()

            builder += multiline(
                "\u25AB **Name**: ${guild.name()}",
                "\u25AB **Region**: `${guild.region()}`"
            )

            description(builder.toString())

            footer(
                "Count: ${guild.catnip().cache().guilds().size()} | ID: ${guild.id()}",
                guild.catnip().selfUser()!!.effectiveAvatarUrl()
            )
        }
    }
}