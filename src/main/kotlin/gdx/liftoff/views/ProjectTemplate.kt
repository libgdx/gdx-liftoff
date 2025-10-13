package gdx.liftoff.views

/**
 * Should annotate all project templates. Marks if the template is official.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProjectTemplate(val official: Boolean = false)
