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
		project.properties["gdxTeaVMVersion"] = project.advanced.gdxTeaVMVersion
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

		addDependency("com.github.xpenatan.gdx-teavm:backend-web:\$gdxTeaVMVersion")
		addDependency("com.github.xpenatan.gdx-teavm:backend-teavm:\$gdxTeaVMVersion")
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
  setDescription("Run the JavaScript application hosted via a local Jetty server at http://localhost:8080/")
}
"""
}
