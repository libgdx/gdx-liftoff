package gdx.liftoff.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.Version
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.czyzby.autumn.annotation.Inject
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService
import com.github.czyzby.autumn.mvc.stereotype.View
import com.github.czyzby.lml.annotation.LmlAction
import com.github.czyzby.lml.annotation.LmlActor
import com.github.czyzby.lml.annotation.LmlAfter
import com.github.czyzby.lml.annotation.LmlInject
import com.github.czyzby.lml.parser.LmlParser
import com.github.czyzby.lml.parser.action.ActionContainer
import com.github.czyzby.lml.vis.ui.VisFormTable
import com.kotcrab.vis.ui.widget.LinkLabel
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import gdx.liftoff.config.Configuration
import gdx.liftoff.config.inject
import gdx.liftoff.config.threadPool
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.project.Project
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryUtil.memAllocPointer
import org.lwjgl.system.MemoryUtil.memFree
import org.lwjgl.util.nfd.NativeFileDialog
import kotlin.Array
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Main application's view. Displays application's menu.
 */
@View(id = "main", value = "templates/main.lml", first = true)
@Suppress("unused") // Methods and fields accessed via reflection.
class MainView : ActionContainer {
  @Inject private val interfaceService: InterfaceService = inject()

  @LmlInject private val basicData: BasicProjectDataView = inject()

  @LmlInject private val advancedData: AdvancedProjectDataView = inject()

  @LmlInject @Inject
  private val platformsView: PlatformsView = inject()

  @LmlInject @Inject
  private val languagesView: LanguagesView = inject()

  @LmlInject @Inject
  private val extensionsData: ExtensionsView = inject()

  @LmlInject @Inject
  private val templatesView: TemplatesView = inject()

  @LmlActor("logo")
  @Inject
  private val logo: Image = inject()

  @LmlActor("versionUpdate")
  @LmlInject
  private val versionUpdate: VisLabel = inject()

  @LmlActor("versionLink")
  @LmlInject
  private val versionLink: LinkLabel = inject()

  @LmlActor("form")
  private val form: VisFormTable = inject()

  @LmlAction("chooseDirectory")
  fun chooseDirectory() {
    pickDirectory(
      getDestination(),
      object : FileChooserAdapter() {
        override fun selected(files: GdxArray<FileHandle>?) {
          val file = files?.first()
          if (file != null) {
            basicData.setDestination(file.path())
          }
        }
      }
    )
  }

  @LmlAction("chooseSdkDirectory")
  fun chooseSdkDirectory() {
    pickDirectory(
      getAndroidSdkVersion(),
      object : FileChooserAdapter() {
        override fun selected(files: GdxArray<FileHandle>?) {
          val file = files?.first()
          if (file != null) {
            basicData.setAndroidSdkPath(file.path())
          }
        }
      }
    )
  }

  @LmlAction("prefetchVersion")
  fun prefetchLibraryVersion(button: Button) {
    if (button.isChecked) {
      threadPool.execute {
        // Prefetching library version - all Maven repositories use group and name cache,
        // so prefetching the version asynchronously cuts down on project generation time.
        extensionsData.extensionsById[button.name]?.version
      }
    }
  }

  private fun pickDirectory(initialFolder: FileHandle, callback: FileChooserAdapter) {
    var initialPath = initialFolder.path()

    if (System.getProperty("os.name").lowercase().contains("win")) {
      initialPath = initialPath.replace("/", "\\")
    }

    val pathPointer = memAllocPointer(1)

    try {
      val status = NativeFileDialog.NFD_PickFolder(initialPath, pathPointer)

      if (status == NativeFileDialog.NFD_CANCEL) {
        callback.canceled()
        return
      }

      // Unexpected error - show VisUI dialog.
      if (status != NativeFileDialog.NFD_OKAY) {
        throw Throwable("Native file dialog error")
      }

      val folder = pathPointer.getStringUTF8(0)
      NativeFileDialog.nNFD_Free(pathPointer.get(0))

      val array = GdxArray<FileHandle>()
      array.add(Gdx.files.absolute(folder))

      callback.selected(array)
    } catch (e: Throwable) {
      Gdx.app.error(
        "NFD",
        "The Native File Dialog library could not be loaded.\n" +
          "Check if you have multiple LWJGL3 applications open simultaneously,\n" +
          "since that can cause this error."
      )
      Gdx.app.error("NFD", e.stackTraceToString())
      val fileChooser = FileChooser(FileChooser.Mode.OPEN)
      fileChooser.selectionMode = FileChooser.SelectionMode.DIRECTORIES
      fileChooser.setDirectory(initialPath)
      fileChooser.setListener(callback)

      form.stage.addActor(fileChooser.fadeIn())
    } finally {
      memFree(pathPointer)
    }
  }

