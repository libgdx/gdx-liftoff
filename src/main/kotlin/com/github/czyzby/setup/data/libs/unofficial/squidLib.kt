@file:Suppress("unused") // Extension classes accessed via reflection.

package com.github.czyzby.setup.data.libs.unofficial

import com.github.czyzby.setup.data.libs.camelCaseToKebabCase
import com.github.czyzby.setup.data.platforms.Core
import com.github.czyzby.setup.data.platforms.GWT
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.Extension

/**
 * Base class of SquidLib libraries.
 * @author Eben Howard
 * @author Tommy Ettinger
 */
abstract class SquidLibExtension: ThirdPartyExtension() {
    override val defaultVersion = "3.0.4"
    override val group = "com.squidpony"
    override val name: String
        get() = id.camelCaseToKebabCase()
    override val url = "https://github.com/SquidPony/SquidLib"
}

/**
 * Utilities for grid-based games.
 * @author Eben Howard
 * @author Tommy Ettinger
 */
@Extension
class SquidLibUtil : SquidLibExtension() {
    override val id = "squidlibUtil"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group:$name")

        addDependency(project, GWT.ID, "$group:$name:sources")
        addGwtInherit(project, "squidlib-util")

        RegExodus().initiate(project)
    }
}

/**
 * Text-based display for roguelike games.
 * @author Eben Howard
 * @author Tommy Ettinger
 */
@Extension
class SquidLib : SquidLibExtension() {
    override val id = "squidlib"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group:$name")

        addDependency(project, GWT.ID, "$group:$name:sources")
        addGwtInherit(project, "squidlib")

        SquidLibUtil().initiate(project)
        Anim8().initiate(project)
    }
}

/**
 * Extra save/load support for SquidLib objects.
 * @author Eben Howard
 * @author Tommy Ettinger
 */
@Extension
class SquidLibExtra : SquidLibExtension() {
    override val id = "squidlibExtra"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$group:$name")

        addDependency(project, GWT.ID, "$group:$name:sources")
        addGwtInherit(project, "squidlib-extra")

        SquidLibUtil().initiate(project)
    }
}
