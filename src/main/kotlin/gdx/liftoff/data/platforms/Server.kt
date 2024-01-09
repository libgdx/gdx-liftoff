package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents server application project.
 */
@GdxPlatform
class Server : Platform {
  companion object {
    const val ID = "server"
    const val ORDER = Lwjgl2.ORDER + 1
  }

  override val id = ID
  override val description = "A separate application without access to the `core` module."
  override val order = ORDER
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
 */
class ServerGradleFile(val project: Project) : GradleFile(Server.ID) {
  override fun getContent(): String = """apply plugin: 'application'
${if (project.rootGradle.plugins.contains("kotlin")) "apply plugin: 'org.jetbrains.kotlin.jvm'\n" else ""}

java.sourceCompatibility = ${project.advanced.serverJavaVersion}
java.targetCompatibility = ${project.advanced.serverJavaVersion}
mainClassName = '${project.basic.rootPackage}.server.ServerLauncher'
application.setMainClass(mainClassName)
eclipse.project.name = appName + '-server'

dependencies {
${joinDependencies(dependencies)}}

jar {
  archiveBaseName.set(appName)
// the duplicatesStrategy matters starting in Gradle 7.0; this setting works.
  duplicatesStrategy(DuplicatesStrategy.EXCLUDE)
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

// Equivalent to the jar task; here for compatibility with gdx-setup.
task dist(dependsOn: [jar]) {
}
"""
}
