package com.github.czyzby.setup.data.files

import com.badlogic.gdx.files.FileHandle

/**
 * Saves gradle.properties file.
 * @author MJ
 */
class PropertiesFile(val properties: Map<String, String>) : ProjectFile {
    override val path = "gradle.properties"

    override fun save(destination: FileHandle) {
        val content = properties.map { it.key + "=" + it.value }.joinToString(separator = "\n", postfix = "\n")
        destination.child(path).writeString(content, false, "UTF-8")
    }
}