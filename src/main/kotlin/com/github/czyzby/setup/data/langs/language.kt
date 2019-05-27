package com.github.czyzby.setup.data.langs

import com.github.czyzby.setup.data.platforms.Core
import com.github.czyzby.setup.data.platforms.Shared
import com.github.czyzby.setup.data.project.Project

/**
 * Common interface for additional JVM languages. If the language is optional, its class should be annotated with
 * JvmLanguage.
 * @author MJ
 */
interface Language {
    val id: String
    val version: String

    /**
     * Adds language-specific dependencies and plugins.
     * @param project is being generated.
     */
    fun initiate(project: Project)

    fun addDependency(project: Project, dependency: String) {
        project.getGradleFile(Core.ID).addDependency(dependency)
        if (project.hasPlatform(Shared.ID)) {
            project.getGradleFile(Shared.ID).addDependency(dependency)
        }
        if (project.hasPlatform(Shared.ID)) {
            project.getGradleFile(Shared.ID).addDependency(dependency)
        }
    }
}