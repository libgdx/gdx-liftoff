package gdx.liftoff.views.dialogs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import com.badlogic.gdx.utils.ObjectSet
import com.github.czyzby.autumn.annotation.Inject
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog
import com.github.czyzby.lml.annotation.LmlAction
import com.github.czyzby.lml.annotation.LmlActor
import com.github.czyzby.lml.parser.action.ActionContainer
import com.kotcrab.vis.ui.widget.Tooltip
import gdx.liftoff.config.inject
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.project.ProjectLogger
import gdx.liftoff.views.MainView
import gdx.liftoff.views.widgets.ScrollableTextArea
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.properties.Delegates

/**
 * Displayed after generation request was sent.
 */
@ViewDialog(id = "generation", value = "templates/dialogs/generation.lml", cacheInstance = false)
@Suppress("unused") // Referenced via reflection.
class GenerationPrompt : ViewDialogShower, ProjectLogger, ActionContainer {
  private lateinit var intellijPath: String
  private var intellijIsFlatpak by Delegates.notNull<Boolean>()

  @Inject private val locale: LocaleService = inject()

  @Inject private val mainView: MainView = inject()

  @LmlActor("close", "exit")
  private val buttons: ObjectSet<Button> = inject()

  @LmlActor("console")
  private val console: ScrollableTextArea = inject()

  @LmlActor("scroll")
  private val scrollPane: ScrollPane = inject()

  @LmlActor("idea")
  private val ideaButton: Button = inject()

  private val loggingBuffer = ConcurrentLinkedQueue<String>()

  override fun doBeforeShow(dialog: Window) {
    dialog.invalidate()
//    threadPool.execute {
    try {
      logNls("copyStart")
      val project = mainView.createProject()
      logNls("generationStart")
      project.generate()
      logNls("copyEnd")
      mainView.revalidateForm()
      project.includeGradleWrapper(this)
      logNls("generationEnd")
      logAlerts(project)
    } catch (exception: Exception) {
      log(exception.javaClass.name + ": " + exception.message)
      exception.stackTrace.forEach { log("  at $it") }
      exception.printStackTrace()
      logNls("generationFail")
    } finally {
      buttons.forEach { it.isDisabled = false }
    }
//    }

//    threadPool.execute {
    try {
      val findIntellij = if (UIUtils.isWindows) arrayListOf("where.exe", "idea") else arrayListOf("which", "idea")

      val process = ProcessBuilder(findIntellij).start()
      if (process.waitFor() != 0) {
        throw Exception("IntelliJ not found")
      }

      intellijPath = process.inputStream.bufferedReader().readLine()
      intellijIsFlatpak = false

      ideaButton.isDisabled = false
    } catch (e: Exception) {
      Tooltip.Builder("Couldn't find IntelliJ in PATH.\nMake sure that you have JetBrains Toolbox and \"Generate shell scripts\" checked in its settings.").target(ideaButton).build()
      ideaButton.isDisabled = true
    }
//    }

    if (ideaButton.isDisabled && UIUtils.isLinux) {
      try {
        val findFlatpakIntellij = arrayListOf("flatpak", "list", "--app", "--columns=application")

        val process = ProcessBuilder(findFlatpakIntellij).start()
        if (process.waitFor() != 0) {
          throw Exception("Flatpak Intellij not found")
        }

        val ids = arrayListOf("com.jetbrains.IntelliJ-IDEA-Ultimate", "com.jetbrains.IntelliJ-IDEA-Community")
        val output = process.inputStream.bufferedReader().readLines().stream().filter { ids.contains(it) }.findFirst().orElse(null)
        if (output == null) {
          throw Exception("Flatpak Intellij not installed")
        }

        intellijPath = output
        intellijIsFlatpak = true

        ideaButton.isDisabled = false
      } catch (e: Exception) {
        Tooltip.Builder("Couldn't find IntelliJ in PATH or as a flatpak.\nMake sure that you have JetBrains Toolbox and \"Generate shell scripts\" checked in its settings.").target(ideaButton).build()
        ideaButton.isDisabled = true
      }
    }
  }

  override fun logNls(bundleLine: String) = log(locale.i18nBundle.get(bundleLine))
  override fun log(message: String) {
    loggingBuffer.offer(message)
    Gdx.app.postRunnable {
      while (loggingBuffer.isNotEmpty()) {
        if (console.text.isNotBlank()) console.text += '\n'
        console.text += loggingBuffer.poll()
      }
      Gdx.app.postRunnable {
        console.invalidateHierarchy()
        scrollPane.layout()
        scrollPane.scrollPercentY = 1f
      }
    }
  }

  private fun logAlerts(project: Project) {
    val alerts = project.getAlertCodes()
    if (alerts.isEmpty()) return

    logNls("warnings")
    alerts.forEach(this::logNls)
  }

  @LmlAction("openIdea")
  fun openIdea() {
    if (intellijIsFlatpak) {
      ProcessBuilder(arrayListOf("flatpak", "run", intellijPath, ".")).directory(mainView.getDestination().file()).start()
    } else {
      ProcessBuilder(intellijPath, ".").directory(mainView.getDestination().file()).start()
    }
  }
}
