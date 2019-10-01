package net.notjustanna.psi.commands.context

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.builder.EmbedBuilder
import com.mewna.catnip.entity.channel.TextChannel
import com.mewna.catnip.entity.guild.Guild
import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import com.mewna.catnip.entity.message.MessageOptions
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.direct
import org.kodein.di.generic.instance
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.parser.Args
import net.notjustanna.psi.permissions.Permission
import net.notjustanna.utils.extensions.lib.embed
import net.notjustanna.utils.extensions.lib.message
import net.notjustanna.utils.kodein

data class CommandContext(
    val message: Message,
    val args: Args,
    val permissions: Set<Permission>
) : KodeinAware {
    val catnip: Catnip by lazy { message.catnip() }

    override val kodein: Kodein by lazy { catnip.kodein().kodein }

    val def by lazy { direct.instance<BotDef>() }

    val author by lazy { ContextMember(message.author(), message.member()!!) }

    val channel: TextChannel
        get() = message.channel().asTextChannel()

    val guild: Guild
        get() = message.guild()!!

    val self by lazy { ContextMember(catnip.selfUser()!!, guild.selfMember()) }

    fun sendEmbed(builder: EmbedBuilder = EmbedBuilder(), init: EmbedBuilder.() -> Unit) =
        channel.sendMessage(embed(builder, init))

    fun sendMessage(init: MessageOptions.() -> Unit) = channel.sendMessage(message(init))

    fun send(text: String) = channel.sendMessage(text)

    fun send(embed: Embed) = channel.sendMessage(embed)

    fun send(message: Message) = channel.sendMessage(message)

    fun showHelp(): Unit = throw ShowHelp

    fun <T> returnHelp(): T = throw ShowHelp

    object ShowHelp : RuntimeException()
}