  @LmlAction("togglePlatform")
  fun togglePlatform(button: Button) {
    if (button.name == Android.ID) {
      platformsView.toggleAndroidPlatform(button.isChecked)
      revalidateForm()
    }
  }

  @LmlAction("mkdirs")
  fun createDestinationDirectory() {
    basicData.destination.mkdirs()
    revalidateForm()
  }

  @LmlAction("checkProjectDir")
  fun checkProjectDirectory() {
    basicData.revalidateDirectoryUtilityButtons()
  }

  @LmlAction("reloadSdkButtons")
  fun reloadAndroidSdkButtons() {
    basicData.revalidateSdkUtilityButtons()
  }

  @LmlAction("useLatestSdk")
  fun extractLatestAndroidApiVersions() {
    advancedData.androidSdkVersion = basicData.getLatestAndroidApiVersion().toString()
  }

  @LmlAction("useOldestSdk")
  fun extractOldestAndroidApiVersions() {
    advancedData.androidSdkVersion = basicData.getOldestAndroidApiVersion().toString()
  }

  @LmlAfter fun initiateVersions(parser: LmlParser) {
    languagesView.assignVersions(parser)
  }

  @LmlAfter fun checkSetupVersion() {
    versionUpdate.color.a = 0f
    versionLink.color.a = 0f
    // When using snapshots, we don't care if the version matches the latest stable.
    if (Configuration.VERSION.endsWith("SNAPSHOT")) return

    val request = Net.HttpRequest(Net.HttpMethods.GET)
    request.url = "https://raw.githubusercontent.com/libgdx/gdx-liftoff/master/version.txt"
    val listener = object : Net.HttpResponseListener {
      override fun handleHttpResponse(httpResponse: Net.HttpResponse) {
        val latestStable = httpResponse.resultAsString.trim()
        if (Configuration.VERSION != latestStable) {
          Gdx.app.postRunnable {
            logo.setZIndex(0)
            logo.addAction(
              Actions.sequence(
                Actions.fadeOut(1.5f),
                Actions.run {
                  versionLink.addAction(Actions.fadeIn(1f))
                  versionUpdate.addAction(Actions.fadeIn(1f))
                }
              )
            )
          }
        }
      }

      override fun cancelled() {
        // Never cancelled.
      }

      override fun failed(t: Throwable?) {
        // Ignored. The user might not be connected.
      }
    }
    Gdx.net.sendHttpRequest(request, listener)
  }

  fun revalidateForm() {
    form.formValidator.validate()
    basicData.revalidateDirectoryUtilityButtons()
    basicData.revalidateSdkUtilityButtons()
  }

  @LmlAction("platforms")
  fun getPlatforms(): Iterable<*> =
    platformsView.platforms.entries.sortedBy { it.value.order }.map { it.key }

