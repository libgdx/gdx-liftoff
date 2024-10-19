package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.languages.Kotlin
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents core application's project, used by all backends.
 */
@GdxPlatform
class Core : Platform {
  companion object {
    const val ID = "core"
    const val ORDER = 0
  }

  override val id = ID
  override val description = "Main module with the application logic shared by all platforms."
  override val order = ORDER
  override val isStandard = false
  override fun createGradleFile(project: Project): GradleFile {
    return CoreGradleFile(project)
  }

  override fun initiate(project: Project) {
    project.properties["enableGraalNative"] = "false"
    // Core has no external dependencies by default.
  }
}

/**
 * Gradle file of the core project. Should contain all multi-platform dependencies, like "gdx" itself.
 */
class CoreGradleFile(val project: Project) : GradleFile(Core.ID) {
  init {
    addDependency("com.badlogicgames.gdx:gdx:\$gdxVersion")
  }

  override fun getContent(): String {
    return """
plugins {
  id "java-library"
${if ( project.rootGradle.plugins.contains("kotlin")) "  id 'org.jetbrains.kotlin.jvm' version '${project.languages.getVersion("kotlin")}'\n" else ""}}

// From https://lyze.dev/2021/04/29/libGDX-Internal-Assets-List/
// The article can be helpful when using assets.txt in your project.
tasks.register('generateAssetList') {
  inputs.dir("${'$'}{project.rootDir}/assets/")
  // projectFolder/assets
  File assetsFolder = new File("${'$'}{project.rootDir}/assets/")
  // projectFolder/assets/assets.txt
  File assetsFile = new File(assetsFolder, "assets.txt")
  // delete that file in case we've already created it
  assetsFile.delete()

  // iterate through all files inside that folder
  // convert it to a relative path
  // and append it to the file assets.txt
  fileTree(assetsFolder).collect { assetsFolder.relativePath(it) }.sort().each {
    assetsFile.append(it + "\n")
  }
}
processResources.dependsOn 'generateAssetList'

compileJava {
  options.incremental = true
}${if (project.rootGradle.plugins.contains("kotlin")) {
    """
compileKotlin.compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_${
      if (project.advanced.javaVersion.removePrefix("1.") == "8") {
        "1_8"
      } else {
        project.advanced.javaVersion.removePrefix("1.")
      }})
"""
    } else {
      ""
    }}

[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'
eclipse.project.name = appName + '-core'

java.sourceCompatibility = ${project.advanced.javaVersion}
java.targetCompatibility = ${project.advanced.javaVersion}

dependencies {
${joinDependencies(dependencies, "api")}
  if(enableGraalNative == 'true') {
    implementation "io.github.berstanio:gdx-svmhelper-annotations:${'$'}graalHelperVersion"
  }
}
"""
  }
}
