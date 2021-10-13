package com.github.czyzby.setup.views.dialogs

import com.github.czyzby.autumn.annotation.Inject
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog
import com.github.czyzby.lml.annotation.LmlAction
import com.github.czyzby.lml.parser.action.ActionContainer
import com.github.czyzby.setup.views.MainView

/**
 * Allows to clear destination folder.
 */
@ViewDialog(id = "deletePrompt", value = "templates/dialogs/delete.lml", cacheInstance = true)
@Suppress("unused") // Referenced via reflection.
class DeletionPrompt : ActionContainer {
    @Inject private lateinit var interfaceService: InterfaceService
    @Inject private lateinit var mainView: MainView

    @LmlAction("delete")
    fun clearFolder() {
        val folder = mainView.getDestination()
        try {
            folder.deleteDirectory()
            folder.mkdirs()
        } catch(exception: Exception) {
            exception.printStackTrace()
            interfaceService.showDialog(DeletionError::class.java)
        }
        mainView.revalidateForm()
    }
}

/**
 * Deletion error dialog. Shown if an unexpected exception is thrown during folder clearing.
 */
@ViewDialog(id = "deleteError", value = "templates/dialogs/deleteError.lml", cacheInstance = true) class DeletionError
