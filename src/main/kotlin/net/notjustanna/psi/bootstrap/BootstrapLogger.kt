package net.notjustanna.psi.bootstrap

import com.mewna.catnip.util.CatnipMeta
import mu.KLogging
import net.notjustanna.psi.BotDef
import net.notjustanna.psi.exported.psi_version
import net.notjustanna.psi.logging.DiscordLogger
import net.notjustanna.utils.Colors
import net.notjustanna.utils.extensions.lang.limit
import net.notjustanna.utils.extensions.lang.simpleName
import net.notjustanna.utils.extensions.lib.description
import net.notjustanna.utils.extensions.lib.field
import java.io.PrintWriter
import java.io.StringWriter
import java.time.OffsetDateTime

class BootstrapLogger(private val def: BotDef) {
    private companion object : KLogging()

    private val log = def.consoleWebhook?.let { DiscordLogger(it) }

    init {
        log?.text("——————————")
    }

    fun started() {
        logger.info("Booting up...")
        log?.embed {
            author("${def.botName} - Booting up...")
            color(Colors.discordYellow)

            description(
                "${def.botName} v${def.version} (Psi v$psi_version) + Catnip ${CatnipMeta.VERSION}",
                "Hol' up, we're starting!"
            )

            timestamp(OffsetDateTime.now())
        }
    }

    fun successful(shardCount: Int, commandCount: Int) {
        logger.info { "Successful boot! $commandCount commands loaded." }
        log?.embed {
            author("${def.botName} - Successful boot")
            color(Colors.discordGreen)

            description(
                "$shardCount shards loaded.",
                "$commandCount commands loaded."
            )

            timestamp(OffsetDateTime.now())
        }
    }

    fun failed(e: Exception) {
        logger.info("Boot failed.", e)
        log?.embed {

            author("${def.botName} - Boot failed")
            color(Colors.discordRed)

            field(
                e.javaClass.name,
                e.message?.limit(1024) ?: "<No message>"
            )
            field("More Info:", "See file below.")

            timestamp(OffsetDateTime.now())
        }
        log?.message {
            val s = StringWriter().also { e.printStackTrace(PrintWriter(it, true)) }.toString()
            val fileName = e.simpleName() + "_stacktrace.txt"
            addFile(fileName, s.toByteArray())
        }
    }

}