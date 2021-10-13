package gdx.liftoff.config

import com.github.czyzby.autumn.mvc.component.ui.InterfaceService

/**
 * Initializer for properties that store objects injected using reflection.
 * An alternative to `lateinit` variables that allows to define read-only properties.
 *
 * This function should be used only by classes that are registered as Autumn components
 * with appropriate annotations, or are filled by the LML parser with actor references.
 * Properties using this utility function must be annotated with annotations such as
 * [com.github.czyzby.autumn.annotation.Inject] or [com.github.czyzby.lml.annotation.LmlInject].
 */
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> inject(): T = null as T

/**
 * Uses Kotlin reified type to display LML dialogs.
 */
inline fun <reified T : Any> InterfaceService.showDialog() = showDialog(T::class.java)
