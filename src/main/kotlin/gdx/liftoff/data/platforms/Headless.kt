package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents headless application project.
 */
@GdxPlatform
class Headless : Platform {
  companion object {
    const val ID = "headless"
    const val ORDER = GWT.ORDER + 1
  }

  override val id = ID
  override val order = ORDER
  override val description = "Desktop platform without a graphical interface."
  override val isStandard = false

  override fun createGradleFile(project: Project): GradleFile = HeadlessGradleFile(project)

  override fun initiate(project: Project) {
    // Headless project has no additional dependencies.
    addGradleTaskDescription(
      project,
      "run",
      "starts the $id application. Note: if $id sources were not modified - and the application still creates `ApplicationListener` from `core` project - this task might fail due to no graphics support."
    )
  }
}

/**
 * Represents the Gradle file of the headless project. Allows to set up a different Java version and launch the application
 * with "run" task.
 */
class HeadlessGradleFile(val project: Project) : GradleFile(Headless.ID) {
  init {
    dependencies.add("project(':${Core.ID}')")
    addDependency("com.badlogicgames.gdx:gdx-backend-headless:\$gdxVersion")
    addDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-desktop")
  }

  override fun getContent(): String = """apply plugin: 'application'
${if (project.rootGradle.plugins.contains("kotlin")) "apply plugin: 'org.jetbrains.kotlin.jvm'\n" else ""}

java.sourceCompatibility = ${project.advanced.serverJavaVersion}
java.targetCompatibility = ${project.advanced.serverJavaVersion}
mainClassName = '${project.basic.rootPackage}.headless.HeadlessLauncher'
application.setMainClass(mainClassName)
eclipse.project.name = appName + '-headless'

dependencies {
${joinDependencies(dependencies)}}

jar {
  archiveBaseName.set(appName)
  duplicatesStrategy(DuplicatesStrategy.EXCLUDE)
  dependsOn configurations.runtimeClasspath
  from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
  manifest {
    attributes 'Main-Class': project.mainClassName
  }
  doLast {
    file(archiveFile).setExecutable(true, false)
  }
}

// Equivalent to the jar task; here for compatibility with gdx-setup.
tasks.register('dist') {
  dependsOn 'jar'
}
"""
}
