package net.notjustanna.psi.bootstrap

import io.github.classgraph.ScanResult
import mu.KLogging
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
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

class RegistryBootstrap(private val scanResult: ScanResult, override val kodein: Kodein) : KodeinAware {
    companion object : KLogging()

    private val tasks: TaskExecutorService by instance()
    private val registry: CommandRegistry by instance()
    private val errorHandler: ErrorHandler by instance()

    private val injectors by lazy {
        scanResult.getClassesImplementing(IRegistryInjector::class.qualifiedName)
            .filter { it.hasAnnotation(RegistryInjector::class.qualifiedName) }
            .loadClasses(IRegistryInjector::class.java)
            .groupBy { it.getAnnotation(RegistryInjector::class.java).value }
    }

    fun createCategories() {
        scanResult.getClassesImplementing(ICategory::class.qualifiedName)
            .filter { it.hasAnnotation(Category::class.qualifiedName) }
            .loadClasses(ICategory::class.java)
            .forEach {
                runCatching {
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
                }.onFailure { t -> errorHandler.onCategoryCreation(it, t) }
            }
    }

    fun createCommands() {
        scanResult.getClassesImplementing(ICommand::class.qualifiedName)
            .filter { it.hasAnnotation(Command::class.qualifiedName) }
            .loadClasses(ICommand::class.java)
            .forEach {
                runCatching {
                    // command metadata
                    val meta = it.getAnnotation(Command::class.java)

                    // injectable category
                    val category = it.getAnnotation(Category::class.java)?.value?.let(registry::category)

                    val command = kodein.maybeInject(category).jitInstance(it)
                    registry.registerCommand(meta.value.toList(), command)
                    processExecutable(command)
                }.onFailure { t -> errorHandler.onCommandCreation(it, t) }
            }
    }

    fun loadInjectors(phase: RegistryPhase) {
        injectors[phase]?.forEach {
            runCatching {
                // injectable category
                val category = it.getAnnotation(Category::class.java)?.value?.let(registry::category)

                val provider = kodein.maybeInject(category).jitInstance(it)
                provider.inject(registry)
                processExecutable(provider)
            }.onFailure { t -> errorHandler.onRegistryInjectorCreation(it, phase, t) }
        }
    }

    fun createStandalones() {
        scanResult.getClassesImplementing(Executable::class.qualifiedName)
            .filter {
                allOf(
                    arrayOf(
                        RunAtStartup::class.qualifiedName,
                        RunEvery::class.qualifiedName
                    ).any(it::hasAnnotation),
                    arrayOf(
                        ICategory::class.qualifiedName,
                        ICommand::class.qualifiedName,
                        IRegistryInjector::class.qualifiedName
                    ).none(it::implementsInterface)
                )
            }
            .loadClasses(Executable::class.java)
            .forEach {
                runCatching {
                    processExecutable(kodein.jitInstance(it))
                }.onFailure { t -> errorHandler.onExecutableCreation(it, t) }
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
