package net.notjustanna.psi.bootstrap

import io.github.classgraph.ScanResult
import mu.KLogging
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import net.notjustanna.libs.kodein.jit.jitInstance
import net.notjustanna.psi.commands.Command
import net.notjustanna.psi.commands.ICommand
import net.notjustanna.psi.commands.ICommandProvider
import net.notjustanna.psi.commands.manager.CommandRegistry
import net.notjustanna.psi.executor.Executable
import net.notjustanna.psi.executor.RunAtStartup
import net.notjustanna.psi.executor.RunEvery
import net.notjustanna.psi.logging.DiscordLogger
import net.notjustanna.utils.Colors
import net.notjustanna.utils.PsiTaskExecutor.queue
import net.notjustanna.utils.PsiTaskExecutor.task
import net.notjustanna.utils.extensions.lang.allOf
import net.notjustanna.utils.extensions.lang.classOf
import net.notjustanna.utils.extensions.lib.field
import java.time.OffsetDateTime

class CommandBootstrap(private val scanResult: ScanResult, private val kodein: Kodein) {
    companion object : KLogging()

    private val registry by kodein.instance<CommandRegistry>()
    private val listener = RegistryListener()

    class RegistryListener : CommandRegistry.Listener {
        val unnamedCommands = ArrayList<String>()
        val noHelpCommands = ArrayList<String>()
        val multipleHelpCommands = ArrayList<String>()

        val clean get() = unnamedCommands.isEmpty() && noHelpCommands.isEmpty() && multipleHelpCommands.isEmpty()

        override fun unnamedCommand(command: ICommand) {
            unnamedCommands += command.toString()
        }

        override fun noHelpCommand(command: ICommand, names: List<String>) {
            noHelpCommands += command.toString()
        }

        override fun multipleHelpCommand(command: ICommand, names: List<String>) {
            multipleHelpCommands += command.toString()
        }
    }

    init {
        registry.listener = listener
    }

    fun createCommands() {
        scanResult.getClassesImplementing("net.notjustanna.psi.ICommand")
            .filter { it.hasAnnotation("net.notjustanna.psi.Command") }
            .loadClasses(ICommand::class.java)
            .forEach {
                try {
                    val meta = it.getAnnotation(classOf<Command>())
                    val command = kodein.jitInstance(it)
                    registry.register(meta.value.toList(), command)
                    processExecutable(command)
                } catch (e: Exception) {
                    logger.error(e) { "Error while registering $it" }
                }
            }
    }

    fun createProviders() {
        scanResult.getClassesImplementing("net.notjustanna.psi.ICommandProvider")
            .filter { it.hasAnnotation("net.notjustanna.psi.CommandProvider") }
            .loadClasses(ICommandProvider::class.java)
            .forEach {
                try {
                    val provider = kodein.jitInstance(it)
                    provider.provide(registry)
                    processExecutable(provider)
                } catch (e: Exception) {
                    logger.error(e) { "Error while registering commands through $it" }
                }
            }
    }

    fun createStandalones() {
        scanResult.getClassesImplementing("net.notjustanna.psi.Executable")
            .filter {
                allOf(
                    arrayOf(
                        "net.notjustanna.core.RunAtStartup",
                        "net.notjustanna.core.RunEvery"
                    ).any(it::hasAnnotation),
                    arrayOf(
                        "net.notjustanna.core.ICommand",
                        "net.notjustanna.core.ICommandProvider"
                    ).none(it::implementsInterface)
                )
            }
            .loadClasses(Executable::class.java)
            .forEach {
                try {
                    processExecutable(kodein.jitInstance(it))
                } catch (e: Exception) {
                    logger.error(e) { "Error while executing $it" }
                }
            }
    }

    fun reportResults() {
        if (!listener.clean) {
            val log: DiscordLogger by kodein.instance()
            log.embed {
                author("Command Registry Report")
                color(Colors.discordYellow)


                if (listener.unnamedCommands.isNotEmpty()) {
                    field(
                        "Unnamed Commands:",
                        listener.unnamedCommands.joinToString("\n- ", "- ")
                    )
                }

                if (listener.noHelpCommands.isNotEmpty()) {
                    field(
                        "Commands without a help interface:",
                        listener.noHelpCommands.joinToString("\n- ", "- ")
                    )
                }

                if (listener.multipleHelpCommands.isNotEmpty()) {
                    field(
                        "Commands with multiple help interfaces:",
                        listener.multipleHelpCommands.joinToString("\n- ", "- ")
                    )
                }

                timestamp(OffsetDateTime.now())
            }
        }
    }

    private fun processExecutable(it: Any) {
        if (it is Executable) {
            when {
                it.javaClass.isAnnotationPresent(classOf<RunEvery>()) -> {
                    val meta = it.javaClass.getAnnotation(classOf<RunEvery>())
                    task(meta.amount, meta.unit, meta.initialDelay, it.simpleName + meta, it::run)
                }
                it.javaClass.isAnnotationPresent(classOf<RunAtStartup>()) -> {
                    queue("${it.simpleName}@RunAtStartup", it::run)
                }
                else -> {
                    logger.warn { "Error: $it is an Executable but lacks an annotation" }
                }
            }
        }
    }

    private val Any.simpleName get() = javaClass.simpleName
}
