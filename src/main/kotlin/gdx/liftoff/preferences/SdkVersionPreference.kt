package gdx.liftoff.preferences

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves Android SDK version. This setting is likely to be the same for multiple projects.
 */
@Property("SdkVersion")
class SdkVersionPreference : AbstractStringPreference() {
  override fun getDefault(): String = "30"
}
