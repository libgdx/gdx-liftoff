package com.github.czyzby.setup.views

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.github.czyzby.kiwi.util.common.Strings
import com.github.czyzby.lml.annotation.LmlActor
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner

/**
 * Stores data from "advanced" tab.
 * @author MJ
 */
class AdvancedData {
    @LmlActor("version") private lateinit var versionField: VisTextField
    @LmlActor("gdxVersion") private lateinit var gdxVersionField: VisTextField
    @LmlActor("javaVersion") private lateinit var javaVersionField: Spinner
    @LmlActor("sdkVersion") private lateinit var sdkVersionField: Spinner
    @LmlActor("androidPluginVersion") private lateinit var androidPluginVersionField: VisTextField
    @LmlActor("robovmVersion") private lateinit var robovmVersionField: VisTextField
//    @LmlActor("gwtVersion") private lateinit var gwtVersionField: VisSelectBox<String>
    @LmlActor("gwtPlugin") private lateinit var gwtPluginVersionField: VisTextField
    @LmlActor("serverJavaVersion") private lateinit var serverJavaVersionField: Spinner
    @LmlActor("desktopJavaVersion") private lateinit var desktopJavaVersionField: Spinner
    @LmlActor("generateSkin") private lateinit var generateSkinButton: Button
    @LmlActor("generateReadme") private lateinit var generateReadmeButton: Button
    @LmlActor("gradleTasks") private lateinit var gradleTasksField: VisTextField

    val version: String
        get() = versionField.text

    val gdxVersion: String
        get() = gdxVersionField.text

    fun wrangleVersion(text : String) : String = (if(text.length == 1 || text == "10")
        "1.$text" else if(text.startsWith("1."))
        text.substring(2) else text)

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
        get() = if(gdxVersion.length == 5 && gdxVersion[4] != '9') {
            if(gdxVersion[4] < '5') "2.6.1" else "2.8.0"
        } else "2.8.2"

    val gwtPluginVersion: String
        get() = gwtPluginVersionField.text

    val serverJavaVersion: String
        get() = wrangleVersion(serverJavaVersionField.model.text.removeSuffix(".0"))

    val desktopJavaVersion: String
        get() = wrangleVersion(desktopJavaVersionField.model.text.removeSuffix(".0"))

    val generateSkin: Boolean
        get() = generateSkinButton.isChecked

    val generateReadme: Boolean
        get() = generateReadmeButton.isChecked

    val addGradleWrapper: Boolean
        get() = true

    val gradleTasks: List<String>
        get() = if (gradleTasksField.isEmpty) listOf()
        else gradleTasksField.text.split(Regex(Strings.WHITESPACE_SPLITTER_REGEX)).filter { it.isNotBlank() }

    fun forceSkinGeneration() {
        generateSkinButton.isChecked = true
    }
}
