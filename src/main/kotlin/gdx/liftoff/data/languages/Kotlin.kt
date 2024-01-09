package gdx.liftoff.data.languages

import gdx.liftoff.data.files.SourceDirectory
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.AndroidGradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.JvmLanguage

/**
 * Adds Kotlin support to the project.
 */
@JvmLanguage
@Suppress("unused") // Class accessed via reflection.
class Kotlin : Language {
  override val id = "kotlin"
  override val version = "1.9.22"

  override fun initiate(project: Project) {
    project.rootGradle.buildDependencies.add("\"org.jetbrains.kotlin:kotlin-gradle-plugin:\$kotlinVersion\"")
    project.rootGradle.plugins.add(id)
    project.platforms.values.forEach { project.files.add(SourceDirectory(it.id, path("src", "main", "kotlin"))) }
    if (project.hasPlatform(Android.ID)) {
      val gradleFile = project.getGradleFile(Android.ID) as AndroidGradleFile
      gradleFile.insertLatePlugin()
      gradleFile.srcFolders.add("'src/main/kotlin'")
    }
    addDependency(project, "org.jetbrains.kotlin:kotlin-stdlib:\$kotlinVersion")
  }
}
