package gdx.liftoff.preferences

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves GWT framework version. This setting is likely to be the same for multiple projects.
 */
@Property("GwtVersion")
@Suppress("unused") // Referenced via reflection.
class GwtVersionPreference : AbstractStringPreference() {
  override fun getDefault(): String = "2.11.0"
}
