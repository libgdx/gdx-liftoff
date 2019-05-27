package com.github.czyzby.setup.data.libs.unofficial

import com.github.czyzby.setup.data.platforms.Core
import com.github.czyzby.setup.data.platforms.GWT
import com.github.czyzby.setup.data.platforms.Headless
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.Extension

/**
 * UI toolkit.
 * @author Kotcrab
 */
@Extension
class VisUI : ThirdPartyExtension() {
    override val id = "visUi"
    override val defaultVersion = "1.3.0"
    override val url = "https://github.com/kotcrab/VisEditor/wiki/VisUI"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.kotcrab.vis:vis-ui")

        addDependency(project, GWT.ID, "com.kotcrab.vis:vis-ui:sources")
        addGwtInherit(project, "com.kotcrab.vis.vis-ui")
    }
}

/**
 * VisEditor runtime.
 * @author Kotcrab
 */
@Extension
class VisRuntime : ThirdPartyExtension() {
    override val id = "visRuntime"
    override val defaultVersion = "0.3.4"
    override val url = "https://vis.kotcrab.com"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.kotcrab.vis:vis-runtime")

        addDependency(project, GWT.ID, "com.kotcrab.vis:vis-runtime-gwt")
        addDependency(project, GWT.ID, "com.kotcrab.vis:vis-runtime-gwt:sources")
        addDependency(project, GWT.ID, "com.kotcrab.vis:vis-runtime:sources")
        addGwtInherit(project, "com.kotcrab.vis.vis-runtime")

        ArtemisOdb().initiate(project)
        project.properties["artemisOdbVersion"] = "1.3.1"
    }
}

/**
 * UI styling language extension.
 * @author Kotcrab
 */
@Extension
class USL : ThirdPartyExtension() {
    override val id = "usl"
    override val defaultVersion = "0.2.1"
    override val url = "https://github.com/kotcrab/vis-editor/wiki/USL"

    override fun initiateDependencies(project: Project) {
        project.rootGradle.buildDependencies.add("\"com.kotcrab.vis:vis-usl:\$uslVersion\"");

        addDependency(project, Headless.ID, "com.kotcrab.vis.vis-usl")
    }
}
