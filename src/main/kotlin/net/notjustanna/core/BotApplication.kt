package net.notjustanna.core

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.user.Presence.Activity
import com.mewna.catnip.entity.user.Presence.ActivityType.PLAYING
import com.mewna.catnip.entity.user.Presence.OnlineStatus.ONLINE
import com.mewna.catnip.entity.user.Presence.of
import io.github.classgraph.ClassGraph
import org.kodein.di.direct
import org.kodein.di.generic.instance
import net.notjustanna.core.bootstrap.*
import net.notjustanna.core.commands.manager.CommandRegistry
import net.notjustanna.utils.AruTaskExecutor.task
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

@Suppress("unused")
class BotApplication(private val def: BotDef) {
    private lateinit var shutdownManager: ShutdownManager

    fun init() {
        val log = BootstrapLogger(def)
        log.started()

        try {
            val scanResult = ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .whitelistPackages("net.notjustanna", def.basePackage)
                .scan()

            val catnip = Catnip.catnip(def.catnipOptions)

            val kodein = KodeinBootstrap(def, catnip).create()

            CatnipBootstrap(def, kodein).run {
                onFirstShardReady = {
                    val commandBootstrap = CommandBootstrap(scanResult, kodein)

                    commandBootstrap.createCommands()
                    commandBootstrap.createProviders()
                    commandBootstrap.createStandalones()

                    scanResult.close()
                    commandBootstrap.reportResults()
                }

                onAllShardsReady = {
                    task(1, TimeUnit.MINUTES) {
                        val text = "${def.prefixes.first()}help | ${def.splashes.random()}"
                        catnip.presence(of(ONLINE, Activity.of(text, PLAYING)))
                    }

                    val registry by kodein.instance<CommandRegistry>()
                    log.successful(it, registry.commands.size)
                }

                configure(catnip)
            }

            shutdownManager = kodein.direct.instance()
        } catch (e: Exception) {
            log.failed(e)
            exitProcess(1)
        }
    }

    fun shutdown() {
        shutdownManager.shutdown()
    }
}
