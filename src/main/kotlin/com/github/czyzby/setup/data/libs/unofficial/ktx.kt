package com.github.czyzby.setup.data.libs.unofficial

import com.github.czyzby.setup.data.libs.official.Ashley
import com.github.czyzby.setup.data.libs.official.Box2D
import com.github.czyzby.setup.data.platforms.Core
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.Extension

/**
 * Current version of KTX libraries.
 * @author MJ
 */
const val KTX_VERSION = "1.9.6-b7"

/**
 * Kotlin utilities for Scene2D actors API.
 * @author MJ
 */
@Extension
class KtxActors : ThirdPartyExtension() {
    override val id = "ktxActors"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/actors"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-actors")
    }
}

/**
 * General application listener utilities for Kotlin applications.
 * @author MJ
 */
@Extension
class KtxApp : ThirdPartyExtension() {
    override val id = "ktxApp"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/app"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-app")
    }
}

/**
 * Utilities for Ashley entity component system.
 * @author Jkly
 */
@Extension
class KtxAshley : ThirdPartyExtension() {
    override val id = "ktxAshley"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/ashley"

    override fun initiateDependencies(project: Project) {
        Ashley().initiate(project)

        addDependency(project, Core.ID, "io.github.libktx:ktx-ashley")
    }
}

/**
 * Kotlin utilities for assets management.
 * @author MJ
 */
@Extension
class KtxAssets : ThirdPartyExtension() {
    override val id = "ktxAssets"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/assets"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-assets")
    }
}

/**
 * Kotlin coroutines context and utilities for asynchronous operations.
 * @author MJ
 */
@Extension
class KtxAsync : ThirdPartyExtension() {
    override val id = "ktxAsync"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/async"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-async")
    }
}

/**
 * Kotlin Box2D utilities and type-safe builders
 * @author MJ
 */
@Extension
class KtxBox2D : ThirdPartyExtension() {
    override val id = "ktxBox2d"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/box2d"

    override fun initiateDependencies(project: Project) {
        Box2D().initiate(project)

        addDependency(project, Core.ID, "io.github.libktx:ktx-box2d")
    }
}

/**
 * Kotlin utilities for LibGDX collections.
 * @author MJ
 */
@Extension
class KtxCollections : ThirdPartyExtension() {
    override val id = "ktxCollections"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/collections"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-collections")
    }
}

/**
 * Kotlin utilities for internationalization.
 * @author MJ
 */
@Extension
class KtxI18n : ThirdPartyExtension() {
    override val id = "ktxI18n"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/i18n"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-i18n")
    }
}

/**
 * Kotlin dependency injection without reflection usage.
 * @author MJ
 */
@Extension
class KtxInject : ThirdPartyExtension() {
    override val id = "ktxInject"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/inject"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-inject")
    }
}

/**
 * Kotlin utilities for zero-overhead logging.
 * @author MJ
 */
@Extension
class KtxLog : ThirdPartyExtension() {
    override val id = "ktxLog"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/log"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-log")
    }
}

/**
 * Kotlin utilities for math-related classes.
 * @author MJ
 */
@Extension
class KtxMath : ThirdPartyExtension() {
    override val id = "ktxMath"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/math"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-math")
    }
}

/**
 * Kotlin type-safe builders for Scene2D GUI.
 * @author MJ
 */
@Extension
class KtxScene2D : ThirdPartyExtension() {
    override val id = "ktxScene2D"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/scene2d"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-scene2d")
    }
}

/**
 * Kotlin type-safe builders for Scene2D widget styles.
 * @author MJ
 */
@Extension
class KtxStyle : ThirdPartyExtension() {
    override val id = "ktxStyle"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/style"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-style")
    }
}

/**
 * Kotlin type-safe builders for VisUI widgets.
 * @author Kotcrab
 */
@Extension
class KtxVis : ThirdPartyExtension() {
    override val id = "ktxVis"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/vis"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-vis")
    }
}

/**
 * Kotlin type-safe builders for VisUI widget styles.
 * @author MJ
 * @author Kotcrab
 */
@Extension
class KtxVisStyle : ThirdPartyExtension() {
    override val id = "ktxVisStyle"
    override val defaultVersion = KTX_VERSION
    override val url = "https://github.com/libktx/ktx/tree/master/vis-style"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "io.github.libktx:ktx-vis-style")
    }
}
