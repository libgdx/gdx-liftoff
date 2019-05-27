package com.github.czyzby.setup.prefs

import com.github.czyzby.autumn.mvc.stereotype.preference.Property
import com.github.czyzby.kiwi.util.common.Strings

/**
 * These tasks will be run after the project generation.
 * @author MJ
 */
@Property("GradleTasks")
class GradleTasksPreference : AbstractStringPreference() {
    override fun serialize(preference: String): String = preference.split(Regex(Strings.WHITESPACE_SPLITTER_REGEX))
            .filter { it.isNotBlank() }.joinToString(separator = " ")
}
