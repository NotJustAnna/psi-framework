package net.notjustanna.psi.commands

/**
 * ### [RegistryBootstrap][net.notjustanna.psi.bootstrap.RegistryBootstrap] annotation
 *
 * [IRegistryInjector] classes annotated with this class will be injected at the
 * [RegistryBootstrap][net.notjustanna.psi.bootstrap.RegistryBootstrap] and the [IRegistryInjector.provide]
 * method will be injected with the [CommandRegistry][net.notjustanna.psi.commands.manager.CommandRegistry].
 *
 * @param value the phase which this registry extension should be injected and initialized.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RegistryInjector(val value: RegistryPhase)