package net.notjustanna.psi.commands

/**
 * ### [CommandBootstrap][net.notjustanna.psi.bootstrap.CommandBootstrap] annotation
 *
 * [ICategory] classes annotated with this class will be injected at the
 * [CommandBootstrap][net.notjustanna.psi.bootstrap.CommandBootstrap] and registered at the
 * [CommandRegistry][net.notjustanna.psi.commands.manager.CommandRegistry].
 *
 * Alternatively, [ICategory] enum classes annotated with this class will get all
 * the enum constants registered at the [CommandRegistry][net.notjustanna.psi.commands.manager.CommandRegistry],
 * with the name `${annotation.value}#${enum.name.toLowerCase()}`
 *
 * [ICommand] and [ICommandProvider] classes annotated this class will be injected
 * with the [ICategory] of the value set in this annotation.
 *
 * @param value the command's names to register into the command registry.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Category(val value: String)