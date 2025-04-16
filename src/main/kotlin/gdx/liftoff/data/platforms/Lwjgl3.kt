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
  override val description = "Primary desktop platform using LWJGL3; was called 'desktop' in older docs."
  override val order = ORDER

  // override val isStandard = true
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
            original = path("icons", icon),
          ),
        )
      }
    arrayOf("logo.png", "logo.ico", "logo.icns")
      .forEach { icon ->
        project.files.add(
          CopiedFile(
            projectName = ID,
            path = path("icons", icon),
            original = path("icons", icon),
          ),
        )
      }

    addGradleTaskDescription(project, "run", "starts the application.")
    addGradleTaskDescription(
      project,
      "jar",
      "builds application's runnable jar, which can be found at `$id/build/libs`.",
    )
    project.properties["graalHelperVersion"] = "2.0.1"

    project.files.add(
      SourceFile(
        projectName = Lwjgl3.ID,
        fileName = "nativeimage.gradle",
        content =
"""
project(":lwjgl3") {
  apply plugin: "org.graalvm.buildtools.native"

  graalvmNative {
    binaries {
      main {
        imageName = appName
        mainClass = project.mainClassName
        requiredVersion = '23.0'
        buildArgs.add("-march=compatibility")
        jvmArgs.addAll("-Dfile.encoding=UTF8")
        sharedLibrary = false
        resources.autodetect()
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
""",
      ),
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
  override fun getContent(): String =
    """
buildscript {
  repositories {
    gradlePluginPortal()
  }
  dependencies {
    classpath "io.github.fourlastor:construo:1.7.1"
    if(enableGraalNative == 'true') {
      classpath "org.graalvm.buildtools.native:org.graalvm.buildtools.native.gradle.plugin:0.9.28"
    }
  }
}
plugins {
  id "application"
}
apply plugin: 'io.github.fourlastor.construo'
${if (project.rootGradle.plugins.contains("kotlin")) "apply plugin: 'org.jetbrains.kotlin.jvm'\n" else ""}

import io.github.fourlastor.construo.Target

sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]
mainClassName = '${project.basic.rootPackage}.lwjgl3.Lwjgl3Launcher'
application.setMainClass(mainClassName)
eclipse.project.name = appName + '-lwjgl3'
java.sourceCompatibility = ${project.advanced.desktopJavaVersion}
java.targetCompatibility = ${project.advanced.desktopJavaVersion}
if (JavaVersion.current().isJava9Compatible()) {
        compileJava.options.release.set(${project.advanced.desktopJavaVersion})
}
${if (project.rootGradle.plugins.contains("kotlin")) "kotlin.compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_" + (if (project.advanced.desktopJavaVersion == "8") "1_8" else project.advanced.desktopJavaVersion) + ")\n" else ""}
dependencies {
${joinDependencies(dependencies)}
  if(enableGraalNative == 'true') {
    implementation "io.github.berstanio:gdx-svmhelper-backend-lwjgl3:${'$'}graalHelperVersion"
""" +
      (if (project.extensions.isSelected("gdx-box2d")) "      implementation \"io.github.berstanio:gdx-svmhelper-extension-box2d:\$graalHelperVersion\"\n" else "") +
      (if (project.extensions.isSelected("gdx-bullet")) "      implementation \"io.github.berstanio:gdx-svmhelper-extension-bullet:\$graalHelperVersion\"\n" else "") +
      (if (project.extensions.isSelected("gdx-controllers-lwjgl3")) "      implementation \"io.github.berstanio:gdx-svmhelper-extension-controllers-lwjgl3:\$graalHelperVersion\"\n" else "") +
      (if (project.extensions.isSelected("gdx-freetype")) "      implementation \"io.github.berstanio:gdx-svmhelper-extension-freetype:\$graalHelperVersion\"\n" else "") +
      """
    }
}

def os = System.properties['os.name'].toLowerCase()

run {
  workingDir = rootProject.file('assets').path
// You can uncomment the next line if your IDE claims a build failure even when the app closed properly.
  //setIgnoreExitValue(true)

  if (os.contains('mac')) jvmArgs += "-XstartOnFirstThread"
}

jar {
// sets the name of the .jar file this produces to the name of the game or app, with the version after.
  archiveFileName.set("${'$'}{appName}-${'$'}{projectVersion}.jar")
// the duplicatesStrategy matters starting in Gradle 7.0; this setting works.
  duplicatesStrategy(DuplicatesStrategy.EXCLUDE)
  dependsOn configurations.runtimeClasspath
  from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
// these "exclude" lines remove some unnecessary duplicate files in the output JAR.
  exclude('META-INF/INDEX.LIST', 'META-INF/*.SF', 'META-INF/*.DSA', 'META-INF/*.RSA')
  dependencies {
    exclude('META-INF/INDEX.LIST', 'META-INF/maven/**'""" +
      (if (project.advanced.gdxVersion == "1.13.0") " 'windows/x86/**'" else "") +
"""
)
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

// Builds a JAR that only includes the files needed to run on macOS, not Windows or Linux.
// The file size for a Mac-only JAR is about 7MB smaller than a cross-platform JAR.
tasks.register("jarMac") {
  dependsOn("jar")
  group("build")
  jar.archiveFileName.set("${'$'}{appName}-${'$'}{projectVersion}-mac.jar")
  jar.exclude("windows/x86/**", "windows/x64/**", "linux/arm32/**", "linux/arm64/**", "linux/x64/**", "**/*.dll", "**/*.so",
    'META-INF/INDEX.LIST', 'META-INF/*.SF', 'META-INF/*.DSA', 'META-INF/*.RSA')
  dependencies {
    jar.exclude("windows/x86/**", "windows/x64/**", "linux/arm32/**", "linux/arm64/**", "linux/x64/**",
      'META-INF/INDEX.LIST', 'META-INF/maven/**')
  }
}

// Builds a JAR that only includes the files needed to run on Linux, not Windows or macOS.
// The file size for a Linux-only JAR is about 5MB smaller than a cross-platform JAR.
tasks.register("jarLinux") {
  dependsOn("jar")
  group("build")
  jar.archiveFileName.set("${'$'}{appName}-${'$'}{projectVersion}-linux.jar")
  jar.exclude("windows/x86/**", "windows/x64/**", "macos/arm64/**", "macos/x64/**", "**/*.dll", "**/*.dylib",
    'META-INF/INDEX.LIST', 'META-INF/*.SF', 'META-INF/*.DSA', 'META-INF/*.RSA')
  dependencies {
    jar.exclude("windows/x86/**", "windows/x64/**", "macos/arm64/**", "macos/x64/**",
      'META-INF/INDEX.LIST', 'META-INF/maven/**')
  }
}

// Builds a JAR that only includes the files needed to run on Windows, not Linux or macOS.
// The file size for a Windows-only JAR is about 6MB smaller than a cross-platform JAR.
tasks.register("jarWin") {
  dependsOn("jar")
  group("build")
  jar.archiveFileName.set("${'$'}{appName}-${'$'}{projectVersion}-win.jar")
  jar.exclude("macos/arm64/**", "macos/x64/**", "linux/arm32/**", "linux/arm64/**", "linux/x64/**", "**/*.dylib", "**/*.so",
    'META-INF/INDEX.LIST', 'META-INF/*.SF', 'META-INF/*.DSA', 'META-INF/*.RSA')
  dependencies {
    jar.exclude("macos/arm64/**", "macos/x64/**", "linux/arm32/**", "linux/arm64/**", "linux/x64/**",
      'META-INF/INDEX.LIST', 'META-INF/maven/**')
  }
}

construo {
    // name of the executable
    name.set(appName)
    // human-readable name, used for example in the `.app` name for macOS
    humanName.set(appName)
    // Optional, defaults to project version property
    version.set("${'$'}projectVersion")

    targets.configure {
      create("linuxX64", Target.Linux) {
        architecture.set(Target.Architecture.X86_64)
        jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.14%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.14_7.tar.gz")
        // Linux does not currently have a way to set the icon on the executable
      }
      create("macM1", Target.MacOs) {
        architecture.set(Target.Architecture.AARCH64)
        jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.14%2B7/OpenJDK17U-jdk_aarch64_mac_hotspot_17.0.14_7.tar.gz")
        // macOS needs an identifier
        identifier.set("${project.basic.rootPackage}." + appName)
        // Optional: icon for macOS, as an ICNS file
        macIcon.set(project.file("icons/logo.icns"))
      }
      create("macX64", Target.MacOs) {
        architecture.set(Target.Architecture.X86_64)
        jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.14%2B7/OpenJDK17U-jdk_x64_mac_hotspot_17.0.14_7.tar.gz")
        // macOS needs an identifier
        identifier.set("${project.basic.rootPackage}." + appName)
        // Optional: icon for macOS, as an ICNS file
        macIcon.set(project.file("icons/logo.icns"))
      }
      create("winX64", Target.Windows) {
        architecture.set(Target.Architecture.X86_64)
        // Optional: icon for Windows, as a PNG
        icon.set(file("icons/logo.png"))
        jdkUrl.set("https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.14%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.14_7.zip")
        // Uncomment the next line to show a console when the game runs, to print messages.
        //useConsole.set(true)
      }
    }
}

// Equivalent to the jar task; here for compatibility with gdx-setup.
tasks.register('dist') {
  dependsOn 'jar'
}

distributions {
  main {
    contents {
      into('libs') {
        project.configurations.runtimeClasspath.files.findAll { file ->
          file.getName() != project.tasks.jar.outputs.files.singleFile.name
        }.each { file ->
          exclude file.name
        }
      }
    }
  }
}

startScripts.dependsOn(':lwjgl3:jar')
startScripts.classpath = project.tasks.jar.outputs.files

if(enableGraalNative == 'true') {
  apply from: file("nativeimage.gradle")
}

""".trimIndent()
}
