package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents the unofficial TeaVM web backend created by xpenatan.
 */
@GdxPlatform
class TeaVM : Platform {
  companion object {
    const val ID = "teavm"
    const val ORDER = Headless.ORDER + 1
  }

  override val id = ID
  override val description = "Web backend that supports most JVM languages."
  override val order = ORDER
  override val isStandard = false

  override fun createGradleFile(project: Project) = TeaVMGradleFile(project)

  override fun initiate(project: Project) {
    project.properties["gdxTeaVMVersion"] = project.advanced.gdxTeaVMVersion
    addGradleTaskDescription(
      project,
      "run",
      "serves the JavaScript application at http://localhost:8080 via a local Jetty server.",
    )
    addGradleTaskDescription(
      project,
      "build",
      "builds the JavaScript application into the build/dist/webapp folder.",
    )
  }
}

class TeaVMGradleFile(
  val project: Project,
) : GradleFile(TeaVM.ID) {
  init {
    dependencies.add("project(':${Core.ID}')")

    addDependency("com.github.xpenatan.gdx-teavm:backend-web:\$gdxTeaVMVersion")
  }

  override fun getContent() =
    """plugins {
  id 'java'
}

sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]
project.ext.mainClassName = "${project.basic.rootPackage}.teavm.TeaVMBuilder"
eclipse.project.name = appName + "-teavm"

// This must be at least 11, and no higher than the JDK version this project is built with.
java.targetCompatibility = "${project.advanced.javaVersion}"
// This should probably be equal to targetCompatibility, above. This only affects the TeaVM module.
java.sourceCompatibility = "${project.advanced.javaVersion}"


dependencies {
${joinDependencies(dependencies)}
}

tasks.register("runRelease", JavaExec) {
  description = "Run the TeaVM application hosted via a local Jetty server at http://localhost:8080/"
  group("application")
  dependsOn(classes)
  mainClass.set(project.mainClassName)
  setClasspath(sourceSets.main.runtimeClasspath)
  args += ["run"]
}

tasks.register("runDebug", JavaExec) {
  description = "Run the TeaVM application with debug enabled hosted via a local Jetty server at http://localhost:8080/"
  group("application")
  dependsOn(classes)
  mainClass.set(project.mainClassName)
  setClasspath(sourceSets.main.runtimeClasspath)
  args += ["debug", "run"]
}

tasks.register("buildRelease", JavaExec) {
  description = "Build the TeaVM application; doesn't run directly"
  group("build")
  dependsOn(classes)
  mainClass.set(project.mainClassName)
  setClasspath(sourceSets.main.runtimeClasspath)
}

tasks.register("buildDebug", JavaExec) {
  description = "Build the TeaVM application with debug enabled; doesn't run directly"
  group("build")
  dependsOn(classes)
  mainClass.set(project.mainClassName)
  setClasspath(sourceSets.main.runtimeClasspath)
  args += ["debug"]
}

build.dependsOn buildRelease
"""
}
