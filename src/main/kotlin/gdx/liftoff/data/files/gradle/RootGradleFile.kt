package gdx.liftoff.data.files.gradle

import gdx.liftoff.data.libraries.Repository
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.project.Project

/**
 * Gradle file of the root project. Manages build script and global settings.
 */
class RootGradleFile(val project: Project) : GradleFile("") {
    val plugins = mutableSetOf<String>()
    private val buildRepositories = mutableSetOf<String>()

    init {
        buildRepositories.add("mavenLocal()")
        buildRepositories.add("mavenCentral()")
        buildRepositories.add("google()")
        buildRepositories.add("gradlePluginPortal()")
        buildRepositories.add("maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }")
    }

    override fun getContent(): String = """buildscript {
	repositories {
${buildRepositories.joinToString(separator = "\n") { "		$it" }}
	}
	dependencies {
${joinDependencies(buildDependencies, type = "classpath", tab = "		")}	}
}

allprojects {
	apply plugin: 'eclipse'
	apply plugin: 'idea'
}

configure(subprojects${if (project.hasPlatform(Android.ID)) {
        " - project(':android')"
    } else {
        ""
    }}) {
${plugins.joinToString(separator = "\n") { "	apply plugin: '$it'" }}
	sourceCompatibility = ${project.advanced.javaVersion}
	compileJava {
		options.incremental = true
	}
}

subprojects {
	version = '${project.advanced.version}'
	ext.appName = '${project.basic.name}'
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
		maven { url 'https://jitpack.io' }${
    if (project.extensions.thirdParty.any { it.repository == Repository.OTHER })
        "\n\t\tmaven { url 'https://s01.oss.sonatype.org' }" else ""
    }
	}
}

eclipse.project.name = '${project.basic.name}' + '-parent'
"""
}
