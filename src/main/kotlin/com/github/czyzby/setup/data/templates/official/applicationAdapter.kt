package com.github.czyzby.setup.data.templates.official

import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Extends ApplicationAdapter, overriding no methods. Application does nothing.
 * @author MJ
 */
@ProjectTemplate(official = true)
class ApplicationAdapterTemplate : Template {
    override val id = "applicationAdapter"
    override val description: String
        get() = "Project template included simple launchers and an empty `ApplicationAdapter` extension."

    override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationAdapter;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends ApplicationAdapter {
}"""
}
