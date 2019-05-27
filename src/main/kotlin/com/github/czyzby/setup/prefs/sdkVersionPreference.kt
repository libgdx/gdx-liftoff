package com.github.czyzby.setup.prefs

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves Android SDK version. This setting is likely to be the same for multiple projects.
 * @author MJ
 */
@Property("SdkVersion")
class SdkVersionPreference : AbstractStringPreference() {
    override fun getDefault(): String = "25"
}