package com.github.czyzby.setup.data.platforms

import com.github.czyzby.setup.data.gradle.GradleFile
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.GdxPlatform

/**
 * Represents server application project.
 * @author MJ
 */
@GdxPlatform
class Server : Platform {
    companion object {
        const val ID = "server"
    }

    override val id = ID
    override val isStandard = false

    override fun createGradleFile(project: Project): GradleFile = ServerGradleFile(project)

    override fun initiate(project: Project) {
        // Server project has no additional dependencies.

        addGradleTaskDescription(project, "run", "runs the $id application.")
    }
}

/**
 * Represents the Gradle file of server project. Allows to set up a different Java version and launch the application
 * with "run" task.
 * @author MJ
 */
class ServerGradleFile(val project: Project) : GradleFile(Server.ID) {
    override fun getContent(): String = """apply plugin: 'application'

sourceCompatibility = ${project.advanced.serverJavaVersion}
mainClassName = '${project.basic.rootPackage}.server.ServerLauncher'
eclipse.project.name = appName + '-server'

dependencies {
${joinDependencies(dependencies)}}

jar {
	archiveFileName = "${'$'}{appName}-server-${'$'}{archiveVersion.get()}.jar"
	dependsOn configurations.runtimeClasspath
	from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } } 
	manifest {
		attributes 'Main-Class': project.mainClassName
	}
}"""
}
