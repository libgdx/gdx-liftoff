package com.github.czyzby.setup.data.langs

import com.github.czyzby.setup.data.files.SourceDirectory
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.JvmLanguage

/**
 * Adds Groovy support to the project.
 * @author MJ
 */
@JvmLanguage
class Groovy : Language {
    override val id = "groovy"
    override val version = "2.4.+"

    override fun initiate(project: Project) {
        project.rootGradle.plugins.add(id)
        project.platforms.values.forEach { project.files.add(SourceDirectory(it.id, path("src", "main", "groovy"))) }
        addDependency(project, "org.codehaus.groovy:groovy-all:\$groovyVersion")
    }
}