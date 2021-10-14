package gdx.liftoff.data.files

import com.badlogic.gdx.files.FileHandle
import gdx.liftoff.data.platforms.Platform

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