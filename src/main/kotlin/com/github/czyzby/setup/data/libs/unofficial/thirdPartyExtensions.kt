package com.github.czyzby.setup.data.libs.unofficial

import com.github.czyzby.setup.data.libs.Library
import com.github.czyzby.setup.data.platforms.*
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.Extension

/**
 * Abstract base for unofficial extensions.
 * @author MJ
 */
abstract class ThirdPartyExtension : Library {
    override val official = false

    override fun initiate(project: Project) {
        project.properties[id + "Version"] = project.extensions.getVersion(id)
        initiateDependencies(project)
    }

    abstract fun initiateDependencies(project: Project)

    override fun addDependency(project: Project, platform: String, dependency: String) {
        if (dependency.endsWith(":sources")) {
            super.addDependency(project, platform, dependency.replace(":sources", ":\$${id}Version:sources"))
        } else {
            super.addDependency(project, platform, dependency + ":\$${id}Version")
        }
    }

    fun addExternalDependency(project: Project, platform: String, dependency: String) {
        super.addDependency(project, platform, dependency)
    }
}

/**
 * High performance Entity-Component-System framework.
 * @author junkdog
 */
@Extension
class ArtemisOdb : ThirdPartyExtension() {
    override val id = "artemisOdb"
    override val defaultVersion = "2.3.0"
    override val url = "https://github.com/junkdog/artemis-odb"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "net.onedaybeard.artemis:artemis-odb");

        addDependency(project, GWT.ID, "net.onedaybeard.artemis:artemis-odb-gwt")
        addDependency(project, GWT.ID, "net.onedaybeard.artemis:artemis-odb-gwt:sources")
        addDependency(project, GWT.ID, "net.onedaybeard.artemis:artemis-odb:sources")
        addGwtInherit(project, "com.artemis.backends.artemis_backends_gwt")
    }
}

/**
 * General LibGDX utilities.
 * @author Dermetfan
 * @author Maintained by Tommy Ettinger
 */
@Extension
class LibgdxUtils : ThirdPartyExtension() {
    override val id = "utils"
    override val defaultVersion = "0.13.6"
    override val url = "http://dermetfan.net/libgdx-utils.php"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.tommyettinger:libgdx-utils")

        addDependency(project, GWT.ID, "com.github.tommyettinger:libgdx-utils:sources")
        addGwtInherit(project, "libgdx-utils")
    }
}

/**
 * Box2D LibGDX utilities.
 * @author Dermetfan
 * @author Maintained by Tommy Ettinger
 */
@Extension
class LibgdxUtilsBox2D : ThirdPartyExtension() {
    override val id = "utilsBox2d"
    override val defaultVersion = "0.13.6"
    override val url = "http://dermetfan.net/libgdx-utils.php"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.tommyettinger:libgdx-utils-box2d")

        addDependency(project, GWT.ID, "com.github.tommyettinger:libgdx-utils-box2d:sources")
        addGwtInherit(project, "libgdx-utils-box2d")

        LibgdxUtils().initiate(project)
    }
}

/**
 * Facebook graph API wrapper.
 * @author Tom Grill
 */
@Extension
class Facebook : ThirdPartyExtension() {
    override val id = "facebook"
    override val defaultVersion = "1.5.0"
    override val url = "https://github.com/TomGrill/gdx-facebook"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "de.tomgrill.gdxfacebook:gdx-facebook-core")

        addDependency(project, Android.ID, "de.tomgrill.gdxfacebook:gdx-facebook-android")

        addDesktopDependency(project, "de.tomgrill.gdxfacebook:gdx-facebook-desktop")

        addDependency(project, iOS.ID, "de.tomgrill.gdxfacebook:gdx-facebook-ios")
        addDependency(project, MOE.ID, "de.tomgrill.gdxfacebook:gdx-facebook-ios-moe")

        addDependency(project, GWT.ID, "de.tomgrill.gdxfacebook:gdx-facebook-core:sources")
        addDependency(project, GWT.ID, "de.tomgrill.gdxfacebook:gdx-facebook-html")
        addDependency(project, GWT.ID, "de.tomgrill.gdxfacebook:gdx-facebook-html:sources")
        addGwtInherit(project, "de.tomgrill.gdxfacebook.html.gdx_facebook_gwt")
    }
}

/**
 * Native dialogs support.
 * @author Tom Grill
 */
@Extension
class Dialogs : ThirdPartyExtension() {
    override val id = "dialogs"
    override val defaultVersion = "1.3.0"
    override val url = "https://github.com/TomGrill/gdx-dialogs"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "de.tomgrill.gdxdialogs:gdx-dialogs-core")

        addDependency(project, Android.ID, "de.tomgrill.gdxdialogs:gdx-dialogs-android")

        addDesktopDependency(project, "de.tomgrill.gdxdialogs:gdx-dialogs-desktop")

        addDependency(project, iOS.ID, "de.tomgrill.gdxdialogs:gdx-dialogs-ios")
        addDependency(project, MOE.ID, "de.tomgrill.gdxdialogs:gdx-dialogs-ios-moe")

        addDependency(project, GWT.ID, "de.tomgrill.gdxfacebook:gdx-dialogs-core:sources")
        addDependency(project, GWT.ID, "de.tomgrill.gdxfacebook:gdx-dialogs-html")
        addDependency(project, GWT.ID, "de.tomgrill.gdxfacebook:gdx-dialogs-html:sources")
        addGwtInherit(project, "de.tomgrill.gdxfacebook.html.gdx_dialogs_html")
    }
}

