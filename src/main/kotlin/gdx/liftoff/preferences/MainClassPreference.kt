package gdx.liftoff.preferences

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves main class name. Main core's project class name is usually generic, like "Core", "Main" or "Root".
 */
@Property("MainClass")
@Suppress("unused") // Referenced via reflection.
class MainClassPreference : AbstractStringPreference() {
  override fun getDefault(): String {
    return "Main"
  }
}
