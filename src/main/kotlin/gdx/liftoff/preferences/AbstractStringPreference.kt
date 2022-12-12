package gdx.liftoff.preferences

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.utils.GdxRuntimeException
import com.github.czyzby.autumn.mvc.component.preferences.dto.AbstractPreference
import com.github.czyzby.kiwi.util.common.Strings
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.spinner.Spinner

/**
 * Abstract base for all application's preferences.
 */
open class AbstractStringPreference : AbstractPreference<String>() {
  override fun extractFromActor(actor: Actor): String {
    return when (actor) {
      is VisTextField -> actor.text
      is Spinner -> actor.model.text
      is SelectBox<*> -> if (actor.selectedIndex > 0) actor.selected.toString() else get()
      else -> throw GdxRuntimeException("Actor type unsupported: " + actor.javaClass)
    }
  }

  override fun getDefault(): String = Strings.EMPTY_STRING

  override fun serialize(preference: String): String = preference

  override fun convert(rawPreference: String): String = rawPreference
}

// Side note: there is no preference for project name, as it is likely to be unique. There is also no preference
// for project's destination folder, but file chooser supports saving default folders, so one can easily remember
// the path to this workspace.
