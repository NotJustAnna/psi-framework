package net.notjustanna.testbot

import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import org.kodein.di.Kodein
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.PsiApplication
import net.notjustanna.utils.Colors

object TestBot : BotDef {
    override val botName = "TestBot"
    override val version = "1.0"
    override val basePackage = "net.notjustanna.testbot"
    override val prefixes = listOf("!")
    override val splashes = listOf("I love tests!")
    override val mainColor = Colors.blurple

    override val builder = DefaultShardManagerBuilder.createLight(
        System.getenv("token"), GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES
    )
    override val kodeinModule: Kodein.Module? = null
}

fun main() {
    PsiApplication(TestBot).init()
}