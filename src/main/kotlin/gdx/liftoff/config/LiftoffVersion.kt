package gdx.liftoff.config

/**
 * Used to represent and compare gdx-liftoff versions using major.minor.revision.liftoff schema.
 */
data class LiftoffVersion(
  val major: Int,
  val minor: Int,
  val revision: Int,
  val liftoff: Int,
) : Comparable<LiftoffVersion> {
  override operator fun compareTo(other: LiftoffVersion) = compareValuesBy(this, other, LiftoffVersion::major, LiftoffVersion::minor, LiftoffVersion::revision, LiftoffVersion::liftoff)

  companion object {
    @JvmStatic
    fun parseLiftoffVersion(version: String): LiftoffVersion? {
      val trimmed: String = version.trim().removeSuffix("-SNAPSHOT")
      val parts: List<Int?> = trimmed.split('.').map(String::toIntOrNull)
      // mumble mumble klint
      return if (parts.size < 4 || parts.any { it == null })
        null
      else
        LiftoffVersion(parts[0]!!, parts[1]!!, parts[2]!!, parts[3]!!)
    }
  }
}
