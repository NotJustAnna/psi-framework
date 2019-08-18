package net.notjustanna.psi

import com.mewna.catnip.CatnipOptions
import org.kodein.di.Kodein
import java.awt.Color

/**
 * A Bot Definitions file.
 *
 * Use [PsiApplication] with a instance of this interface to start a bot.
 */
interface BotDef {
    /**
     * Bot name. Can be anything.
     *
     * Used internally in webhook messages.
     *
     * **Example**: `"MyBot"`
     */
    val botName: String

    /**
     * Bot version. Can be anything.
     *
     * Used internally in webhook messages.
     *
     * **Example**: `"1.0"`
     */
    val version: String

    /**
     * Base package of your commands and executables.
     *
     * Used to narrow down the classes to scan for.
     *
     * **Example**: `"com.coderwebsite.mybot"`
     */
    val basePackage: String

    /**
     * Prefixes that your bot answers to.
     *
     * **Example**: `listOf("!", "-")`
     */
    val prefixes: List<String>

    /**
     * Splashes to be randomly shown as the bot's presence.
     *
     * Can be a empty.
     *
     * **Example**: `listOf("New commands!", "Plays music!")`
     */
    val splashes: List<String>

    /**
     * Webhook to announce bot start-ups.
     *
     * Can be `null`, which disables the console messages.
     */
    val consoleWebhook: String?

    /**
     * Webhook to announce server joins or leaves.
     *
     * Can be `null`, which disables the server messages.
     */
    val serversWebhook: String?

    /**
     * Main color of the bot.
     *
     * Used to generate the [net.notjustanna.psi.commands.help.Help] dialogs.
     *
     * **Example**: [Colors.blurple][net.notjustanna.utils.Colors.blurple]
     */
    val mainColor: Color

    /**
     * Options used to create the [com.mewna.catnip.Catnip] instance.
     *
     * You're supposed to at least create a [CatnipOptions] interface with your bot token.
     *
     * **Example**: `new CatnipOptions("YOUR_TOKEN_HERE")`
     */
    val catnipOptions: CatnipOptions

    /**
     * Custom [Kodein] module used for dependency injection.
     *
     * Can be `null`.
     *
     * This is your entry point to override
     * [the default command processor][net.notjustanna.psi.commands.manager.CommandProcessor],
     * [add a custom error handling][net.notjustanna.psi.bootstrap.CatnipErrorHandler],
     * [override the task executor][net.notjustanna.psi.executor.TaskExecutorService],
     * as well as adding extra objects which can be injected on commands.
     */
    val kodeinModule: Kodein.Module?
}