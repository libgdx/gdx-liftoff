package gdx.liftoff.data.languages

import gdx.liftoff.data.files.SourceDirectory
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.AndroidGradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.JvmLanguage

/**
 * Adds Scala support to the project.
 */
@JvmLanguage
data object Scala : Language {
  override val id = "scala"
  override val version = "3.7.3"

  override fun initiate(project: Project) {
    project.rootGradle.plugins.add(id)
    project.platforms.values.forEach { project.files.add(SourceDirectory(it.id, path("src", "main", "scala"))) }
    if (project.hasPlatform(Android.ID)) {
      val gradleFile: AndroidGradleFile = project.getGradleFile(Android.ID) as AndroidGradleFile
      gradleFile.srcFolders.add("'src/main/scala'")
    }
    addDependency(project, $$"org.scala-lang:scala3-library_3:$scalaVersion")
  }
}
