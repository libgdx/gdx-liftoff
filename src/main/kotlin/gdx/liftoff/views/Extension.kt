package gdx.liftoff.views

/**
 * Should annotate all third-party extensions.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Extension(val official: Boolean = false)
