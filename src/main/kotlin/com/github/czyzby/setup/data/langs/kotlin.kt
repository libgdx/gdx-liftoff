package com.github.czyzby.setup.data.langs

import com.github.czyzby.setup.data.files.SourceDirectory
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.platforms.Android
import com.github.czyzby.setup.data.platforms.AndroidGradleFile
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.JvmLanguage

/**
 * Adds Kotlin support to the project.
 * @author MJ
 */
@JvmLanguage
class Kotlin : Language {
    override val id = "kotlin"
    override val version = "1.1.+"

    override fun initiate(project: Project) {
        project.rootGradle.buildDependencies.add("\"org.jetbrains.kotlin:kotlin-gradle-plugin:\$kotlinVersion\"")
        project.rootGradle.plugins.add(id)
        project.platforms.values.forEach { project.files.add(SourceDirectory(it.id, path("src", "main", "kotlin"))) }
        if (project.hasPlatform(Android.ID)) {
            val gradleFile = project.getGradleFile(Android.ID) as AndroidGradleFile
            gradleFile.plugins.add("kotlin-android")
        }
        addDependency(project, "org.jetbrains.kotlin:kotlin-stdlib:\$kotlinVersion")
    }
}
