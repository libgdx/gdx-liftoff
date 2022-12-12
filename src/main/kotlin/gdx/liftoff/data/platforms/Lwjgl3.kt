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
  override val description = "Primary desktop platform using LWJGL3."
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
    arrayOf("logo.png", "logo.ico", "logo.icns")
      .forEach { icon ->
        project.files.add(
          CopiedFile(
            projectName = ID,
            path = path("icons", icon),
            original = path("icons", icon)
          )
        )
      }

    addGradleTaskDescription(project, "run", "starts the application.")
    addGradleTaskDescription(
      project,
      "jar",
      "builds application's runnable jar, which can be found at `$id/build/libs`."
    )
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

  override fun getContent(): String = """buildscript {
  repositories {
    gradlePluginPortal()
  }
  dependencies {
// using jpackage only works if the JDK version is 14 or higher.
// your JAVA_HOME environment variable may also need to be a JDK with version 14 or higher.
    if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_14)) {
      classpath "org.beryx:badass-runtime-plugin:1.12.7"
    }
  }
}
if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_14)) {
  apply plugin: 'org.beryx.runtime'
}
else {
  apply plugin: 'application'
}

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
// using 'lib' instead of the default 'libs' appears to be needed by jpackageimage.
  destinationDirectory = file("${'$'}project.buildDir/lib")
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

if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_14)) {
  jpackageImage.dependsOn jar

  runtime {
    options.set(['--strip-debug',
           '--compress', '2',
           '--no-header-files',
           '--no-man-pages',
           '--strip-native-commands',
           '--vm', 'server'])
// you could very easily need more modules than this one.
// use the lwjgl3:suggestModules task to see which modules may be needed.
    modules.set([
        'jdk.unsupported'
    ])
    distDir.set(file(buildDir))
    jpackage {
      imageName = appName
// you can set this to false if you want to build an installer, or keep it as true to build just an app.
      skipInstaller = true
// this may need to be set to a different path if your JAVA_HOME points to a low JDK version.
      jpackageHome = javaHome.getOrElse("")
      mainJar = jar.archiveFileName.get()
      if (os.contains('win')) {
        imageOptions = ["--icon", "icons/logo.ico"]
      } else if (os.contains('nix') || os.contains('nux') || os.contains('bsd')) {
        imageOptions = ["--icon", "icons/logo.png"]
      } else if (os.contains('mac')) {
        imageOptions = ["--icon", "icons/logo.icns", "--java-options", "\"-XstartOnFirstThread\""]
      }
    }
  }
}

// Equivalent to the jar task; here for compatibility with gdx-setup.
task dist(dependsOn: [jar]) {
}
"""
}
