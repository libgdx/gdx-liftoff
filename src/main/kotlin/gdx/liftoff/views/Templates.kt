package gdx.liftoff.views

import com.github.czyzby.autumn.annotation.Processor
import com.github.czyzby.autumn.context.Context
import com.github.czyzby.autumn.context.ContextDestroyer
import com.github.czyzby.autumn.context.ContextInitializer
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor
import com.github.czyzby.lml.annotation.LmlActor
import com.github.czyzby.lml.scene2d.ui.reflected.ButtonTable
import gdx.liftoff.config.inject
import gdx.liftoff.data.templates.Template

/**
 * Handles templates tab.
 */
@Processor
class TemplatesView : AbstractAnnotationProcessor<ProjectTemplate>() {
  // Filled by the annotation processor.
  private val templates = mutableListOf<Template>()

  val officialTemplates = mutableListOf<Template>()
  val thirdPartyTemplates = mutableListOf<Template>()

  @LmlActor("templatesTable")
  private val templatesTable: ButtonTable = inject()

  fun getSelectedTemplate(): Template = templates.first { it.id == templatesTable.buttonGroup.checked.name }

  // Automatic scanning of project templates:

  override fun getSupportedAnnotationType(): Class<ProjectTemplate> = ProjectTemplate::class.java
  override fun isSupportingTypes(): Boolean = true
  override fun processType(
    type: Class<*>,
    annotation: ProjectTemplate,
    component: Any,
    context: Context,
    initializer: ContextInitializer,
    contextDestroyer: ContextDestroyer
  ) {
    val template = component as Template
    templates.add(template)
    if (annotation.official) {
      officialTemplates
    } else {
      thirdPartyTemplates
    }.add(template)
  }
}

/**
 * Should annotate all project templates. Marks if the template is official.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProjectTemplate(val official: Boolean = false)
