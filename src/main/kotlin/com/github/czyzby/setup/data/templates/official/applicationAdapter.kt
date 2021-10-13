package com.github.czyzby.setup.data.templates.official

import com.github.czyzby.setup.data.files.CopiedFile
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.platforms.Assets
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Extends ApplicationAdapter, overriding no methods. Application does nothing.
 */
@ProjectTemplate(official = true)
@Suppress("unused") // Referenced via reflection.
class ApplicationAdapterTemplate : Template {
    override val id = "applicationAdapter"
    override val description: String
        get() = "Project template included simple launchers and an empty `ApplicationAdapter` extension."

    override fun apply(project: Project) {
        super.apply(project)
        project.files.add(
            CopiedFile(projectName = Assets.ID, original = path("generator", "assets",
            ".gitkeep"), path = ".gitkeep")
        )
    }

    override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationAdapter;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends ApplicationAdapter {
}"""
}
