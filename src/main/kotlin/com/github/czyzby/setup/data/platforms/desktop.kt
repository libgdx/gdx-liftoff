package com.github.czyzby.setup.data.platforms

import com.github.czyzby.setup.data.files.CopiedFile
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.gradle.GradleFile
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.GdxPlatform

/**
 * Represents the legacy Desktop backend, which has been replaced in practice by LWJGL3.
 * @author MJ
 */
@GdxPlatform
class Desktop : Platform {
	companion object {
		const val ID = "desktop"
	}

	override val id = ID
	override val isStandard = false // use lwjgl3 instead
	override fun createGradleFile(project: Project): GradleFile = DesktopGradleFile(project)

	override fun initiate(project: Project) {
		// Adding game icons:
		arrayOf(16, 32, 64, 128)
				.map { "libgdx${it}.png" }
				.forEach { icon ->
					project.files.add(CopiedFile(projectName = ID, path = path("src", "main", "resources", icon),
							original = path("icons", icon)))
				}

		addGradleTaskDescription(project, "run", "starts the application.")
		addGradleTaskDescription(project, "jar", "builds application's runnable jar, which can be found at `${id}/build/libs`.")
	}
}

/**
 * Gradle file of the desktop project.
 * @author MJ
 */
class DesktopGradleFile(val project: Project) : GradleFile(Desktop.ID) {
	init {
		dependencies.add("project(':${Core.ID}')")
		addDependency("com.badlogicgames.gdx:gdx-backend-lwjgl:\$gdxVersion")
		addDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-desktop")
	}

	override fun getContent(): String = """apply plugin: 'application'

sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]
mainClassName = '${project.basic.rootPackage}.desktop.DesktopLauncher'
eclipse.project.name = appName + '-desktop'
sourceCompatibility = ${project.advanced.desktopJavaVersion}

dependencies {
${joinDependencies(dependencies)}}

jar {
	archiveFileName = "${'$'}{appName}.jar"
	dependsOn configurations.runtimeClasspath
	from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } } 
	manifest {
		attributes 'Main-Class': project.mainClassName
	}
	doLast {
		file(archivePath).setExecutable(true, false)
	}
}

run {
	ignoreExitValue = true
}
"""

}
