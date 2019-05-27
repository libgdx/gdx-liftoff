package com.github.czyzby.setup.data.templates.official

import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Generates no source files.
 * @author MJ
 */
@ProjectTemplate(official = true)
class EmptyTemplate : Template {
    override val id = "emptyTemplate"
    override val description: String
        get() = "No sources were generated."

    override fun apply(project: Project) {
        // Does nothing.
    }

    override fun getApplicationListenerContent(project: Project): String = ""
}