  @LmlAction("show")
  fun getTabShowingAction(): Action = Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.1f))

  @LmlAction("hide")
  fun getTabHidingAction(): Action = Actions.fadeOut(0.1f)

  @LmlAction("gdxVersion")
  fun getGdxVersion(): String = Version.VERSION

  @LmlAction("gwtVersions")
  fun getGwtVersions(): Array<String> = arrayOf("2.8.2")

  @LmlAction("jvmLanguages")
  fun getLanguages(): Array<String> = languagesView.languages

  @LmlAction("jvmLanguagesVersions")
  fun getLanguagesVersions(): Array<String> = languagesView.versions

  @LmlAction("templates")
  fun getOfficialTemplates(): Array<String> =
    templatesView.officialTemplates.map { it.id }.sortedWith { left, right -> if (left == "classic") -1 else if (right == "classic") 1 else left.compareTo(right) }
      .toTypedArray()

  @LmlAction("thirdPartyTemplates")
  fun getThirdPartyTemplates(): Array<String> =
    templatesView.thirdPartyTemplates.map { it.id }.sorted().toTypedArray()

  @LmlAction("officialExtensions")
  fun getOfficialExtensions(): Array<String> =
    extensionsData.official.map { it.id }.sorted().toTypedArray()

  @LmlAction("officialExtensionsUrls")
  fun getOfficialExtensionsUrls(): Array<String> =
    extensionsData.official.sortedBy { it.id }.map { it.url }.toTypedArray()

  @LmlAction("thirdPartyExtensions")
  fun getThirdPartyExtensions(): Array<String> =
    extensionsData.thirdParty.map { it.id }.sorted().toTypedArray()

  @LmlAction("thirdPartyExtensionsUrls")
  fun getThirdPartyExtensionsUrls(): Array<String> =
    extensionsData.thirdParty.sortedBy { it.id }.map { it.url }.toTypedArray()

  @LmlAction("initTabs")
  fun initiateTabbedPane(tabbedPane: TabbedPane.TabbedPaneTable) {
    tabbedPane.tabbedPane.tabsPane.horizontalFlowGroup.spacing = 2f
  }

  fun getDestination(): FileHandle = basicData.destination
  private fun getAndroidSdkVersion(): FileHandle = basicData.androidSdk

  fun createProject(): Project = Project(
    basic = basicData.exportData(),
    platforms = platformsView.getSelectedPlatforms(),
    advanced = advancedData.exportData(),
    languages = languagesView.exportData(),
    extensions = extensionsData.exportData(),
    template = templatesView.getSelectedTemplate()
  )

  @LmlAction("minimize")
  fun iconify() = GLFW.glfwIconifyWindow(GLFW.glfwGetCurrentContext())

//  @LmlAction("initTitleTable")
//  fun addWindowDragListener(actor: Actor) {
//    actor.addListener(object : InputListener() {
//      private val context = GLFW.glfwGetCurrentContext()
//      private var startX = 0
//      private var startY = 0
//      private var offsetX = 0
//      private var offsetY = 0
//      private val cursorX = BufferUtils.createDoubleBuffer(1)
//      private val cursorY = BufferUtils.createDoubleBuffer(1)
//      private val windowX = BufferUtils.createIntBuffer(1)
//      private val windowY = BufferUtils.createIntBuffer(1)
//
//      override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
//        GLFW.glfwGetCursorPos(context, cursorX, cursorY)
//        startX = getX()
//        startY = getY()
//        return true
//      }
//
//      override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
//        GLFW.glfwGetCursorPos(context, cursorX, cursorY)
//        offsetX = getX() - startX
//        offsetY = getY() - startY
//        GLFW.glfwGetWindowPos(context, windowX, windowY)
//        GLFW.glfwSetWindowPos(context, windowX.get(0) + offsetX, windowY.get(0) + offsetY)
//      }
//
//      private fun getX(): Int = MathUtils.floor(cursorX.get(0).toFloat())
//      private fun getY(): Int = MathUtils.floor(cursorY.get(0).toFloat())
//    })
//  }

  /**
   * I have no idea how to register this on an LML Actor. LML docs are no help. Agh.
   */
  fun assignScrollFocus(actor: Actor) {
    actor.addListener(object : ClickListener() {
      override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        actor.stage?.scrollFocus = actor
      }

      override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        super.exit(event, x, y, pointer, toActor)
        if (actor.stage?.scrollFocus == actor) {
          actor.stage?.scrollFocus = null
        }
      }
    })
  }
}
