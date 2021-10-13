package com.github.czyzby.setup.data.templates.official

import com.github.czyzby.setup.data.files.CopiedFile
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.platforms.Assets
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Generates no source files.
 */
@ProjectTemplate(official = true)
@Suppress("unused") // Referenced via reflection.
class EmptyTemplate : Template {
    override val id = "emptyTemplate"
    override val description: String
        get() = "No sources were generated."

    override fun apply(project: Project) {
        super.apply(project)
        project.files.add(
            CopiedFile(projectName = Assets.ID, original = path("generator", "assets",
                ".gitkeep"), path = ".gitkeep")
        )
    }

    override fun getApplicationListenerContent(project: Project): String = ""
}
