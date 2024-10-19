package gdx.liftoff.data.files.gradle

import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.TeaVM
import gdx.liftoff.data.project.Project

/**
 * Gradle file of the root project. Manages build script and global settings.
 */
class RootGradleFile(val project: Project) : GradleFile("") {
  val plugins = mutableSetOf<String>()
  private val buildRepositories = mutableSetOf<String>()

  init {
    buildRepositories.add("mavenCentral()")
    buildRepositories.add("maven { url 'https://s01.oss.sonatype.org' }")
    buildRepositories.add("gradlePluginPortal()")
    buildRepositories.add("mavenLocal()")
    buildRepositories.add("google()")
    buildRepositories.add("maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }")
    buildRepositories.add("maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots/' }")
  }

  override fun getContent(): String = """buildscript {
  repositories {
${buildRepositories.joinToString(separator = "\n") { "    $it" }}
  }
  dependencies {
${joinDependencies(buildDependencies, type = "classpath", indent = "    ")}
  }
}

allprojects {
  apply plugin: 'eclipse'
  apply plugin: 'idea'

  // This allows you to "Build and run using IntelliJ IDEA", an option in IDEA's Settings.
  idea {
    module {
      outputDir file('build/classes/java/main')
      testOutputDir file('build/classes/java/test')
    }
  }
}

subprojects {
  version = '${'$'}projectVersion'
  ext.appName = '${project.basic.name}'
}

eclipse.project.name = '${project.basic.name}' + '-parent'
"""
}
/*
  repositories {
    mavenCentral()
    maven { url 'https://s01.oss.sonatype.org' }
    // You may want to remove the following line if you have errors downloading dependencies.
    mavenLocal()
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots/' }
    maven { url 'https://jitpack.io' }${
  if (project.hasPlatform(TeaVM.ID)) {
    "\n    maven { url 'https://teavm.org/maven/repository/' }"
  } else {
    ""
  }}
  }
 */
