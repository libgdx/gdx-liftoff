package com.github.czyzby.setup.views

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.utils.ObjectMap
import com.github.czyzby.autumn.annotation.Processor
import com.github.czyzby.autumn.context.Context
import com.github.czyzby.autumn.context.ContextDestroyer
import com.github.czyzby.autumn.context.ContextInitializer
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor
import com.github.czyzby.lml.annotation.LmlActor
import com.github.czyzby.lml.parser.LmlParser
import com.github.czyzby.setup.data.libs.Library
import com.kotcrab.vis.ui.widget.VisTextField


/**
 * Holds data about official and third-party extensions.
 * @author MJ
 */
@Processor
class ExtensionsData : AbstractAnnotationProcessor<Extension>() {
    val official = mutableListOf<Library>()
    val thirdParty = mutableListOf<Library>()

    @LmlActor("\$officialExtensions") private lateinit var officialButtons: ObjectMap<String, Button>
    @LmlActor("\$thirdPartyExtensions") private lateinit var thirdPartyButtons: ObjectMap<String, Button>
    private val thirdPartyVersions = ObjectMap<String, VisTextField>()

    fun assignVersions(parser: LmlParser) {
        thirdParty.forEach {
            thirdPartyVersions.put(it.id,
                    parser.actorsMappedByIds.get(it.id + "Version") as VisTextField)
        }
    }

    fun getVersion(libraryId: String): String = thirdPartyVersions.get(libraryId).text

    fun getSelectedOfficialExtensions(): Array<Library> = official.filter { officialButtons.get(it.id).isChecked }.toTypedArray()
    fun getSelectedThirdPartyExtensions(): Array<Library> = thirdParty.filter { thirdPartyButtons.get(it.id).isChecked }.toTypedArray()

    fun hasExtensionSelected(id: String) : Boolean = (officialButtons.containsKey(id) && officialButtons.get(id).isChecked) || (thirdPartyButtons.containsKey(id) && thirdPartyButtons.get(id).isChecked)
    // Automatic scanning of extensions:

    override fun getSupportedAnnotationType(): Class<Extension> = Extension::class.java
    override fun isSupportingTypes(): Boolean = true
    override fun processType(type: Class<*>, annotation: Extension, component: Any, context: Context,
                             initializer: ContextInitializer, contextDestroyer: ContextDestroyer) {
        if (annotation.official) {
            official.add(component as Library)
        } else {
            thirdParty.add(component as Library)
        }
    }
}

/**
 * Should annotate all third-party extensions.
 * @author MJ
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Extension(val official: Boolean = false)
