package com.github.czyzby.setup.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.utils.ObjectSet
import com.github.czyzby.kiwi.util.common.Strings
import com.github.czyzby.lml.annotation.LmlActor
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.VisValidatableTextField

/**
 * Filled by the LML parser, this class contains references to basic project data widgets.
 */
class BasicProjectData {
    @LmlActor("name") private lateinit var nameField: VisTextField
    @LmlActor("package") private lateinit var rootPackageField: VisTextField
    @LmlActor("class") private lateinit var mainClassField: VisTextField
    @LmlActor("destination") private lateinit var destinationField: VisTextField
    @LmlActor("androidSdk") private lateinit var androidSdkPathField: VisValidatableTextField

    @LmlActor("mkdirs") private lateinit var mkdirsButton: Button
    @LmlActor("clearFolder") private lateinit var clearButton: Button

    @LmlActor("useOldestSdk", "useLatestSdk") private lateinit var sdkButtons: ObjectSet<Button>

    val name: String
        get() = nameField.text
    val rootPackage: String
        get() = rootPackageField.text
    val mainClass: String
        get() = mainClassField.text
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
                if (folder.isDirectory && folder.list().size > 0) {
                    clearButton.isDisabled = false
                } else {
                    clearButton.isDisabled = true
                }
            } else {
                mkdirsButton.isDisabled = destinationField.text.isBlank()
                clearButton.isDisabled = true
            }
        } catch(exception: Exception) {
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
        if (!androidSdkPathField.isDisabled && androidSdkPathField.isInputValid ) {
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
}
