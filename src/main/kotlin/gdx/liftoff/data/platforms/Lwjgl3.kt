package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.SourceFile
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
      "builds application's runnable jar, which can be found at `$id/build/lib`."
    )
    project.properties["graalHelperVersion"] = "2.0.1"
    project.properties["enableGraalNative"] = "false"

    project.files.add(
      SourceFile(
        projectName = Lwjgl3.ID,
        fileName = "nativeimage.gradle",
        content =
"""
project(":lwjgl3") {
  apply plugin: "org.graalvm.buildtools.native"

  dependencies {
    implementation "io.github.berstanio:gdx-svmhelper-backend-lwjgl3:""" + '$' + """graalHelperVersion"
""" +
          (if (project.extensions.isSelected("gdx-box2d")) "    implementation \"io.github.berstanio:gdx-svmhelper-extension-box2d:\$graalHelperVersion\"\n" else "") +
          (if (project.extensions.isSelected("gdx-bullet")) "    implementation \"io.github.berstanio:gdx-svmhelper-extension-bullet:\$graalHelperVersion\"\n" else "") +
          (if (project.extensions.isSelected("gdx-controllers-lwjgl3")) "    implementation \"io.github.berstanio:gdx-svmhelper-extension-controllers-lwjgl3:\$graalHelperVersion\"\n" else "") +
          (if (project.extensions.isSelected("gdx-freetype")) "    implementation \"io.github.berstanio:gdx-svmhelper-extension-freetype:\$graalHelperVersion\"\n" else "") +
          """  }
  graalvmNative {
    binaries {
      main {
        imageName = appName
        mainClass = project.mainClassName
        requiredVersion = '23.0'
        buildArgs.add("-march=compatibility")
        jvmArgs.addAll("-Dfile.encoding=UTF8")
        sharedLibrary = false
      }
    }
  }

  run {
    doNotTrackState("Running the app should not be affected by Graal.")
  }

  // Modified from https://lyze.dev/2021/04/29/libGDX-Internal-Assets-List/ ; thanks again, Lyze!
  // This creates a resource-config.json file based on the contents of the assets folder (and the libGDX icons).
  // This file is used by Graal Native to embed those specific files.
  // This has to run before nativeCompile, so it runs at the start of an unrelated resource-handling command.
  generateResourcesConfigFile.doFirst {
    def assetsFolder = new File("${'$'}{project.rootDir}/assets/")
    def lwjgl3 = project(':lwjgl3')
    def resFolder = new File("${'$'}{lwjgl3.projectDir}/src/main/resources/META-INF/native-image/${'$'}{lwjgl3.ext.appName}")
    resFolder.mkdirs()
    def resFile = new File(resFolder, "resource-config.json")
    resFile.delete()
    resFile.append(
            ""${'"'}{
  "resources":{
  "includes":[
    {
      "pattern": ".*(""${'"'})
    // This adds every filename in the assets/ folder to a pattern that adds those files as resources.
    fileTree(assetsFolder).each {
      // The backslash-Q and backslash-E escape the start and end of a literal string, respectively.
      resFile.append("\\\\Q${'$'}{it.name}\\\\E|")
    }
    // We also match all of the window icon images this way and the font files that are part of libGDX.
    resFile.append(
            ""${'"'}libgdx.+\\\\.png|lsans.+)"
    }
  ]},
  "bundles":[]
}""${'"'}
    )
  }
}

project(":core") {
  dependencies {
    implementation "io.github.berstanio:gdx-svmhelper-annotations:""" + '$' + """graalHelperVersion"
  }
}
"""
      )
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

  // language=groovy
  override fun getContent(): String = """
buildscript {
  repositories {
    gradlePluginPortal()
  }
  dependencies {
    if(enableGraalNative == 'true') {
      classpath "org.graalvm.buildtools.native:org.graalvm.buildtools.native.gradle.plugin:0.9.28"
    }
  }
}
plugins {
  id "io.github.fourlastor.construo" version "1.1.1"
  id "application"
}
${if (project.rootGradle.plugins.contains("kotlin")) "apply plugin: 'org.jetbrains.kotlin.jvm'\n" else ""}

import io.github.fourlastor.construo.Target

sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]
mainClassName = '${project.basic.rootPackage}.lwjgl3.Lwjgl3Launcher'
application.setMainClass(mainClassName)
eclipse.project.name = appName + '-lwjgl3'
java.sourceCompatibility = ${project.advanced.desktopJavaVersion}
java.targetCompatibility = ${project.advanced.desktopJavaVersion}

dependencies {
${joinDependencies(dependencies)}}

def jarName = "${'$'}{appName}-${'$'}{version}.jar"
def os = System.properties['os.name'].toLowerCase()

run {
  workingDir = rootProject.file('assets').path
  setIgnoreExitValue(true)

  if (os.contains('mac')) jvmArgs += "-XstartOnFirstThread"
}

jar {
// sets the name of the .jar file this produces to the name of the game or app.
  archiveFileName.set(jarName)
// using 'lib' instead of the default 'libs' appears to be needed by jpackageimage.
  destinationDirectory = file("${'$'}{project.layout.buildDirectory.asFile.get().absolutePath}/lib")
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

construo {
    // name of the executable
    name.set(appName)
    // human-readable name, used for example in the `.app` name for macOS
    humanName.set(appName)
    // Optional, defaults to project version
    version.set("0.0.0")

    targets.configure {
      create("linuxX64", Target.Linux) {
        architecture.set(Target.Architecture.X86_64)
        jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_linux_hotspot_17.0.11_9.tar.gz")
      }
      create("macM1", Target.MacOs) {
        architecture.set(Target.Architecture.AARCH64)
        jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_aarch64_mac_hotspot_17.0.11_9.tar.gz")
        // macOS needs an identifier
        identifier.set("i.should.be.changed.before.merge." + appName)
        // Optional: icon for macOS
        macIcon.set(project.file("icons/logo.icns"))
      }
      create("macX64", Target.MacOs) {
        architecture.set(Target.Architecture.X86_64)
        jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_mac_hotspot_17.0.11_9.tar.gz")
        // macOS needs an identifier
        identifier.set("i.should.be.changed.before.merge." + appName)
        // Optional: icon for macOS
        macIcon.set(project.file("icons/logo.icns"))
      }
      create("winX64", Target.Windows) {
        architecture.set(Target.Architecture.X86_64)
        jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.11_9.zip")
      }
    }
}

// Equivalent to the jar task; here for compatibility with gdx-setup.
tasks.register('dist') {
  dependsOn 'jar'
}

if(enableGraalNative == 'true') {
  apply from: file("nativeimage.gradle")
}

  """.trimIndent()
}
