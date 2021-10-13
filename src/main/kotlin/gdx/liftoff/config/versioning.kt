package gdx.liftoff.config

/**
 * Used to represent and compare libGDX versions using major.minor.revision schema.
 */
data class LibGdxVersion(
    val major: Int,
    val minor: Int,
    val revision: Int
) : Comparable<LibGdxVersion> {
    override operator fun compareTo(other: LibGdxVersion) =
            compareValuesBy(this, other, { it.major }, { it.minor }, { it.revision })

    companion object {
        fun parseLibGdxVersion(version: String): LibGdxVersion? {
            val trimmed = version.trim().removeSuffix("-SNAPSHOT")
            val parts = trimmed.split('.')
            return if (parts.size < 3 || parts.any { it.toIntOrNull() == null }) {
                null
            } else {
                LibGdxVersion(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            }
        }
    }
}

