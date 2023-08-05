package gdx.liftoff.data.files.gradle

import com.badlogic.gdx.files.FileHandle
import gdx.liftoff.data.files.ProjectFile
import java.io.File

abstract class GradleFile private constructor(override val path: String) : ProjectFile {
  val buildDependencies = mutableSetOf<String>()
  val dependencies = mutableSetOf<String>()
  val specialDependencies = mutableSetOf<String>()

  constructor(projectName: String, fileName: String = "build.gradle") : this(
    if (projectName.isNotEmpty()) {
      projectName + File.separator
    } else {
      ""
    } + fileName
  )

  fun joinDependencies(dependencies: Collection<String>, type: String = "implementation", indent: String = "  "): String {
    return (
      if (dependencies.isEmpty()) {
        "\n"
      } else {
        dependencies.sorted().joinToString(prefix = "$indent$type ", separator = "\n$indent$type ", postfix = "\n")
      }
      ) + (
      if (specialDependencies.isEmpty()) {
        ""
      } else {
        specialDependencies.sorted().joinToString(prefix = indent, separator = "\n$indent", postfix = "\n")
      }
      )
  }

  /**
   * @param dependency will be added as an "implementation" or "api" dependency, quoted.
   */
  fun addDependency(dependency: String) = dependencies.add("\"$dependency\"")

  /**
   * @param dependency should have the dependency type specified, and the full dependency vector quoted.
   */
  fun addSpecialDependency(dependency: String) = specialDependencies.add(dependency)

  override fun save(destination: FileHandle) {
    destination.child(path).writeString(getContent(), false, "UTF-8")
  }

  abstract fun getContent(): String
}
