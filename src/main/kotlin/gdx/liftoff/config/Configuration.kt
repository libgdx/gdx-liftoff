package gdx.liftoff.config

import java.lang.NumberFormatException

/**
 * Configures Autumn MVC application.
 */
object Configuration {
  const val VERSION = "1.14.0.6-SNAPSHOT"

  @JvmStatic
  fun parseJavaVersion(version: String): Double = try {
    version.removePrefix("1.").removeSuffix(".0").toDouble()
  } catch (_: NumberFormatException) {
    8.0
  }
}
