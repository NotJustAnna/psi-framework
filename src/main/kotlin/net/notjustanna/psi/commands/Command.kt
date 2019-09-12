package net.notjustanna.psi.commands

/**
 * ### [RegistryBootstrap][net.notjustanna.psi.bootstrap.RegistryBootstrap] annotation
 *
 * [ICommand] classes annotated with this class will be injected at the
 * [RegistryBootstrap][net.notjustanna.psi.bootstrap.RegistryBootstrap] and registered at the
 * [CommandRegistry][net.notjustanna.psi.commands.manager.CommandRegistry].

 * @param value the command's names to register into the command registry.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Command(vararg val value: String)

