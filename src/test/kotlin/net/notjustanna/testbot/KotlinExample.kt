package net.notjustanna.testbot

import com.mewna.catnip.CatnipOptions
import org.kodein.di.Kodein
import net.notjustanna.psi.BotApplication
import net.notjustanna.psi.BotDef
import net.notjustanna.utils.Colors

object TestBot : BotDef {
    override val botName = "TestBot"
    override val version = "1.0"
    override val basePackage = "net.notjustanna.testbot"
    override val prefixes = listOf("!")
    override val splashes = listOf("I love tests!")
    override val consoleWebhook = System.getenv("webhook")
    override val serversWebhook = System.getenv("webhook")
    override val mainColor = Colors.blurple

    override val catnipOptions = CatnipOptions(System.getenv("token"))
    override val kodeinModule: Kodein.Module? = null
}

fun main() {
    BotApplication(TestBot).init()
}