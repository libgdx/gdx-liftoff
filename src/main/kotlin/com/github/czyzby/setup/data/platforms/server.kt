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
	archiveBaseName.set(appName)
// the duplicatesStrategy will matter starting in Gradle 7.0; this setting works.
	duplicatesStrategy(DuplicatesStrategy.INCLUDE)
	dependsOn configurations.runtimeClasspath
	from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
// these "exclude" lines remove some unnecessary duplicate files in the output JAR.
	exclude('META-INF/INDEX.LIST', 'META-INF/*.SF', 'META-INF/*.DSA', 'META-INF/*.RSA')
	dependencies {
		exclude('META-INF/INDEX.LIST', 'META-INF/maven/**')
	}
// setting the manifest makes the JAR runnable.
	manifest {
		attributes 'Main-Class': project.mainClassName
	}
// this last step may help on some OSes that need extra instruction to make runnable JARs.
	doLast {
		file(archiveFile).setExecutable(true, false)
	}
}
"""
}
