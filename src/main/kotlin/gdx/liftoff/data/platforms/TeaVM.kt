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
  override val description = "Experimental web platform using TeaVM and WebGL."
  override val order = ORDER
  override val isStandard = false

  override fun createGradleFile(project: Project) = TeaVMGradleFile(project)

  override fun initiate(project: Project) {
    project.properties["gdxTeaVMVersion"] = project.advanced.gdxTeaVMVersion
    project.properties["teaVMVersion"] = project.advanced.teaVMVersion
    addGradleTaskDescription(
      project,
      "run",
      "serves the JavaScript application at http://localhost:8080 via a local Jetty server."
    )
    addGradleTaskDescription(
      project,
      "build",
      "builds the JavaScript application into the build/dist/webapp folder."
    )
  }
}

class TeaVMGradleFile(val project: Project) : GradleFile(TeaVM.ID) {
  init {
    dependencies.add("project(':${Core.ID}')")

    addDependency("com.github.xpenatan.gdx-teavm:backend-teavm:\$gdxTeaVMVersion")
    addDependency("org.teavm:teavm-tooling:\$teaVMVersion")
    addDependency("org.teavm:teavm-core:\$teaVMVersion")
    addDependency("org.teavm:teavm-classlib:\$teaVMVersion")
    addDependency("org.teavm:teavm-jso:\$teaVMVersion")
    addDependency("org.teavm:teavm-jso-apis:\$teaVMVersion")
    addDependency("org.teavm:teavm-jso-impl:\$teaVMVersion")
  }

  override fun getContent() = """plugins {
  id 'java'
  id 'org.gretty' version '${project.advanced.grettyVersion}'
}

gretty {
  contextPath = '/'
  extraResourceBase 'build/dist/webapp'
}

sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]
project.ext.mainClassName = '${project.basic.rootPackage}.teavm.TeaVMBuilder'
eclipse.project.name = appName + '-teavm'

// This must be at least 11, and no higher than the JDK version this project is built with.
java.targetCompatibility = "${project.advanced.javaVersion}"
// This should probably be equal to targetCompatibility, above. This only affects the TeaVM module.
java.sourceCompatibility = "${project.advanced.javaVersion}"


dependencies {
${joinDependencies(dependencies)}
}

tasks.register('buildJavaScript', JavaExec) {
  dependsOn classes
  setDescription("Transpile bytecode to JavaScript via TeaVM")
  mainClass.set(project.mainClassName)
  setClasspath(sourceSets.main.runtimeClasspath)
}
build.dependsOn buildJavaScript

tasks.register("run") {
  description = "Run the JavaScript application hosted via a local Jetty server at http://localhost:8080/"
  dependsOn(buildJavaScript, tasks.named("jettyRun"))
}
"""
}
