package com.github.czyzby.setup.data.libs.unofficial

import com.github.czyzby.setup.data.platforms.Core
import com.github.czyzby.setup.data.platforms.GWT
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.Extension


/**
 * Version of SquidLib libraries.
 * @author Eben Howard
 * @author Tommy Ettinger
 */
const val SQUID_LIB_VERSION = "88360e8df6"

/**
 * URL of SquidLib libraries.
 * @author Eben Howard
 * @author Tommy Ettinger
 */
const val SQUID_LIB_URL = "https://github.com/SquidPony/SquidLib"

const val REPO_PATH = "com.github.SquidPony.SquidLib"
//const val REPO_PATH = "com.squidpony"

/**
 * Cross-platform regex utilities.
 * @author Tommy Ettinger
 */
@Extension()
class RegExodus : ThirdPartyExtension() {
    override val id = "regExodus"
    override val defaultVersion = "0.1.10"
    override val url = "https://github.com/tommyettinger/RegExodus"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.tommyettinger:regexodus")

        addDependency(project, GWT.ID, "com.github.tommyettinger:regexodus:sources")
        addGwtInherit(project, "regexodus")
    }
}
/**
 * Utilities for grid-based games.
 * @author Eben Howard
 * @author Tommy Ettinger
 */
@Extension()
class SquidLibUtil : ThirdPartyExtension() {
    override val id = "squidLibUtil"
    override var defaultVersion = SQUID_LIB_VERSION
    override val url = SQUID_LIB_URL

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$REPO_PATH:squidlib-util")

        addDependency(project, GWT.ID, "$REPO_PATH:squidlib-util:sources")
        addGwtInherit(project, "squidlib-util")

        RegExodus().initiate(project)
    }
}

/**
 * Text-based display for roguelike games.
 * @author Eben Howard
 * @author Tommy Ettinger
 */
@Extension()
class SquidLib : ThirdPartyExtension() {
    override val id = "squidLib"
    override var defaultVersion = SQUID_LIB_VERSION
    override val url = SQUID_LIB_URL

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$REPO_PATH:squidlib")

        addDependency(project, GWT.ID, "$REPO_PATH:squidlib:sources")
        addGwtInherit(project, "squidlib")

        SquidLibUtil().initiate(project)
        defaultVersion = SQUID_LIB_VERSION
    }
}

/**
 * Extra save/load support for SquidLib objects.
 * @author Eben Howard
 * @author Tommy Ettinger
 */
@Extension()
class SquidLibExtra : ThirdPartyExtension() {
    override val id = "squidLibExtra"
    override var defaultVersion = SQUID_LIB_VERSION
    override val url = SQUID_LIB_URL

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "$REPO_PATH:squidlib-extra")

        addDependency(project, GWT.ID, "$REPO_PATH:squidlib-extra:sources")
        addGwtInherit(project, "squidlib-extra")

        SquidLibUtil().initiate(project)
        defaultVersion = SQUID_LIB_VERSION
    }
}
