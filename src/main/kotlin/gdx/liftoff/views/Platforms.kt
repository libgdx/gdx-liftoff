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
import gdx.liftoff.config.inject
import gdx.liftoff.data.platforms.Platform

/**
 * Handles platform-related input.
 */
@Processor
class PlatformsView : AbstractAnnotationProcessor<GdxPlatform>() {
  // Filled by the annotation processor.
  val platforms = mutableMapOf<String, Platform>()

  @LmlActor("androidSdk")
  private val androidSdk: Disableable = inject()

  @LmlActor("androidSdkButton")
  private val androidSdkButton: Disableable = inject()

  @LmlActor("\$platforms")
  private val platformButtons: ObjectSet<Button> = inject()

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
  override fun processType(
    type: Class<*>,
    annotation: GdxPlatform,
    component: Any,
    context: Context,
    initializer: ContextInitializer,
    contextDestroyer: ContextDestroyer
  ) {
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
