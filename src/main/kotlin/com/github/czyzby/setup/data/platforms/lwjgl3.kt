package com.github.czyzby.setup.data.platforms

import com.github.czyzby.setup.data.files.CopiedFile
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.gradle.GradleFile
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.GdxPlatform

/**
 * Represents the LWJGL3 backend, which runs on all desktop platforms and supports more features than LWJGL2.
 * @author MJ
 */
@GdxPlatform
class LWJGL3 : Platform {
	companion object {
		const val ID = "lwjgl3"
	}

	override val id = ID
	//override val isStandard = true // true is the default, and we want to prefer this to desktop
	override fun createGradleFile(project: Project): GradleFile = Lwjgl3GradleFile(project)
	override fun initiate(project: Project) {
		// Adding game icons:
		arrayOf(16, 32, 64, 128)
			.map { "libgdx${it}.png" }
			.forEach { icon ->
				project.files.add(CopiedFile(projectName = LWJGL3.ID, path = path("src", "main", "resources", icon),
					original = path("icons", icon)))
			}

		addGradleTaskDescription(project, "run", "starts the application.")
		addGradleTaskDescription(project, "jar", "builds application's runnable jar, which can be found at `${id}/build/libs`.")
	}
}


/**
 * Gradle file of the LWJGL3 project.
 * @author MJ
 */
class Lwjgl3GradleFile(val project: Project) : GradleFile(LWJGL3.ID) {
	init {
		dependencies.add("project(':${Core.ID}')")
		addDependency("com.badlogicgames.gdx:gdx-backend-lwjgl3:\$gdxVersion")
		addDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-desktop")
	}

	override fun getContent(): String = """apply plugin: 'application'

sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]
mainClassName = '${project.basic.rootPackage}.lwjgl3.Lwjgl3Launcher'
eclipse.project.name = appName + '-lwjgl3'
sourceCompatibility = ${project.advanced.desktopJavaVersion}

dependencies {
${joinDependencies(dependencies)}}

import org.gradle.internal.os.OperatingSystem

run {
	workingDir = rootProject.file('assets').path
	setIgnoreExitValue(true)
	
	if (OperatingSystem.current() == OperatingSystem.MAC_OS) {
		// Required to run LWJGL3 Java apps on MacOS
		jvmArgs += "-XstartOnFirstThread"
	}
}
jar {
	archiveFileName = "${'$'}{appName}-${'$'}{archiveVersion.get()}.jar"
	dependsOn configurations.runtimeClasspath
	from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } } 
	manifest {
		attributes 'Main-Class': project.mainClassName
	}
}
"""

}
