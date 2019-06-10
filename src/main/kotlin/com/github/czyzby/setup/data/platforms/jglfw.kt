package com.github.czyzby.setup.data.platforms

import com.github.czyzby.setup.data.gradle.GradleFile
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.GdxPlatform


/**
 * Represents alternative desktop platforum using JGLFW.
 * @author MJ
 */
@GdxPlatform
class JGLFW : Platform {
    companion object {
        const val ID = "jglfw"
    }

    override val id = ID
    override val isStandard = false // JGLFW is an alternative to the default desktop project.
    override fun createGradleFile(project: Project): GradleFile = JglfwGradleFile(project)
    override fun initiate(project: Project) {
        // JGLFW platform requires no additional dependencies.

        addGradleTaskDescription(project, "run", "starts the application.")
        addGradleTaskDescription(project, "jar", "builds application's runnable jar, which can be found at `${id}/build/libs`.")
    }
}

/**
 * Gradle file of the JGLFW project.
 * @author MJ
 */
class JglfwGradleFile(val project: Project) : GradleFile(JGLFW.ID) {
    init {
        dependencies.add("project(':${Core.ID}')")
        addDependency("com.badlogicgames.gdx:gdx-backend-jglfw:\$gdxVersion")
        addDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-desktop")
    }

    override fun getContent(): String = """apply plugin: 'application'

sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]
mainClassName = '${project.basic.rootPackage}.jglfw.JglfwLauncher'
eclipse.project.name = appName + '-jglfw'
sourceCompatibility = ${project.advanced.desktopJavaVersion}

dependencies {
${joinDependencies(dependencies)}}

jar {
	archiveFileName = "${'$'}{appName}-${'$'}{version}.jar"
	from files(sourceSets.main.output.classesDirs)
	from { configurations.compileClasspath.collect { it.isDirectory() ? it : zipTree(it) } } 
	manifest {
		attributes 'Main-Class': project.mainClassName
	}
}

run {
	ignoreExitValue = true
}
"""

}
