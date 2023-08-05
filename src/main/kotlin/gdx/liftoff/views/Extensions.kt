package gdx.liftoff.views

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.utils.ObjectMap
import com.github.czyzby.autumn.annotation.Processor
import com.github.czyzby.autumn.context.Context
import com.github.czyzby.autumn.context.ContextDestroyer
import com.github.czyzby.autumn.context.ContextInitializer
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor
import com.github.czyzby.lml.annotation.LmlActor
import gdx.liftoff.config.inject
import gdx.liftoff.data.libraries.Library
import gdx.liftoff.data.project.ExtensionsData

/**
 * Holds data about official and third-party extensions.
 */
@Processor
class ExtensionsView : AbstractAnnotationProcessor<Extension>() {
  // Filled by the annotation processor.
  val extensionsById = mutableMapOf<String, Library>()

  val official = mutableListOf<Library>()
  val thirdParty = mutableListOf<Library>()

  @LmlActor("\$officialExtensions")
  private val officialButtons: ObjectMap<String, Button> = inject()

  @LmlActor("\$thirdPartyExtensions")
  private val thirdPartyButtons: ObjectMap<String, Button> = inject()

  private fun getSelectedOfficialExtensions(): List<Library> = official.filter { officialButtons.get(it.id).isChecked }
  private fun getSelectedThirdPartyExtensions(): List<Library> = thirdParty.filter { thirdPartyButtons.get(it.id).isChecked }

  fun exportData(): ExtensionsData = ExtensionsData(
    officialExtensions = getSelectedOfficialExtensions(),
    thirdPartyExtensions = getSelectedThirdPartyExtensions()
  )

  // Automatic scanning of extensions:
  override fun getSupportedAnnotationType(): Class<Extension> = Extension::class.java
  override fun isSupportingTypes(): Boolean = true
  override fun processType(
    type: Class<*>,
    annotation: Extension,
    component: Any,
    context: Context,
    initializer: ContextInitializer,
    contextDestroyer: ContextDestroyer
  ) {
    val library = component as Library
    if (annotation.official) { official } else { thirdParty }.add(library)
    extensionsById[library.id] = library
  }
}

/**
 * Should annotate all third-party extensions.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Extension(val official: Boolean = false)
