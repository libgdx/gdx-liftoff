package gdx.liftoff.preferences

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves Android SDK path. Needless to say, this setting is unlikely to change once set.
 */
@Property("AndroidSdk")
@Suppress("unused") // Referenced via reflection.
class AndroidSdkPreference : AbstractStringPreference() {
  override fun getDefault(): String {
    return System.getenv("ANDROID_SDK_ROOT").orEmpty()
  }
}
