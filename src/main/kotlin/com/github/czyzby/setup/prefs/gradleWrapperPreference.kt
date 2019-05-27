package com.github.czyzby.setup.prefs

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.github.czyzby.autumn.mvc.component.preferences.dto.AbstractPreference
import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Marks whether Gradle wrapper should be included.
 * @author MJ
 */
@Property("GradleWrapper")
class GradleWrapperPreference : AbstractPreference<Boolean>() {
    override fun getDefault(): Boolean = true

    override fun extractFromActor(actor: Actor): Boolean {
        if (actor is Button) {
            return actor.isChecked
        }
        throw UnsupportedOperationException("Cannot extract value from: " + actor)
    }

    override fun convert(rawPreference: String): Boolean = rawPreference.toBoolean()
    override fun serialize(preference: Boolean): String = preference.toString()
}
