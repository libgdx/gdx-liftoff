package com.github.czyzby.setup.prefs

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves Android build tools version. This setting is likely to be the same for multiple projects.
 * @author MJ
 */
@Property("ToolsVersion")
class ToolsVersionPreference : AbstractStringPreference() {
    override fun getDefault(): String = "25.0.2"
}