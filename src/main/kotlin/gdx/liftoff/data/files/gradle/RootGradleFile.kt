package gdx.liftoff.data.files.gradle

import gdx.liftoff.data.platforms.Android
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
        buildRepositories.add("mavenLocal()")
        buildRepositories.add("google()")
        buildRepositories.add("gradlePluginPortal()")
        buildRepositories.add("maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }")
        buildRepositories.add("maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots/' }")
    }

    override fun getContent(): String = """buildscript {
	repositories {
${buildRepositories.joinToString(separator = "\n") { "		$it" }}
	}
	dependencies {
${joinDependencies(buildDependencies, type = "classpath", tab = "		")}
		// This follows advice from https://blog.gradle.org/log4j-vulnerability
		constraints {
			classpath("org.apache.logging.log4j:log4j-core") {
				version {
					strictly("[2.17, 3[")
					prefer("2.17.0")
				}
				because("CVE-2021-44228, CVE-2021-45046, CVE-2021-45105: Log4j vulnerable to remote code execution and other critical security vulnerabilities")
			}
		}
	}
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
	dependencies {
		// This follows advice from https://blog.gradle.org/log4j-vulnerability
		constraints {
			implementation("org.apache.logging.log4j:log4j-core") {
				version {
					strictly("[2.17, 3[")
					prefer("2.17.0")
				}
				because("CVE-2021-44228, CVE-2021-45046, CVE-2021-45105: Log4j vulnerable to remote code execution and other critical security vulnerabilities")
			}
		}
	}

}

subprojects {
	version = '${project.advanced.version}'
	ext.appName = '${project.basic.name}'
	repositories {
		mavenCentral()
		maven { url 'https://s01.oss.sonatype.org' }
		mavenLocal()
		gradlePluginPortal()
		maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
		maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots/' }
		maven { url 'https://jitpack.io' }
	}
}

eclipse.project.name = '${project.basic.name}' + '-parent'
"""
}
