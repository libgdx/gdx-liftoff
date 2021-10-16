package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents the LWJGL3 backend, which runs on all desktop platforms and supports more features than LWJGL2.
 */
@GdxPlatform
class Lwjgl3 : Platform {
    companion object {
        const val ID = "lwjgl3"
        const val ORDER = Core.ORDER + 1
    }

    override val id = ID
    override val order = ORDER
    // override val isStandard = true // true is the default, and we want to prefer this to desktop
    override fun createGradleFile(project: Project): GradleFile = Lwjgl3GradleFile(project)
    override fun initiate(project: Project) {
        // Adding game icons:
        arrayOf(16, 32, 64, 128)
            .map { "libgdx$it.png" }
            .forEach { icon ->
                project.files.add(
                    CopiedFile(
                        projectName = ID,
                        path = path("src", "main", "resources", icon),
                        original = path("icons", icon)
                    )
                )
            }

        addGradleTaskDescription(project, "run", "starts the application.")
        addGradleTaskDescription(project, "jar", "builds application's runnable jar, which can be found at `$id/build/libs`.")
    }
}

/**
 * Gradle file of the LWJGL3 project.
 */
class Lwjgl3GradleFile(val project: Project) : GradleFile(Lwjgl3.ID) {
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

def os = System.properties['os.name'].toLowerCase()

run {
	workingDir = rootProject.file('assets').path
	setIgnoreExitValue(true)

	if (os.contains('mac')) {
		// Required to run LWJGL3 Java apps on MacOS
		jvmArgs += "-XstartOnFirstThread"
	}
}

jar {
// sets the name of the .jar file this produces to the name of the game or app.
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
"""
}
