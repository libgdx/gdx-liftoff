package gdx.liftoff.views

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.ObjectSet
import com.github.czyzby.autumn.annotation.Processor
import com.github.czyzby.autumn.context.Context
import com.github.czyzby.autumn.context.ContextDestroyer
import com.github.czyzby.autumn.context.ContextInitializer
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor
import com.github.czyzby.lml.annotation.LmlActor
import com.github.czyzby.lml.parser.LmlParser
import com.kotcrab.vis.ui.widget.VisTextField
import gdx.liftoff.config.inject
import gdx.liftoff.data.languages.Language
import gdx.liftoff.data.project.LanguagesData

/**
 * Holds additional JVM languages data.
 */
@Processor
class LanguagesView : AbstractAnnotationProcessor<JvmLanguage>() {
  // Filled by the annotation processor.
  private val jvmLanguages = mutableMapOf<String, Language>()

  @LmlActor("\$jvmLanguages")
  val languageButtons: ObjectSet<Button> = inject()
  private val languageVersions = ObjectMap<String, VisTextField>()

  val languages: Array<String>
    get() = jvmLanguages.values.map { it.id }.sorted().toTypedArray()

  val versions: Array<String>
    get() = jvmLanguages.values.sortedBy { it.id }.map { it.version }.toTypedArray()

  fun assignVersions(parser: LmlParser) {
    jvmLanguages.values.forEach {
      languageVersions.put(
        it.id,
        parser.actorsMappedByIds.get(it.id + "Version") as VisTextField
      )
    }
  }

  private fun getSelectedLanguages(): List<Language> = languageButtons.filter { it.isChecked }.map {
    jvmLanguages[it.name]!!
  }.toList()

  fun exportData(): LanguagesData {
    val languages = getSelectedLanguages()
    return LanguagesData(
      list = languages.toMutableList(),
      versions = languageVersions.associate { it.key to it.value.text }
    )
  }

  override fun getSupportedAnnotationType(): Class<JvmLanguage> = JvmLanguage::class.java
  override fun isSupportingTypes(): Boolean = true
  override fun processType(
    type: Class<*>,
    annotation: JvmLanguage,
    component: Any,
    context: Context,
    initializer: ContextInitializer,
    contextDestroyer: ContextDestroyer
  ) {
    val language = component as Language
    jvmLanguages[language.id] = language
  }
}

/**
 * Should annotate all additional JVM languages.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JvmLanguage
