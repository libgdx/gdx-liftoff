package gdx.liftoff.config

/**
 * Used to represent and compare libGDX versions using major.minor.revision schema.
 */
data class GdxVersion(
  val major: Int,
  val minor: Int,
  val revision: Int
) : Comparable<GdxVersion> {
  override operator fun compareTo(other: GdxVersion) =
    compareValuesBy(this, other, { it.major }, { it.minor }, { it.revision })

  companion object {
    fun parseGdxVersion(version: String): GdxVersion? {
      val trimmed = version.trim().removeSuffix("-SNAPSHOT")
      val parts = trimmed.split('.')
      return if (parts.size < 3 || parts.any { it.toIntOrNull() == null }) {
        null
      } else {
        GdxVersion(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
      }
    }
  }
}
