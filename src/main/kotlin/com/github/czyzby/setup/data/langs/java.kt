package com.github.czyzby.setup.data.langs

import com.github.czyzby.setup.data.files.SourceDirectory
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.project.Project

/**
 * Adds Java support to the project.
 */
class Java : Language {
    override val id = "java-library"
    override val version = "1.8"

    override fun initiate(project: Project) {
        project.rootGradle.plugins.add(id)
        project.platforms.values.forEach { project.files.add(SourceDirectory(it.id, path("src", "main", "java"))) }
    }
}
