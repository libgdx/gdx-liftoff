package gdx.liftoff.config

import java.lang.NumberFormatException

/**
 * Configures Autumn MVC application.
 */
@Suppress("unused") // Fields accessed via reflection.
class Configuration {
  companion object {
    const val VERSION = "1.13.5.3"
    const val WIDTH = 600
    const val HEIGHT = 700
    const val PREFERENCES_PATH = "gdx-liftoff-prefs"

    fun parseJavaVersion(version: String): Double {
      val d =
        try {
          version.removePrefix("1.").removeSuffix(".0").toDouble()
        } catch (nfe: NumberFormatException) {
          8.0
        }
      return d
    }
  }
}
