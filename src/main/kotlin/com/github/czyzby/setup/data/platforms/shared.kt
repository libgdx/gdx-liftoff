package com.github.czyzby.setup.data.platforms

import com.github.czyzby.setup.data.gradle.GradleFile
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.GdxPlatform

/**
 * Represents shared project, accessible by both client and server application.
 */
@GdxPlatform
class Shared : Platform {
    companion object {
        const val ID = "shared"
    }

    override val id = ID
    override val isStandard = false

    override fun createGradleFile(project: Project): GradleFile = SharedGradleFile(project)

    override fun initiate(project: Project) {
        project.getGradleFile(Core.ID).dependencies.add("project(':$id')")
        if (project.hasPlatform(Server.ID)) {
            project.getGradleFile(Server.ID).dependencies.add("project(':$id')")
        }
        if (project.hasPlatform(GWT.ID)) {
            // Including shared project sources in GWT platform:
            project.getGradleFile(GWT.ID).buildDependencies.add("project(':$id')")
        }
    }
}

/**
 * Represents shared project Gradle file. Should include dependencies that should be available for both server and
 * client applications.
 */
class SharedGradleFile(val project: Project) : GradleFile(Shared.ID) {
    override fun getContent(): String = """eclipse.project.name = appName + '-shared'

dependencies {
${joinDependencies(dependencies)}}
"""
}
