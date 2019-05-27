package com.github.czyzby.setup.prefs

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves main class name. Main core's project class name is usually generic, like "Core", "Main" or "Root".
 * @author MJ
 */
@Property("MainClass")
class MainClassPreference : AbstractStringPreference() {
}