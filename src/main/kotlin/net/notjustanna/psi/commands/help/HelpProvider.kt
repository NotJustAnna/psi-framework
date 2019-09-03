package net.notjustanna.psi.commands.help

import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import net.notjustanna.psi.BotDef

interface HelpProvider {
    fun onHelp(def: BotDef, message: Message): Embed
}