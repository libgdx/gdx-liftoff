package com.github.czyzby.setup.data.langs

import com.github.czyzby.setup.data.files.SourceDirectory
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.JvmLanguage

/**
 * Adds Scala support to the project.
 * @author MJ
 */
@JvmLanguage
class Scala : Language {
    override val id = "scala"
    override val version = "2.13.+"

    override fun initiate(project: Project) {
        project.rootGradle.plugins.add(id)
        project.platforms.values.forEach { project.files.add(SourceDirectory(it.id, path("src", "main", "scala"))) }
        addDependency(project, "org.scala-lang:scala-library:\$scalaVersion")
    }
}
