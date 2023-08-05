package gdx.liftoff.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.utils.ObjectSet
import com.github.czyzby.kiwi.util.common.Strings
import com.github.czyzby.lml.annotation.LmlActor
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.VisValidatableTextField
import gdx.liftoff.config.inject
import gdx.liftoff.data.project.BasicProjectData

/**
 * Filled by the LML parser, this class contains references to basic project data widgets.
 */
class BasicProjectDataView {
  @LmlActor("name")
  private val nameField: VisTextField = inject()

  @LmlActor("package")
  private val rootPackageField: VisTextField = inject()

  @LmlActor("class")
  private val mainClassField: VisTextField = inject()

  @LmlActor("destination")
  private val destinationField: VisTextField = inject()

  @LmlActor("androidSdk")
  private val androidSdkPathField: VisValidatableTextField = inject()

  @LmlActor("mkdirs")
  private val mkdirsButton: Button = inject()

  @LmlActor("clearFolder")
  private val clearButton: Button = inject()

  @LmlActor("useOldestSdk", "useLatestSdk")
  private val sdkButtons: ObjectSet<Button> = inject()

  val destination: FileHandle
    get() = Gdx.files.absolute(destinationField.text)
  val androidSdk: FileHandle
    get() = Gdx.files.absolute(androidSdkPathField.text)

  fun setDestination(path: String) {
    destinationField.text = path
  }

  fun setAndroidSdkPath(path: String) {
    androidSdkPathField.text = path
  }

  fun revalidateDirectoryUtilityButtons() {
    try {
      val folder = destination
      if (folder.exists()) {
        mkdirsButton.isDisabled = true
        clearButton.isDisabled = !(folder.isDirectory && folder.list().isNotEmpty())
      } else {
        mkdirsButton.isDisabled = destinationField.text.isBlank()
        clearButton.isDisabled = true
      }
    } catch (exception: Exception) {
      // Somewhat expected for invalid input.
      clearButton.isDisabled = true
      mkdirsButton.isDisabled = true
    }
  }

  fun getLatestAndroidApiVersion(): Int = getAndroidApi { ver1, ver2 -> ver1 - ver2 }
  fun getOldestAndroidApiVersion(): Int = getAndroidApi { ver1, ver2 -> ver2 - ver1 }

  private fun getAndroidApi(comparator: (Int, Int) -> Int): Int {
    if (!androidSdkPathField.isInputValid) {
      return 0
    }
    var apiLevel: Int? = null
    androidSdk.child("platforms").list().forEach {
      val level = findProperty(it, "AndroidVersion.ApiLevel")
      if (apiLevel == null || (level != null && comparator(apiLevel!!, level.toInt()) < 0)) {
        apiLevel = level!!.toInt()
      }
    }
    return if (apiLevel == null) 0 else apiLevel!!
  }

  fun revalidateSdkUtilityButtons() {
    if (!androidSdkPathField.isDisabled && androidSdkPathField.isInputValid) {
      sdkButtons.forEach { it.isDisabled = false }
    } else {
      sdkButtons.forEach { it.isDisabled = true }
    }
  }

  private fun findProperty(directory: FileHandle, property: String, file: String = "source.properties"): String? {
    val properties = directory.child(file)
    if (properties.exists()) {
      properties.file().bufferedReader().use {
        var line = it.readLine()
        while (line != null) {
          if (line.contains(property)) {
            return Strings.split(line, '=')[1].trim()
          }
          line = it.readLine()
        }
      }
    }
    return null
  }

  fun exportData(): BasicProjectData = BasicProjectData(
    name = nameField.text,
    rootPackage = rootPackageField.text,
    mainClass = mainClassField.text,
    destination = destination,
    androidSdk = androidSdk
  )
}
