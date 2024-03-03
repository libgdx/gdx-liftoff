package gdx.liftoff.preferences

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves package name. Package name is usually company-dependent and often shared (at least partially) across
 * multiple projects.
 */
@Property("Package")
@Suppress("unused") // Referenced via reflection.
class PackagePreference : AbstractStringPreference() {
  override fun getDefault(): String {
    return "com.libgdx.example"
  }
}
