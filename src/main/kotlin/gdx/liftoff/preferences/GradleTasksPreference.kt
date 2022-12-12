package gdx.liftoff.preferences

import com.github.czyzby.autumn.mvc.stereotype.preference.Property
import com.github.czyzby.kiwi.util.common.Strings

/**
 * These tasks will be run after the project generation.
 */
@Property("GradleTasks")
@Suppress("unused") // Referenced via reflection.
class GradleTasksPreference : AbstractStringPreference() {
  override fun serialize(preference: String): String = preference.split(Regex(Strings.WHITESPACE_SPLITTER_REGEX))
    .filter { it.isNotBlank() }.joinToString(separator = " ")
}
