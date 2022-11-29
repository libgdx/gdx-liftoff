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
	override val order = ORDER
	override val isStandard = false

	override fun createGradleFile(project: Project) = TeaVMGradleFile(project)

	override fun initiate(project: Project) {
		project.properties["gdxWebToolsVersion"] = project.advanced.gdxWebToolsVersion
		addGradleTaskDescription(project, "run", "starts the application via a local Jetty server.")
		addGradleTaskDescription(project, "build", "transpiles the application into JavaScript.")
	}
}

class TeaVMGradleFile(val project: Project) : GradleFile(TeaVM.ID) {
	init {
		dependencies.add("project(':${Core.ID}')")

		addDependency("com.github.xpenatan.gdx-web-tools:backend-web:\$gdxWebToolsVersion")
		addDependency("com.github.xpenatan.gdx-web-tools:backend-teavm:\$gdxWebToolsVersion")
		addDependency("com.github.xpenatan.gdx-web-tools:backend-teavm-native:\$gdxWebToolsVersion")
	}

	override fun getContent() = """plugins {
  id 'java'
  id 'org.gretty' version '3.1.0'
}

gretty {
  extraResourceBase 'webapp'
}

sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]
project.ext.mainClassName = '${project.basic.rootPackage}.teavm.TeaVMLauncher'
eclipse.project.name = appName + '-teavm'

dependencies {
${joinDependencies(dependencies)}
}

task buildJavaScript(dependsOn: classes, type: JavaExec) {
  setDescription("Transpile bytecode to JavaScript via TeaVM")
  mainClass.set(project.mainClassName)
  setClasspath(sourceSets.main.runtimeClasspath)
}
build.dependsOn buildJavaScript

task run(dependsOn: [buildJavaScript, ":${TeaVM.ID}:jettyRun"]) {
  setDescription("Run the JavaScript application hosted via a local Jetty server on http://localhost:8080/teavm/")
}

clean.doLast {
    file('webapp').deleteDir()
}
"""
}