/**
 * In-game console implementation.
 * @author StrongJoshua
 */
@Extension
class InGameConsole : ThirdPartyExtension() {
    override val id = "inGameConsole"
    override val defaultVersion = "1.0.0"
    override val url = "https://github.com/StrongJoshua/libgdx-inGameConsole"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.strongjoshua:libgdx-inGameConsole")

        addDependency(project, GWT.ID, "com.strongjoshua:libgdx-inGameConsole:sources")
        addGwtInherit(project, "com.strongjoshua.console")
    }
}

/**
 * Java Annotation Console Interface. In-game console implementation.
 * @author Yevgeny Krasik
 */
@Extension
class Jaci : ThirdPartyExtension() {
    override val id = "jaci"
    override val defaultVersion = "0.4.0"
    override val url = "https://github.com/ykrasik/jaci"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.ykrasik:jaci-libgdx-cli-java")
    }
}

/**
 * Java Annotation Console Interface. GWT-compatible in-game console implementation.
 * @author Yevgeny Krasik
 */
@Extension
class JaciGwt : ThirdPartyExtension() {
    override val id = "jaciGwt"
    override val defaultVersion = "0.4.0"
    override val url = "https://github.com/ykrasik/jaci"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.ykrasik:jaci-libgdx-cli-gwt")

        addDependency(project, GWT.ID, "com.github.ykrasik:jaci-libgdx-cli-gwt:sources")
        addGwtInherit(project, "com.github.ykrasik.jaci")
    }
}

/**
 * Simple map generators.
 * @author MJ
 */
@Extension
class Noise4J : ThirdPartyExtension() {
    override val id = "noise4j"
    override val defaultVersion = "0.1.0"
    override val url = "https://github.com/czyzby/noise4j"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.czyzby:noise4j")

        addDependency(project, GWT.ID, "com.github.czyzby:noise4j:sources")
        addGwtInherit(project, "com.github.czyzby.noise4j.Noise4J")
    }
}

/**
 * Java implementation of Ink language: a scripting language for writing interactive narrative.
 * @author bladecoder
 */
@Extension
class BladeInk : ThirdPartyExtension() {
    override val id = "bladeInk"
    override val defaultVersion = "0.7.3"
    override val url = "https://github.com/bladecoder/blade-ink"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.bladecoder.ink:blade-ink")
    }
}

/**
 * 2D, 3D, 4D and 6D modular noise library written in Java.
 * @author SudoPlayGames
 */
@Extension
class Joise : ThirdPartyExtension() {
    override val id = "joise"
    override val defaultVersion = "1.1.0"
    override val url = "https://github.com/SudoPlayGames/Joise"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.sudoplay.joise:joise")

        addDependency(project, GWT.ID, "com.sudoplay.joise:joise:sources")
        addGwtInherit(project, "joise")
    }
}

/**
 * Another 2D, 3D, 4D and 6D noise library, supporting some unusual types of noise.
 * @author Tommy Ettinger
 */
@Extension
class MakeSomeNoise : ThirdPartyExtension() {
    override val id = "makeSomeNoise"
    override val defaultVersion = "0.2"
    override val url = "https://github.com/tommyettinger/make-some-noise"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.tommyettinger:make_some_noise")

        addDependency(project, GWT.ID, "com.github.tommyettinger:make_some_noise:sources")
        addGwtInherit(project, "make.some.noise")
    }
}

/**
 * An animated Label equivalent that appears as if it was being typed in real time.
 * @author Rafa Skoberg
 */
@Extension
class TypingLabel : ThirdPartyExtension() {
    override val id = "typingLabel"
    override val defaultVersion = "1.2.0"
    override val url = "https://github.com/rafaskb/typing-label"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.rafaskoberg.gdx:typing-label")

        addDependency(project, GWT.ID, "com.rafaskoberg.gdx:typing-label:sources")
        addGwtInherit(project, "com.rafaskoberg.gdx.typinglabel.typinglabel")
    }
}

/**
 * A high-performance alternative to libGDX's built-in ShapeRenderer, with smoothing and more shapes.
 * @author earlygrey
 */
@Extension
class ShapeDrawer : ThirdPartyExtension() {
    override val id = "shapeDrawer"
    override val defaultVersion = "2.3.0"
    override val url = "https://github.com/earlygrey/shapedrawer"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "space.earlygrey:shapedrawer")

        addDependency(project, GWT.ID, "space.earlygrey:shapedrawer:sources")
        addGwtInherit(project, "space.earlygrey.shapedrawer")
    }
}

/**
 * Provides a replacement for GWT's missing String.format() with its Stringf.format().
 * @author Tommy Ettinger
 */
@Extension
class Formic : ThirdPartyExtension() {
    override val id = "formic"
    override val defaultVersion = "0.1.2"
    override val url = "https://github.com/tommyettinger/formic"

    override fun initiateDependencies(project: Project) {
        addDependency(project, Core.ID, "com.github.tommyettinger:formic")

        addDependency(project, GWT.ID, "com.github.tommyettinger:formic:sources")
        addGwtInherit(project, "text.formic")
    }
}
