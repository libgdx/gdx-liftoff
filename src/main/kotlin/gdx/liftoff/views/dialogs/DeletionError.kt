package gdx.liftoff.views.dialogs

import com.github.czyzby.autumn.mvc.stereotype.ViewDialog

/**
 * Deletion error dialog. Shown if an unexpected exception is thrown during folder clearing.
 */
@ViewDialog(id = "deleteError", value = "templates/dialogs/deleteError.lml", cacheInstance = true)
class DeletionError
