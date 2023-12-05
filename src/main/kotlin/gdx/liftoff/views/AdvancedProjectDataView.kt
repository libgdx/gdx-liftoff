package gdx.liftoff.views

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.github.czyzby.kiwi.util.common.Strings
import com.github.czyzby.lml.annotation.LmlActor
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import gdx.liftoff.config.inject
import gdx.liftoff.data.project.AdvancedProjectData

/**
 * Stores data from "advanced" tab.
 */
class AdvancedProjectDataView {
  @LmlActor("version")
  private val versionField: VisTextField = inject()

  @LmlActor("gdxVersion")
  private val gdxVersionField: VisTextField = inject()

  @LmlActor("javaVersion")
  private val javaVersionField: Spinner = inject()

  @LmlActor("sdkVersion")
  private val sdkVersionField: Spinner = inject()

  @LmlActor("androidPluginVersion")
  private val androidPluginVersionField: VisTextField = inject()

  @LmlActor("robovmVersion")
  private val robovmVersionField: VisTextField = inject()

  @LmlActor("gwtPlugin")
  private val gwtPluginVersionField: VisTextField = inject()

  @LmlActor("serverJavaVersion")
  private val serverJavaVersionField: Spinner = inject()

  @LmlActor("desktopJavaVersion")
  private val desktopJavaVersionField: Spinner = inject()

  @LmlActor("generateSkin")
  private val generateSkinButton: Button = inject()

  @LmlActor("generateReadme")
  private val generateReadmeButton: Button = inject()

  @LmlActor("gradleTasks")
  private val gradleTasksField: VisTextField = inject()

  private fun String.toJavaVersion(): String = wrangleVersion(removeSuffix(".0"))
  private fun wrangleVersion(text: String): String = (
    if (text.length == 1 || text == "10") {
      "1.$text"
    } else if (text.startsWith("1.")) {
      text.substring(2)
    } else {
      text
    }
    )

  var androidSdkVersion: String
    get() = sdkVersionField.model.text
    set(value) {
      val model = sdkVersionField.model as IntSpinnerModel
      model.value = value.toInt()
      sdkVersionField.notifyValueChanged(false)
    }

  private val desktopJavaVersion: String
    get() {
      val javaVersion = javaVersionField.model.text.toJavaVersion()
      val version = desktopJavaVersionField.model.text.toJavaVersion()
      return if (version.toDouble() < javaVersion.toDouble()) javaVersion else version
    }

  fun exportData(): AdvancedProjectData = AdvancedProjectData(
    version = versionField.text,
    gdxVersion = gdxVersionField.text,
    javaVersion = javaVersionField.model.text.toJavaVersion(),
    androidPluginVersion = "8.1.4",
    robovmVersion = robovmVersionField.text,
    gwtPluginVersion = gwtPluginVersionField.text,
    serverJavaVersion = serverJavaVersionField.model.text.toJavaVersion(),
    desktopJavaVersion = desktopJavaVersion,
    generateSkin = generateSkinButton.isChecked,
    generateReadme = generateReadmeButton.isChecked,
    gradleTasks = gradleTasksField.text.split(Regex(Strings.WHITESPACE_SPLITTER_REGEX)).filter {
      it.isNotBlank()
    }.toMutableList()
  )
}
