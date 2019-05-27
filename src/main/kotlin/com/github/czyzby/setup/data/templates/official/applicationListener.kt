package com.github.czyzby.setup.data.templates.official

import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Adds empty implementation of all ApplicationListener methods. Application does nothing.
 * @author MJ
 */
@ProjectTemplate(official = true)
class ApplicationListenerTemplate : Template {
    override val id = "applicationListener"
    override val description: String
        get() = "Project template included simple launchers and an empty `ApplicationListener` implementation."

    override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationListener;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} implements ApplicationListener {
    @Override
    public void create() {
        // Prepare your application here.
    }

    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
    }

    @Override
    public void render() {
        // Draw your application here.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}"""
}
