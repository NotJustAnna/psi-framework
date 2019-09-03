package net.notjustanna.psi

import org.kodein.di.direct
import org.kodein.di.generic.instance
import net.notjustanna.psi.bootstrap.BootstrapCreator
import net.notjustanna.psi.bootstrap.BootstrapLogger
import net.notjustanna.psi.bootstrap.BootstrapLogic
import net.notjustanna.psi.bootstrap.ShutdownManager
import kotlin.system.exitProcess

/**
 * PsiApplication -- the main class to start a bot application.
 *
 * @constructor Creates a instance with the bot definition.
 * @param def the bot definition.
 */
class PsiApplication(private val def: BotDef) {
    private lateinit var shutdownManager: ShutdownManager

    /**
     * Starts the bot application.
     */
    fun init() {
        val log = BootstrapLogger(def)
        val creator = BootstrapCreator(def)
        log.started()

        try {
            val scanResult = creator.scanResult()
            val kodein = creator.kodein()
            shutdownManager = kodein.direct.instance()

            BootstrapLogic(def, log, scanResult, kodein).launch()
        } catch (e: Exception) {
            log.failed(e)
            exitProcess(1)
        }
    }

    /**
     * Shutdowns a previously started bot application.
     */
    fun shutdown() {
        shutdownManager.shutdown()
    }
}
