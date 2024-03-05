package gdx.liftoff.preferences

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves project name. The project name can be any valid folder name.
 */
@Property("Name")
@Suppress("unused") // Referenced via reflection.
class NamePreference : AbstractStringPreference() {
  override fun getDefault(): String {
    return "ExampleGame"
  }
}
