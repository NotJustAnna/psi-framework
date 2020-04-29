package net.notjustanna.ktbot

import com.mewna.catnip.CatnipOptions
import org.kodein.di.Kodein
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.PsiApplication
import net.notjustanna.utils.Colors

object KtBot : BotDef {
    override val botName = "KtBot"
    override val version = "1.0"
    override val basePackage = "net.notjustanna.ktbot"
    override val prefixes = listOf("!")
    override val splashes = listOf("Kotlin!")
    override val mainColor = Colors.discordPurple

    override val catnipOptions = CatnipOptions(System.getenv("token"))
    override val kodeinModule: Kodein.Module? = null
}

fun main() {
    PsiApplication(KtBot).init()
}