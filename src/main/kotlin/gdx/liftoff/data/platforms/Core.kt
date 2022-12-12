package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents core application's project, used by all backends.
 */
@GdxPlatform
class Core : Platform {
  companion object {
    const val ID = "core"
    const val ORDER = 0
  }

  override val id = ID
  override val description = "Main module with the application logic shared by all platforms."
  override val order = ORDER
  override val isStandard = false
  override fun createGradleFile(project: Project): GradleFile {
    return CoreGradleFile()
  }

  override fun initiate(project: Project) {
    // Core has no external dependencies by default.
  }
}

/**
 * Gradle file of the core project. Should contain all multi-platform dependencies, like "gdx" itself.
 */
class CoreGradleFile : GradleFile(Core.ID) {
  init {
    addDependency("com.badlogicgames.gdx:gdx:\$gdxVersion")
  }

  override fun getContent(): String {
    return """[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'
eclipse.project.name = appName + '-core'

dependencies {
${joinDependencies(dependencies, "api")}}
"""
  }
}
