package net.notjustanna.psi.bootstrap

import io.github.classgraph.ScanResult
import mu.KLogging
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import net.notjustanna.libs.kodein.jit.jitInstance
import net.notjustanna.psi.commands.*
import net.notjustanna.psi.commands.manager.CommandRegistry
import net.notjustanna.psi.executor.Executable
import net.notjustanna.psi.executor.RunAtStartup
import net.notjustanna.psi.executor.RunEvery
import net.notjustanna.psi.executor.service.TaskExecutorService
import net.notjustanna.utils.extensions.lang.allOf

class CommandBootstrap(private val scanResult: ScanResult, private val kodein: Kodein) {
    companion object : KLogging()

    private val tasks: TaskExecutorService by kodein.instance()
    private val registry: CommandRegistry by kodein.instance()

    fun createCategories() {
        scanResult.getClassesImplementing("net.notjustanna.psi.commands.ICategory")
            .filter { it.hasAnnotation("net.notjustanna.psi.commands.Category") }
            .loadClasses(ICategory::class.java)
            .forEach {
                try {
                    val meta = it.getAnnotation(Category::class.java)
                    if (it.isEnum) {
                        for (category in it.enumConstants) {
                            registry.registerCategory(
                                "${meta.value}#${(category as Enum<*>).name.toLowerCase()}", category
                            )
                            processExecutable(category)
                        }
                    } else {
                        val category = kodein.jitInstance(it)
                        registry.registerCategory(meta.value, category)
                        processExecutable(category)
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Error while registering $it" }
                }
            }
    }

    fun createCommands() {
        scanResult.getClassesImplementing("net.notjustanna.psi.commands.ICommand")
            .filter { it.hasAnnotation("net.notjustanna.psi.commands.Command") }
            .loadClasses(ICommand::class.java)
            .forEach {
                try {
                    // command metadata
                    val meta = it.getAnnotation(Command::class.java)

                    // injectable category
                    val category = it.getAnnotation(Category::class.java)?.value?.let(registry.categories::get)

                    val command = kodein.maybeInject(category).jitInstance(it)
                    registry.registerCommand(meta.value.toList(), command)
                    processExecutable(command)
                } catch (e: Exception) {
                    logger.error(e) { "Error while registering $it" }
                }
            }
    }

    fun createProviders() {
        scanResult.getClassesImplementing("net.notjustanna.psi.commands.ICommandProvider")
            .filter { it.hasAnnotation("net.notjustanna.psi.commands.CommandProvider") }
            .loadClasses(ICommandProvider::class.java)
            .forEach {
                try {
                    // injectable category
                    val category = it.getAnnotation(Category::class.java)?.value?.let(registry.categories::get)

                    val provider = kodein.maybeInject(category).jitInstance(it)
                    provider.provide(registry)
                    processExecutable(provider)
                } catch (e: Exception) {
                    logger.error(e) { "Error while registering commands through $it" }
                }
            }
    }

    fun createStandalones() {
        scanResult.getClassesImplementing("net.notjustanna.psi.executor.Executable")
            .filter {
                allOf(
                    arrayOf(
                        "net.notjustanna.psi.executor.RunAtStartup",
                        "net.notjustanna.psi.executor.RunEvery"
                    ).any(it::hasAnnotation),
                    arrayOf(
                        "net.notjustanna.psi.commands.ICategory",
                        "net.notjustanna.psi.commands.ICommand",
                        "net.notjustanna.psi.commands.ICommandProvider"
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

    private fun Kodein.maybeInject(category: ICategory?): Kodein {
        if (category != null) {
            return Kodein {
                extend(this@maybeInject)
                bind<ICategory>() with instance(category)
            }
        }
        return this
    }

    private fun processExecutable(it: Any) {
        if (it is Executable) {
            when {
                it.javaClass.isAnnotationPresent(RunEvery::class.java) -> {
                    val meta = it.javaClass.getAnnotation(RunEvery::class.java)
                    tasks.task(meta.amount, meta.unit, meta.initialDelay, it.simpleName + meta, it::run)
                }
                it.javaClass.isAnnotationPresent(RunAtStartup::class.java) -> {
                    tasks.queue("${it.simpleName}@RunAtStartup", it::run)
                }
                else -> {
                    logger.warn { "Error: $it is an Executable but lacks an annotation" }
                }
            }
        }
    }

    private val Any.simpleName get() = javaClass.simpleName
}
