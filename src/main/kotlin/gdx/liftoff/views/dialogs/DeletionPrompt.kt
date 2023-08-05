package gdx.liftoff.views.dialogs

import com.badlogic.gdx.Gdx
import com.github.czyzby.autumn.annotation.Inject
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog
import com.github.czyzby.lml.annotation.LmlAction
import com.github.czyzby.lml.parser.action.ActionContainer
import gdx.liftoff.config.inject
import gdx.liftoff.config.showDialog
import gdx.liftoff.views.MainView

/**
 * Prompts the user whether to clear the destination folder.
 */
@ViewDialog(id = "deletePrompt", value = "templates/dialogs/delete.lml", cacheInstance = true)
@Suppress("unused") // Referenced via reflection.
class DeletionPrompt : ActionContainer {
  @Inject private val interfaceService: InterfaceService = inject()

  @Inject private val mainView: MainView = inject()

  @LmlAction("delete")
  fun clearFolder() {
    val folder = mainView.getDestination()
    try {
      folder.deleteDirectory()
      folder.mkdirs()
    } catch (exception: Exception) {
      Gdx.app.error("gdx-liftoff", "Unable to clear folder: $folder", exception)
      interfaceService.showDialog<DeletionError>()
    }
    mainView.revalidateForm()
  }
}
