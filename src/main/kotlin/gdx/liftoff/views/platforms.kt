package gdx.liftoff.views

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.Disableable
import com.badlogic.gdx.utils.ObjectSet
import com.github.czyzby.autumn.annotation.Processor
import com.github.czyzby.autumn.context.Context
import com.github.czyzby.autumn.context.ContextDestroyer
import com.github.czyzby.autumn.context.ContextInitializer
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor
import com.github.czyzby.lml.annotation.LmlActor
import gdx.liftoff.data.platforms.Platform

/**
 * Handles platform-related input.
 */
@Processor
class PlatformsData : AbstractAnnotationProcessor<GdxPlatform>() {
    val platforms = mutableMapOf<String, Platform>()

    @LmlActor("androidSdk") private lateinit var androidSdk: Disableable
    @LmlActor("androidSdkButton") private lateinit var androidSdkButton: Disableable
    @LmlActor("\$platforms") private lateinit var platformButtons: ObjectSet<Button>

    fun toggleAndroidPlatform(active: Boolean) {
        androidSdk.isDisabled = !active
        androidSdkButton.isDisabled = !active
    }

    operator fun get(platformId: String): Platform = platforms[platformId]!!

    fun getSelectedPlatforms(): Map<String, Platform> =
            platformButtons.filter { it.isChecked }.map { platforms[it.name]!! }.associateBy { it.id }

    // Automatic scanning of platforms:

    override fun getSupportedAnnotationType(): Class<GdxPlatform> = GdxPlatform::class.java
    override fun isSupportingTypes(): Boolean = true
    override fun processType(type: Class<*>, annotation: GdxPlatform, component: Any, context: Context,
                             initializer: ContextInitializer, contextDestroyer: ContextDestroyer) {
        val platform = component as Platform
        platforms[platform.id] = platform
    }
}

/**
 * Should annotate all libGDX platforms.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class GdxPlatform
