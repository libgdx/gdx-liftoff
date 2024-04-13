package gdx.liftoff.data.files

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.github.czyzby.kiwi.util.common.Strings
import gdx.liftoff.data.platforms.Platform
import java.io.File

/**
 * Common interface of files generated or copied during project creation.
 */
interface ProjectFile {
  /**
   * Relative path to the project file.
   */
  val path: String

  /**
   * Saves the file at the chosen location.
   * @param destination project root folder.
   */
  fun save(destination: FileHandle)
}

/**
 * Represents a directory with source files.
 */
open class SourceDirectory(val projectName: String, sourcePath: String = path("src", "main", "java")) : ProjectFile {
  override val path: String = projectName + File.separator + sourcePath

  override fun save(destination: FileHandle) {
    destination.child(path).mkdirs()
  }
}

fun path(vararg directories: String) = directories.joinToString(separator = File.separator)

/**
 * Base class for source files.
 */
open class SourceFile private constructor(val content: String, override val path: String) : ProjectFile {
  /**
   * @param content content of the source file.
   * @param projectName name of the project to which the file should be appended. Optional.
   * @param sourceFolderPath path of the source in the selected project. Defaults to "src/main/java".
   * @param packageName name of the package of the source file. Optional.
   * @param fileName name of the source file. For example, "Content.java".
   */
  constructor(
    content: String,
    projectName: String,
    sourceFolderPath: String = path("src", "main", "java"),
    packageName: String,
    fileName: String
  ) :
    this(content, toRelativePath(projectName, sourceFolderPath, packageName, fileName))

  /**
   * @param content content of the source file.
   * @param projectName name of the project to which the file should be appended. Optional.
   * @param fileName name of the source file. For example, "Content.java".
   */
  constructor(content: String, projectName: String, fileName: String) :
    this(
      content,
      if (projectName.isBlank()) {
        fileName
      } else {
        path(projectName, fileName)
      }
    )

  override fun save(destination: FileHandle) {
    destination.child(path).writeString(content, false, "UTF-8")
  }
}

fun toRelativePath(projectName: String, sourceFolderPath: String, packageName: String, fileName: String): String {
  if (projectName.isEmpty() && sourceFolderPath.isEmpty() && packageName.isEmpty()) {
    return fileName
  }
  return "${if (Strings.isNotBlank(projectName)) {
    projectName + File.separator
  } else {
    Strings.EMPTY_STRING
  }}$sourceFolderPath${File.separator}${if (Strings.isNotBlank(packageName)) {
    packageName.replace('.', File.separatorChar) + File.separator
  } else {
    Strings.EMPTY_STRING
  }}$fileName"
}

/**
 * Base class for copied project resources.
 */
open class CopiedFile private constructor(override val path: String, val original: String, val fileType: Files.FileType) : ProjectFile {
  /**
   * @param projectName name of the project which should contain the file.
   * @param path relative path inside the project with the exact file location.
   * @param original internal path to the resource.
   */
  constructor(projectName: String = "", path: String, original: String, fileType: Files.FileType = Files.FileType.Internal) :
    this(
      if (projectName.isNotEmpty()) {
        projectName + File.separator
      } else {
        ""
      } + path,
      original,
      fileType
    )

  override fun save(destination: FileHandle) {
    Gdx.files.getFileHandle(original, fileType).copyTo(destination.child(path))
  }
}

/**
 * Saves gradle.properties file.
 */
class PropertiesFile(val properties: Map<String, String>) : ProjectFile {
  override val path = "gradle.properties"

  override fun save(destination: FileHandle) {
    val content = properties.map { it.key + "=" + it.value }.joinToString(separator = "\n", postfix = "\n")
    destination.child(path).writeString(content, false, "UTF-8")
  }
}

/**
 * Creates settings.gradle file.
 */
class SettingsFile(val platforms: Iterable<Platform>) : ProjectFile {
  override val path = "settings.gradle"
  override fun save(destination: FileHandle) {
    val content = platforms.joinToString(
      prefix =
      """// A list of which subprojects to load as part of the same larger project.
// You can remove Strings from the list and reload the Gradle project
// if you want to temporarily disable a subproject.
include """,
      separator = ", ",
      postfix = "\n"
    ) { "'${it.id}'" }
    destination.child(path).writeString(content, false, "UTF-8")
  }
}

/**
 * Base class for copied project resources.
 */
class GeneratedImageFile private constructor(override val path: String, val content: Pixmap) : ProjectFile {
  /**
   * @param projectName name of the project which should contain the file.
   * @param path relative path inside the project with the exact file location.
   * @param original internal path to the resource.
   */
  constructor(projectName: String = "", path: String, content: Pixmap) :
    this(
      if (projectName.isNotEmpty()) {
        projectName + File.separator
      } else {
        ""
      } + path,
      content
    )

  override fun save(destination: FileHandle) {
    PixmapIO.writePNG(destination.child(path), content, 4, false)
  }
}
