package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents shared project, accessible by both client and server application.
 */
@GdxPlatform
class Shared : Platform {
  companion object {
    const val ID = "shared"
    const val ORDER = Server.ORDER + 1
  }

  override val id = ID
  override val order = ORDER
  override val description = "A common module shared by `core` and `server` platforms."
  override val isStandard = false

  override fun createGradleFile(project: Project): GradleFile = SharedGradleFile(project)

  override fun initiate(project: Project) {
    project.getGradleFile(Core.ID).dependencies.add("project(':$id')")
    if (project.hasPlatform(Server.ID)) {
      project.getGradleFile(Server.ID).dependencies.add("project(':$id')")
    }
    if (project.hasPlatform(GWT.ID)) {
      // Including shared project sources in GWT platform:
      project.getGradleFile(GWT.ID).buildDependencies.add("project(':$id')")
    }
  }
}

/**
 * Represents shared project Gradle file. Should include dependencies that should be available for both server and
 * client applications.
 */
class SharedGradleFile(val project: Project) : GradleFile(Shared.ID) {
  override fun getContent(): String = """eclipse.project.name = appName + '-shared'

dependencies {
${joinDependencies(dependencies)}}
"""
}
