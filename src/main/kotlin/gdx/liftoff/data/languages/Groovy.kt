package gdx.liftoff.data.languages

import gdx.liftoff.data.files.SourceDirectory
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.AndroidGradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.JvmLanguage

/**
 * Adds Groovy support to the project.
 */
@JvmLanguage
@Suppress("unused") // Class accessed via reflection.
class Groovy : Language {
  override val id = "groovy"
  override val version = "4.0.2"

  override fun initiate(project: Project) {
    project.rootGradle.plugins.add(id)
    project.platforms.values.forEach { project.files.add(SourceDirectory(it.id, path("src", "main", "groovy"))) }
    if (project.hasPlatform(Android.ID)) {
      val gradleFile = project.getGradleFile(Android.ID) as AndroidGradleFile
      gradleFile.srcFolders.add("'src/main/groovy'")
    }
    addDependency(project, "org.codehaus.groovy:groovy-all:\$groovyVersion")
  }
}
