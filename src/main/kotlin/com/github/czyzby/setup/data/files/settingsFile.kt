package com.github.czyzby.setup.data.files

import com.badlogic.gdx.files.FileHandle
import com.github.czyzby.setup.data.platforms.Platform

/**
 * Creates settings.gradle file.
 */
class SettingsFile(val platforms: Iterable<Platform>) : ProjectFile {
    override val path = "settings.gradle"
    override fun save(destination: FileHandle) {
        val content = platforms.joinToString(prefix = "include ", separator = ", ", postfix = "\n") { "'${it.id}'" }
        destination.child(path).writeString(content, false, "UTF-8")
    }
}