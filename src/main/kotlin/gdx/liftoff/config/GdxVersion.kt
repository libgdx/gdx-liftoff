package gdx.liftoff.config

/**
 * Used to represent and compare libGDX versions using major.minor.revision schema.
 */
data class GdxVersion(
  val major: Int,
  val minor: Int,
  val revision: Int,
) : Comparable<GdxVersion> {
  override operator fun compareTo(other: GdxVersion) = compareValuesBy(this, other, GdxVersion::major, GdxVersion::minor, GdxVersion::revision)

  companion object {
    fun parseGdxVersion(version: String): GdxVersion? {
      val trimmed: String = version.trim().removeSuffix("-SNAPSHOT")
      val parts: List<Int?> = trimmed.split('.').map(String::toIntOrNull)
      // mumble mumble klint
      return if (parts.size < 3 || parts.any { it == null }) null
      else GdxVersion(parts[0]!!, parts[1]!!, parts[2]!!)
    }
  }
}
