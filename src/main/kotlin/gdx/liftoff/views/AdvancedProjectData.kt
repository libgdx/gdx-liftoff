package gdx.liftoff.views

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.github.czyzby.kiwi.util.common.Strings
import com.github.czyzby.lml.annotation.LmlActor
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import gdx.liftoff.config.inject
import gdx.liftoff.data.libraries.Repository.MavenCentral

/**
 * Stores data from "advanced" tab.
 */
class AdvancedProjectData {
	@LmlActor("version") private val versionField: VisTextField = inject()
	@LmlActor("gdxVersion") private val gdxVersionField: VisTextField = inject()
	@LmlActor("javaVersion") private val javaVersionField: Spinner = inject()
	@LmlActor("sdkVersion") private val sdkVersionField: Spinner = inject()
	@LmlActor("androidPluginVersion") private val androidPluginVersionField: VisTextField = inject()
	@LmlActor("robovmVersion") private val robovmVersionField: VisTextField = inject()
//    @LmlActor("gwtVersion") private val gwtVersionField: VisSelectBox<String> = injected()
	@LmlActor("gwtPlugin") private val gwtPluginVersionField: VisTextField = inject()
	@LmlActor("serverJavaVersion") private val serverJavaVersionField: Spinner = inject()
	@LmlActor("desktopJavaVersion") private val desktopJavaVersionField: Spinner = inject()
	@LmlActor("generateSkin") private val generateSkinButton: Button = inject()
	@LmlActor("generateReadme") private val generateReadmeButton: Button = inject()
	@LmlActor("gradleTasks") private val gradleTasksField: VisTextField = inject()

	val version: String
		get() = versionField.text

	val gdxVersion: String
		get() = gdxVersionField.text

	private fun wrangleVersion(text: String): String = (
		if (text.length == 1 || text == "10")
			"1.$text" else if (text.startsWith("1."))
			text.substring(2) else text
		)

	val javaVersion: String
		get() = wrangleVersion(javaVersionField.model.text.removeSuffix(".0"))

	var androidSdkVersion: String
		get() = sdkVersionField.model.text
		set(value) {
			val model = sdkVersionField.model as IntSpinnerModel
			model.value = value.toInt()
			sdkVersionField.notifyValueChanged(false)
		}

	val androidPluginVersion: String
		get() = androidPluginVersionField.text

	val robovmVersion: String
		get() = robovmVersionField.text

	val gwtVersion: String
		get() = if (gdxVersion.length == 5 && gdxVersion[4] != '9') {
			if (gdxVersion[4] < '5') "2.6.1" else "2.8.0"
		} else "2.8.2"

	val gwtPluginVersion: String
		get() = gwtPluginVersionField.text

	/**
	 * Version of xpenatan's TeaVM backend.
	 */
	val gdxTeaVMVersion: String
		get() = MavenCentral.getLatestVersion(group = "com.github.xpenatan.gdx-teavm", name = "backend-teavm")
			?: "1.0.0-b1"

	/**
	 * Version of the Gretty Gradle plugin used to serve compiled JavaScript applications.
	 */
	val grettyVersion: String
		get() = "3.1.0"

	val serverJavaVersion: String
		get() = wrangleVersion(serverJavaVersionField.model.text.removeSuffix(".0"))

	val desktopJavaVersion: String
		get() {
			val djv = wrangleVersion(desktopJavaVersionField.model.text.removeSuffix(".0"))
			return if(djv.toDouble() < javaVersion.toDouble())
				javaVersion
			else
				djv
		}

	val generateSkin: Boolean
		get() = generateSkinButton.isChecked

	val generateReadme: Boolean
		get() = generateReadmeButton.isChecked

	val gradleTasks: MutableList<String> = mutableListOf()
	fun composeGradleTasks(): List<String> {
		return gradleTasksField.text.split(Regex(Strings.WHITESPACE_SPLITTER_REGEX)).filter { it.isNotBlank() } + gradleTasks
	}

	fun forceSkinGeneration() {
		generateSkinButton.isChecked = true
	}
}
