package com.github.czyzby.setup.data.platforms

import com.github.czyzby.setup.data.gradle.GradleFile
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.GdxPlatform

/**
 * Represents core application's project, used by all backends.
 * @author MJ
 */
@GdxPlatform
class Core : Platform {
    companion object {
        const val ID = "core"
    }

    override val id = ID
    override val isGraphical = false
    override fun createGradleFile(project: Project): GradleFile {
        return CoreGradleFile()
    }

    override fun initiate(project: Project) {
        // Core has no external dependencies by default.
    }
}

/**
 * Gradle file of the core project. Should contain all multi-platform dependencies, like "gdx" itself.
 * @author MJ
 */
class CoreGradleFile : GradleFile(Core.ID) {
    init {
        addDependency("com.badlogicgames.gdx:gdx:\$gdxVersion")
    }

    override fun getContent(): String {
        return """[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'
eclipse.project.name = appName + '-core'

dependencies {
${joinDependencies(dependencies)}}
"""
    }
}
