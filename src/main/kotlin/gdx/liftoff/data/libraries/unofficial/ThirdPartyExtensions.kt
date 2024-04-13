@file:Suppress("unused") // Extension classes accessed via reflection.

package gdx.liftoff.data.libraries.unofficial

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.libraries.Library
import gdx.liftoff.data.libraries.Repository
import gdx.liftoff.data.libraries.official.Box2D
import gdx.liftoff.data.libraries.official.Controllers
import gdx.liftoff.data.libraries.official.Freetype
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.platforms.GWT
import gdx.liftoff.data.platforms.Headless
import gdx.liftoff.data.platforms.IOS
import gdx.liftoff.data.platforms.Lwjgl2
import gdx.liftoff.data.platforms.Lwjgl3
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.Extension

/**
 * Abstract base for unofficial extensions.
 */
abstract class ThirdPartyExtension : Library {
  override val official = false
  override val repository: Repository = Repository.MavenCentral

  override fun initiate(project: Project) {
    project.properties[id + "Version"] = version
    initiateDependencies(project)
  }

  abstract fun initiateDependencies(project: Project)

  override fun addDependency(project: Project, platform: String, dependency: String) {
    if (dependency.count { it == ':' } > 1) {
      super.addDependency(project, platform, dependency.substringBeforeLast(':') + ":\$${id}Version:" + dependency.substringAfterLast(':'))
    } else {
      super.addDependency(project, platform, dependency + ":\$${id}Version")
    }
  }

  fun addExternalDependency(project: Project, platform: String, dependency: String) {
    super.addDependency(project, platform, dependency)
  }
}

/**
 * A high performance Entity-Component-System framework.
 * If you target GWT, this setup tool handles some of this library's complicated steps for you.
 * @author junkdog
 */
@Extension
class ArtemisOdb : ThirdPartyExtension() {
  override val id = "artemisOdb"
  override val defaultVersion = "2.3.0"
  override val url = "https://github.com/junkdog/artemis-odb"
  override val group = "net.onedaybeard.artemis"
  override val name = "artemis-odb"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "net.onedaybeard.artemis:artemis-odb")

    addSpecialDependency(project, GWT.ID, "implementation(\"net.onedaybeard.artemis:artemis-odb-gwt:\$${id}Version\") {exclude group: \"com.google.gwt\", module: \"gwt-user\"}")
    addSpecialDependency(project, GWT.ID, "implementation(\"net.onedaybeard.artemis:artemis-odb-gwt:\$${id}Version:sources\") {exclude group: \"com.google.gwt\", module: \"gwt-user\"}")
    addDependency(project, GWT.ID, "net.onedaybeard.artemis:artemis-odb:sources")
    addGwtInherit(project, "com.artemis.backends.artemis_backends_gwt")
    if (project.hasPlatform(GWT.ID)) {
      project.files.add(
        CopiedFile(
          projectName = GWT.ID,
          original = path("generator", GWT.ID, "jsr305.gwt.xml"),
          path = path("src", "main", "java", "jsr305.gwt.xml")
        )
      )
      addGwtInherit(project, "jsr305")
    }
  }
}

/**
 * General libGDX utilities.
 * @author Dermetfan
 * @author Maintained by Tommy Ettinger
 */
@Extension
class LibgdxUtils : ThirdPartyExtension() {
  override val id = "utils"
  override val defaultVersion = "0.13.7"
  override val url = "https://github.com/tommyettinger/gdx-utils"
  override val group = "com.github.tommyettinger"
  override val name = "libgdx-utils"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:libgdx-utils")

    addDependency(project, GWT.ID, "com.github.tommyettinger:libgdx-utils:sources")
    addGwtInherit(project, "libgdx-utils")
  }
}

/**
 * Box2D libGDX utilities.
 * @author Dermetfan
 * @author Maintained by Tommy Ettinger
 */
@Extension
class LibgdxUtilsBox2D : ThirdPartyExtension() {
  override val id = "utilsBox2d"
  override val defaultVersion = "0.13.7"
  override val url = "https://github.com/tommyettinger/gdx-utils"
  override val group = "com.github.tommyettinger"
  override val name = "libgdx-utils-box2d"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:libgdx-utils-box2d")

    addDependency(project, GWT.ID, "com.github.tommyettinger:libgdx-utils-box2d:sources")
    addGwtInherit(project, "libgdx-utils-box2d")

    LibgdxUtils().initiate(project)
  }
}

/**
 * Facebook graph API wrapper. iOS-incompatible! Also, out-of-date.
 * @author Tom Grill
 */
@Extension
class Facebook : ThirdPartyExtension() {
  override val id = "facebook"
  override val defaultVersion = "1.5.0"
  override val url = "https://github.com/TomGrill/gdx-facebook"
  override val group = "de.tomgrill.gdxfacebook"
  override val name = "gdx-facebook-core"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "de.tomgrill.gdxfacebook:gdx-facebook-core")

    addDependency(project, Android.ID, "de.tomgrill.gdxfacebook:gdx-facebook-android")

    addDesktopDependency(project, "de.tomgrill.gdxfacebook:gdx-facebook-desktop")
// // This is a problem for the App Store, removed.
//        addDependency(project, iOS.ID, "de.tomgrill.gdxfacebook:gdx-facebook-ios")

    addDependency(project, GWT.ID, "de.tomgrill.gdxfacebook:gdx-facebook-core:sources")
    addSpecialDependency(project, GWT.ID, "implementation(\"de.tomgrill.gdxfacebook:gdx-facebook-html:\$${id}Version:sources\"){exclude group: \"com.badlogicgames.gdx\", module: \"gdx-backend-gwt\"}")
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
  override val group = "de.tomgrill.gdxdialogs"
  override val name = "gdx-dialogs-core"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "de.tomgrill.gdxdialogs:gdx-dialogs-core")

    addDependency(project, Android.ID, "de.tomgrill.gdxdialogs:gdx-dialogs-android")

    addDesktopDependency(project, "de.tomgrill.gdxdialogs:gdx-dialogs-desktop")

    addDependency(project, IOS.ID, "de.tomgrill.gdxdialogs:gdx-dialogs-ios")

    addDependency(project, GWT.ID, "de.tomgrill.gdxdialogs:gdx-dialogs-core:sources")
    addDependency(project, GWT.ID, "de.tomgrill.gdxdialogs:gdx-dialogs-html:sources")
    addGwtInherit(project, "de.tomgrill.gdxdialogs.html.gdx_dialogs_html")
  }
}

/**
 * Fast, lightweight Kotlin ECS framework; needs Java 11 or higher.
 * @author StrongJoshua
 */
@Extension
class Fleks : ThirdPartyExtension() {
  override val id = "fleks"
  override val defaultVersion = "2.6"
  override val url = "https://github.com/Quillraven/Fleks"
  override val group = "io.github.quillraven.fleks"
  override val name = "Fleks"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "io.github.quillraven.fleks:Fleks")
  }
}

/**
 * In-game console implementation; GWT-compatible with config.
 * @author StrongJoshua (original)
 * @author Tommy Ettinger (maintainer)
 */
@Extension
class InGameConsole : ThirdPartyExtension() {
  override val id = "inGameConsole"
  override val defaultVersion = "1.0.1"
  override val url = "https://github.com/tommyettinger/sjInGameConsole"
  override val group = "com.github.tommyettinger"
  override val name = "sjInGameConsole"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:sjInGameConsole")
    addDependency(project, GWT.ID, "com.github.tommyettinger:sjInGameConsole:sources")
    addGwtInherit(project, "com.strongjoshua.console")
  }
}

/**
 * Java Annotation Console Interface. In-game console implementation, for non-GWT usage.
 * If you target GWT, use JaciGwt or InGameConsole instead.
 * @author Yevgeny Krasik
 */
@Extension
class Jaci : ThirdPartyExtension() {
  override val id = "jaci"
  override val defaultVersion = "0.4.0"
  override val url = "https://github.com/ykrasik/jaci"
  override val group = "com.github.ykrasik"
  override val name = "jaci-libgdx-cli-java"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.ykrasik:jaci-libgdx-cli-java")
  }
}

/**
 * Java Annotation Console Interface. GWT-compatible in-game console implementation.
 * Don't use this at the same time as JACI (the non-GWT version).
 * @author Yevgeny Krasik
 */
@Extension
class JaciGwt : ThirdPartyExtension() {
  override val id = "jaciGwt"
  override val defaultVersion = "0.4.0"
  override val url = "https://github.com/ykrasik/jaci"
  override val group = "com.github.ykrasik"
  override val name = "jaci-libgdx-cli-gwt"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.ykrasik:jaci-libgdx-cli-gwt")

    addDependency(project, GWT.ID, "com.github.ykrasik:jaci-libgdx-cli-gwt:sources")
    addGwtInherit(project, "com.github.ykrasik.jaci")
  }
}

/**
 * Official Kotlin coroutines library.
 */
@Extension
class KotlinxCoroutines : ThirdPartyExtension() {
  override val id = "kotlinxCoroutines"
  override val defaultVersion = "1.6.4"
  override val url = "https://kotlinlang.org/docs/coroutines-overview.html"
  override val group = "org.jetbrains.kotlinx"
  override val name = "kotlinx-coroutines-core"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "org.jetbrains.kotlinx:kotlinx-coroutines-core")
  }
}

/**
 * Simple map generators. Noise4J can be used as a continuous noise generator, but you're better served by
 * joise or make-some-noise in that case. There are also many kinds of map generator in squidlib-util.
 * @author czyzby
 */
@Extension
class Noise4J : ThirdPartyExtension() {
  override val id = "noise4j"
  override val defaultVersion = "0.1.0"
  override val url = "https://github.com/czyzby/noise4j"
  override val group = "com.github.czyzby"
  override val name = "noise4j"

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
  override val defaultVersion = "1.1.2"
  override val url = "https://github.com/bladecoder/blade-ink"
  override val group = "com.bladecoder.ink"
  override val name = "blade-ink"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.bladecoder.ink:blade-ink")
  }
}

/**
 * 2D, 3D, 4D and 6D modular noise library written in Java.
 * Joise can combine noise in versatile ways, and can serialize the "recipes" for a particular type of noise generator.
 * @author SudoPlayGames
 */
@Extension
class Joise : ThirdPartyExtension() {
  override val id = "joise"
  override val defaultVersion = "1.1.0"
  override val url = "https://github.com/SudoPlayGames/Joise"
  override val group = "com.sudoplay.joise"
  override val name = "joise"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.sudoplay.joise:joise")

    addDependency(project, GWT.ID, "com.sudoplay.joise:joise:sources")
    addGwtInherit(project, "joise")
  }
}

/**
 * Another 2D, 3D, 4D, 5D, and 6D noise library, supporting some unusual types of noise.
 * The API is more "raw" than Joise, and is meant as a building block for things that use noise, rather
 * than something that generates immediately-usable content. It is still a more convenient API than
 * Noise4J when making fields of noise.
 * @author Tommy Ettinger
 */
@Extension
class MakeSomeNoise : ThirdPartyExtension() {
  override val id = "makeSomeNoise"
  override val defaultVersion = "0.3"
  override val url = "https://github.com/tommyettinger/make-some-noise"
  override val group = "com.github.tommyettinger"
  override val name = "make_some_noise"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:make_some_noise")

    addDependency(project, GWT.ID, "com.github.tommyettinger:make_some_noise:sources")
    addGwtInherit(project, "make.some.noise")
  }
}

/**
 * An animated Label equivalent that appears as if it was being typed in real time.
 * This is really just a wonderful set of effects for games to have.
 * @author Rafa Skoberg
 */
@Extension
class TypingLabel : ThirdPartyExtension() {
  override val id = "typingLabel"
  override val defaultVersion = "1.3.0"
  override val url = "https://github.com/rafaskb/typing-label"
  override val group = "com.rafaskoberg.gdx"
  override val name = "typing-label"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.rafaskoberg.gdx:typing-label")

    addDependency(project, GWT.ID, "com.rafaskoberg.gdx:typing-label:sources")
    addGwtInherit(project, "com.rafaskoberg.gdx.typinglabel.typinglabel")
    RegExodus().initiate(project)
  }
}

/**
 * Augmented text display, including styles and all of TypingLabel's features.
 * @author Tommy Ettinger
 * @author Rafa Skoberg
 */
@Extension
class TextraTypist : ThirdPartyExtension() {
  override val id = "textratypist"
  override val defaultVersion = "0.10.0"
  override val url = "https://github.com/tommyettinger/textratypist"
  override val group = "com.github.tommyettinger"
  override val name = "textratypist"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:textratypist")

    addDependency(project, GWT.ID, "com.github.tommyettinger:textratypist:sources")
    addGwtInherit(project, "com.github.tommyettinger.textratypist")
    RegExodus().initiate(project)
  }
}

/**
 * A high-performance alternative to libGDX's built-in ShapeRenderer, with smoothing and more shapes.
 * Usually more practical when compared with ShapeRenderer, but ShapeRenderer may perform better when
 * rendering hair-thin lines or points.
 * @author earlygrey
 */
@Extension
class ShapeDrawer : ThirdPartyExtension() {
  override val id = "shapeDrawer"
  override val defaultVersion = "2.6.0"
  override val url = "https://github.com/earlygrey/shapedrawer"
  override val repository = Repository.JitPack
  override val group = "space.earlygrey"
  override val name = "shapedrawer"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "space.earlygrey:shapedrawer")

    addDependency(project, GWT.ID, "space.earlygrey:shapedrawer:sources")
    addGwtInherit(project, "space.earlygrey.shapedrawer")
  }
}

/**
 * Provides various frequently-used graph algorithms, aiming to be lightweight, fast, and intuitive.
 * A good substitute for the pathfinding in gdx-ai, but it doesn't include path smoothing or any of the
 * non-pathfinding AI tools in gdx-ai.
 * @author earlygrey
 */
@Extension
class SimpleGraphs : ThirdPartyExtension() {
  override val id = "simpleGraphs"
  override val defaultVersion = "5.1.1"
  override val url = "https://github.com/earlygrey/simple-graphs"
  override val repository = Repository.JitPack
  override val group = "space.earlygrey"
  override val name = "simple-graphs"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "space.earlygrey:simple-graphs")

    addDependency(project, GWT.ID, "space.earlygrey:simple-graphs:sources")
    addGwtInherit(project, "simple_graphs")
  }
}

/**
 * Provides a replacement for GWT's missing String.format() with its Stringf.format().
 * Only relevant if you target the HTML platform or intend to in the future.
 * @author Tommy Ettinger
 */
@Extension
class Formic : ThirdPartyExtension() {
  override val id = "formic"
  override val defaultVersion = "0.1.5"
  override val url = "https://github.com/tommyettinger/formic"
  override val group = "com.github.tommyettinger"
  override val name = "formic"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:formic")

    addDependency(project, GWT.ID, "com.github.tommyettinger:formic:sources")
    addGwtInherit(project, "formic")
  }
}

/**
 * Alternative color models for changing the colors of sprites and scenes, including brightening.
 * @author Tommy Ettinger
 */
@Extension
class Colorful : ThirdPartyExtension() {
  override val id = "colorful"
  override val defaultVersion = "0.8.5"
  override val url = "https://github.com/tommyettinger/colorful-gdx"
  override val group = "com.github.tommyettinger"
  override val name = "colorful"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:colorful")

    addDependency(project, GWT.ID, "com.github.tommyettinger:colorful:sources")
    addGwtInherit(project, "com.github.tommyettinger.colorful.colorful")
  }
}

/**
 * Support for writing animated GIF and animated PNG images from libGDX, as well as 8-bit-palette PNGs.
 * This can be useful for making short captures of gameplay, or making animated characters into GIFs.
 * @author Tommy Ettinger
 */
@Extension
class Anim8 : ThirdPartyExtension() {
  override val id = "anim8"
  override val defaultVersion = "0.4.2"
  override val url = "https://github.com/tommyettinger/anim8-gdx"
  override val group = "com.github.tommyettinger"
  override val name = "anim8-gdx"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:anim8-gdx")

    addDependency(project, GWT.ID, "com.github.tommyettinger:anim8-gdx:sources")
    addGwtInherit(project, "com.github.tommyettinger.anim8")
  }
}

/**
 * Bonus features for 9-patch images, filling significant gaps in normal 9-patch functionality.
 * @author Raymond Buckley
 */
@Extension
class TenPatch : ThirdPartyExtension() {
  override val id = "tenPatch"
  override val defaultVersion = "5.2.3"
  override val url = "https://github.com/raeleus/TenPatch"
  override val group = "com.github.raeleus"
  override val name = "TenPatch"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.raeleus.TenPatch:tenpatch")

    addDependency(project, GWT.ID, "com.github.raeleus.TenPatch:tenpatch:sources")
    addGwtInherit(project, "com.ray3k.tenpatch.tenpatch")
  }
}

/**
 * Collected Scene2D widgets and utilities.
 * @author Raymond Buckley
 */
@Extension
class Stripe : ThirdPartyExtension() {
  override val id = "stripe"
  override val defaultVersion = "1.4.2"
  override val url = "https://github.com/raeleus/stripe"
  override val group = "com.github.raeleus.stripe"
  override val name = "Stripe"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.raeleus.stripe:stripe")

    addDependency(project, GWT.ID, "com.github.raeleus.stripe:stripe:sources")
    addGwtInherit(project, "com.ray3k.stripe")
  }
}

/**
 * Support for the GLTF format for 3D models and physically-based rendering; a huge time-saver for 3D handling.
 * @author mgsx
 */
@Extension
class GdxGltf : ThirdPartyExtension() {
  override val id = "gdxGltf"
  override val defaultVersion = "f1eb2b1799"
  override val repository = Repository.JitPack
  override val url = "https://github.com/mgsx-dev/gdx-gltf"
  override val group = "com.github.mgsx-dev"
  override val name = "gdx-gltf"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.mgsx-dev.gdx-gltf:gltf")
    addDependency(project, GWT.ID, "com.github.mgsx-dev.gdx-gltf:gltf:sources")
    addGwtInherit(project, "GLTF")
  }
}

/**
 * A simple framebuffer based lighting engine for libGDX. Adds sample light assets, so it can be used immediately.
 * @author Ali Asif Khan
 */
@Extension
class HackLights : ThirdPartyExtension() {
  override val id = "hackLights"
  override val defaultVersion = "f0ba5deaff"
  override val repository = Repository.JitPack
  override val url = "https://github.com/aliasifk/HackLights"
  override val group = "com.github.aliasifk"
  override val name = "HackLights"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.aliasifk:HackLights")
    addDependency(project, GWT.ID, "com.github.aliasifk:HackLights:sources")
    addGwtInherit(project, "com.aliasifkhan.hackLights")
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        original = path("generator", "assets", "lights.png"),
        path = path("lights.png")
      )
    )
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        original = path("generator", "assets", "lights.atlas"),
        path = path("lights.atlas")
      )
    )
  }
}

/**
 * The libGDX runtime for Spine, a commercial (and very powerful) skeletal-animation editor.
 * You must have a license for Spine to use the runtime in your code.
 * @author Esoteric Software
 */
@Extension
class SpineRuntime : ThirdPartyExtension() {
  override val id = "spineRuntime"
  override val defaultVersion = "4.1.0"
  override val url = "https://github.com/EsotericSoftware/spine-runtimes/tree/4.1/spine-libgdx"
  override val group = "com.esotericsoftware.spine"
  override val name = "spine-libgdx"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.esotericsoftware.spine:spine-libgdx")

    addDependency(project, GWT.ID, "com.esotericsoftware.spine:spine-libgdx:sources")
    addGwtInherit(project, "com.esotericsoftware.spine")
  }
}

/**
 * Legacy: MrStahlfelge's upgrades to controller support, now part of the official controllers extension.
 * This is here so older projects that don't use the official controllers can be ported more easily.
 * Change the version to 1.0.1 if you use libGDX 1.9.10 or earlier!
 * @author MrStahlfelge
 */
@Extension
class ControllerUtils : ThirdPartyExtension() {
  override val id = "controllerUtils"
  override val defaultVersion = "2.2.1"
  override val url = "https://github.com/MrStahlfelge/gdx-controllerutils"
  override val group = "de.golfgl.gdxcontrollerutils"
  override val name = "gdx-controllers-advanced"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "de.golfgl.gdxcontrollerutils:gdx-controllers-advanced")
    addDependency(project, Lwjgl2.ID, "de.golfgl.gdxcontrollerutils:gdx-controllers-jamepad")
    addDependency(project, Lwjgl3.ID, "de.golfgl.gdxcontrollerutils:gdx-controllers-jamepad")
    addDependency(project, Android.ID, "de.golfgl.gdxcontrollerutils:gdx-controllers-android")
    addDependency(project, IOS.ID, "de.golfgl.gdxcontrollerutils:gdx-controllers-iosrvm")

    addSpecialDependency(project, GWT.ID, "implementation(\"de.golfgl.gdxcontrollerutils:gdx-controllers-gwt:\$${id}Version:sources\"){exclude group: \"com.badlogicgames.gdx\", module: \"gdx-backend-gwt\"}")
    addSpecialDependency(project, GWT.ID, "implementation(\"de.golfgl.gdxcontrollerutils:gdx-controllers-advanced:\$${id}Version:sources\"){exclude group: \"com.badlogicgames.gdx\", module: \"gdx-backend-gwt\"}")
    addGwtInherit(project, "com.badlogic.gdx.controllers.controllers-gwt")
  }
}

/**
 * MrStahlfelge's controller-imitating Scene2D widgets, for players who don't have a controller.
 * <a href="https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Button-operable-Scene2d">See the docs before using</a>.
 * Change the version to 1.0.1 if you use libGDX 1.9.10 or earlier!
 * @author MrStahlfelge
 */
@Extension
class ControllerScene2D : ThirdPartyExtension() {
  override val id = "controllerScene2D"
  override val defaultVersion = "2.3.0"
  override val url = "https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Button-operable-Scene2d"
  override val group = "de.golfgl.gdxcontrollerutils"
  override val name = "gdx-controllerutils-scene2d"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "de.golfgl.gdxcontrollerutils:gdx-controllerutils-scene2d")

    addDependency(project, GWT.ID, "de.golfgl.gdxcontrollerutils:gdx-controllerutils-scene2d:sources")
    addGwtInherit(project, "de.golfgl.gdx.controllers.controller_scene2d")
  }
}

/**
 * MrStahlfelge's configurable mapping for game controllers.
 * Not compatible with libGDX 1.9.10 or older!
 * @author MrStahlfelge
 */
@Extension
class ControllerMapping : ThirdPartyExtension() {
  override val id = "controllerMapping"
  override val defaultVersion = "2.3.0"
  override val url = "https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Configurable-Game-Controller-Mappings"
  override val group = "de.golfgl.gdxcontrollerutils"
  override val name = "gdx-controllerutils-mapping"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "de.golfgl.gdxcontrollerutils:gdx-controllerutils-mapping")

    addDependency(project, GWT.ID, "de.golfgl.gdxcontrollerutils:gdx-controllerutils-mapping:sources")
    Controllers().initiate(project)
  }
}

/**
 * Code for making post-processing effects without so much hassle.
 * @author crashinvaders
 * @author metaphore
 */
@Extension
class GdxVfxCore : ThirdPartyExtension() {
  override val id = "gdxVfxCore"
  override val defaultVersion = "0.5.4"
  override val url = "https://github.com/crashinvaders/gdx-vfx"
  override val group = "com.crashinvaders.vfx"
  override val name = "gdx-vfx-core"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.crashinvaders.vfx:gdx-vfx-core")

    addDependency(project, GWT.ID, "com.crashinvaders.vfx:gdx-vfx-core:sources")
    addDependency(project, GWT.ID, "com.crashinvaders.vfx:gdx-vfx-gwt:sources")
    addGwtInherit(project, "com.crashinvaders.vfx.GdxVfxCore")
    addGwtInherit(project, "com.crashinvaders.vfx.GdxVfxGwt")
  }
}

/**
 * A wide range of predefined post-processing effects using gdx-vfx core.
 * @author crashinvaders
 * @author metaphore
 */
@Extension
class GdxVfxStandardEffects : ThirdPartyExtension() {
  override val id = "gdxVfxEffects"
  override val defaultVersion = "0.5.4"
  override val url = "https://github.com/crashinvaders/gdx-vfx"
  override val group = "com.crashinvaders.vfx"
  override val name = "gdx-vfx-effects"

  override fun initiateDependencies(project: Project) {
    GdxVfxCore().initiate(project)
    addDependency(project, Core.ID, "com.crashinvaders.vfx:gdx-vfx-effects")

    addDependency(project, GWT.ID, "com.crashinvaders.vfx:gdx-vfx-effects:sources")
    addGwtInherit(project, "com.crashinvaders.vfx.GdxVfxEffects")
  }
}

/**
 * Cross-platform regex utilities that work the same on HTML as they do on desktop or mobile platforms.
 * This is not 100% the same as the java.util.regex package, but is similar, and sometimes offers more.
 * @author Tommy Ettinger
 * @author based on JRegex by Sergey A. Samokhodkin
 */
@Extension
class RegExodus : ThirdPartyExtension() {
  override val id = "regExodus"
  override val defaultVersion = "0.1.15"
  override val url = "https://github.com/tommyettinger/RegExodus"
  override val group = "com.github.tommyettinger"
  override val name = "regexodus"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:regexodus")

    addDependency(project, GWT.ID, "com.github.tommyettinger:regexodus:sources")
    addGwtInherit(project, "regexodus.regexodus")
  }
}

/**
 * UI toolkit with extra widgets and a different theme style.
 * Check the vis-ui changelog for what vis-ui versions are compatible
 * with which libGDX versions; vis-ui 1.5.3 is the default and is
 * compatible with libGDX 1.12.1.
 * @author Kotcrab
 */
@Extension
class VisUI : ThirdPartyExtension() {
  override val id = "visUi"

  // You may need to skip a check: VisUI.setSkipGdxVersionCheck(true);
  override val defaultVersion = "1.5.3"
  override val url = "https://github.com/kotcrab/vis-ui"
  override val group = "com.kotcrab.vis"
  override val name = "vis-ui"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.kotcrab.vis:vis-ui")

    addDependency(project, GWT.ID, "com.kotcrab.vis:vis-ui:sources")
    addGwtInherit(project, "com.kotcrab.vis.vis-ui")
  }
}

/**
 * A library to obtain a circular WidgetGroup or context menu using scene2d.ui.
 * Pie menus can be easier for players to navigate with a mouse than long lists.
 * @author Jérémi Grenier-Berthiaume
 */
@Extension
class PieMenu : ThirdPartyExtension() {
  override val id = "pieMenu"
  override val defaultVersion = "5.0.0"
  override val url = "https://github.com/payne911/PieMenu"
  override val repository = Repository.JitPack
  override val group = "com.github.payne911"
  override val name = "PieMenu"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.payne911:PieMenu")

    addDependency(project, GWT.ID, "com.github.payne911:PieMenu:sources")
    addGwtInherit(project, "PieMenu")
    ShapeDrawer().initiate(project)
  }
}

/**
 * A 2D AABB collision detection and response library; like a basic/easy version of box2d.
 * Note, AABB means this only handles non-rotated rectangular collision boxes.
 * @author implicit-invocation
 * @author Raymond Buckley
 */
@Extension
class JBump : ThirdPartyExtension() {
  override val id = "jbump"
  override val defaultVersion = "v1.0.1"
  override val url = "https://github.com/tommyettinger/jbump"
  override val repository = Repository.JitPack
  override val group = "com.github.tommyettinger"
  override val name = "jbump"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:jbump")
    addDependency(project, GWT.ID, "com.github.tommyettinger:jbump:sources")
    addGwtInherit(project, "com.dongbat.jbump")
  }
}

/**
 * Extends or augments the Java Collections Framework.
 * @author Apache Software Foundation
 */
@Extension
class CommonsCollections : ThirdPartyExtension() {
  override val id = "commonsCollections"
  override val defaultVersion = "4.4"
  override val url = "https://commons.apache.org/proper/commons-collections/"
  override val group = "org.apache.commons"
  override val name = "commons-collections4"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "org.apache.commons:commons-collections4")
  }
}

/**
 * Very fast binary serialization. GWT-incompatible.
 * @author Apache Software Foundation
 */
@Extension
class Fury : ThirdPartyExtension() {
  override val id = "fury"
  override val defaultVersion = "0.4.1"
  override val url = "https://fury.apache.org/"
  override val group = "org.furyio" // will change to org.apache.fury in 0.5.0
  override val name = "fury-core"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "org.furyio:fury-core")
  }
}

/**
 * Efficient binary serialization framework for the JVM. GWT-incompatible.
 * @author Nathan Sweet
 */
@Extension
class Kryo : ThirdPartyExtension() {
  override val id = "kryo"
  override val defaultVersion = "5.6.0"
  override val url = "https://github.com/EsotericSoftware/kryo"
  override val group = "com.esotericsoftware"
  override val name = "kryo"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.esotericsoftware:kryo")
  }
}

/**
 * A Java library that provides a clean and simple API for efficient network communication, using Kryo.
 * This is crykn's fork (AKA damios), which is much more up-to-date than the official repo.
 * @author Nathan Sweet
 * @author damios/crykn
 */
@Extension
class KryoNet : ThirdPartyExtension() {
  override val id = "kryoNet"
  override val defaultVersion = "2.22.7"
  override val url = "https://github.com/crykn/kryonet"
  override val repository = Repository.JitPack
  override val group = "com.github.crykn"
  override val name = "kryonet"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.crykn:kryonet")
  }
}

/**
 * A small collection of some common and very basic utilities for libGDX games.
 * @author damios/crykn
 */
@Extension
class Guacamole : ThirdPartyExtension() {
  override val id = "guacamole"
  override val defaultVersion = "0.3.5"
  override val url = "https://github.com/crykn/guacamole"
  override val repository = Repository.JitPack
  override val group = "com.github.crykn"
  override val name = "guacamole"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.crykn.guacamole:core")
    addDependency(project, Core.ID, "com.github.crykn.guacamole:gdx")
    addDependency(project, Lwjgl2.ID, "com.github.crykn.guacamole:gdx-desktop")
    addDependency(project, Lwjgl3.ID, "com.github.crykn.guacamole:gdx-desktop")
    addDependency(project, GWT.ID, "com.github.crykn.guacamole:core:sources")
    addDependency(project, GWT.ID, "com.github.crykn.guacamole:gdx:sources")
    addSpecialDependency(project, GWT.ID, "implementation(\"com.github.crykn.guacamole:gdx-gwt:\$${id}Version:sources\"){exclude group: \"com.badlogicgames.gdx\", module: \"gdx-backend-gwt\"}")
    addGwtInherit(project, "guacamole_gdx_gwt")
    if (project.platforms.containsKey(GWT.ID)) {
      Formic().initiate(project)
    }
  }
}

/**
 * Access the Oboe libraries for audio in Android 16+.
 * @author barsoosayque
 */
@Extension
class LibgdxOboe : ThirdPartyExtension() {
  override val id = "libgdxOboe"
  override val defaultVersion = "0.3.0.3"
  override val url = "https://github.com/tommyettinger/libgdx-oboe"
  override val group = "com.github.tommyettinger"
  override val name = "libgdxoboe"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Android.ID, "com.github.tommyettinger:libgdxoboe")
  }
}

/**
 * A screen manager for libGDX supporting transitions.
 * @author damios/crykn
 */
@Extension
class LibgdxScreenManager : ThirdPartyExtension() {
  override val id = "screenManager"
  override val defaultVersion = "0.7.0"
  override val url = "https://github.com/crykn/libgdx-screenmanager"
  override val repository = Repository.JitPack
  override val group = "com.github.crykn"
  override val name = "libgdx-screenmanager"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.crykn:libgdx-screenmanager")
    addDependency(project, GWT.ID, "com.github.crykn:libgdx-screenmanager:sources")
    addGwtInherit(project, "libgdx_screenmanager")
    Guacamole().initiate(project)
  }
}

/**
 * Advanced audio features for LWJGL3. JDK 8+.
 * @author Hangman
 */
@Extension
class TuningFork : ThirdPartyExtension() {
  override val id = "tuningFork"
  override val defaultVersion = "3.2.0"
  override val url = "https://github.com/Hangman/TuningFork"
  override val repository = Repository.JitPack
  override val group = "com.github.Hangman"
  override val name = "TuningFork"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Lwjgl3.ID, "com.github.Hangman:TuningFork")
  }
}

/**
 * Load/render TinyVG vector graphics.
 * @author Lyze
 */
@Extension
class TinyVG : ThirdPartyExtension() {
  override val id = "tinyVG"
  override val defaultVersion = "f0213161cc"
  override val url = "https://github.com/lyze237/gdx-TinyVG"
  override val repository = Repository.JitPack
  override val group = "com.github.lyze237"
  override val name = "gdx-TinyVG"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.lyze237:gdx-TinyVG")
    addDependency(project, GWT.ID, "com.github.lyze237:gdx-TinyVG:sources")
    addGwtInherit(project, "dev.lyze.tinyvg")
    ShapeDrawer().initiate(project)
  }
}

/**
 * LibGDX PSX-style render features. Not GWT-compatible.
 * @author FXGaming
 */
@Extension
class GdxPsx : ThirdPartyExtension() {
  override val id = "gdxPsx"
  override val defaultVersion = "0.1.5"
  override val url = "https://github.com/fxgaming/gdx-psx"
  override val repository = Repository.JitPack
  override val group = "com.github.fxgaming"
  override val name = "gdx-psx"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.fxgaming:gdx-psx")
//        addDependency(project, GWT.ID, "com.github.fxgaming:gdx-psx:sources")
  }
}

/**
 * A layout engine which implements FlexBox.
 * @author Lyze
 */
@Extension
class GdxFlexBox : ThirdPartyExtension() {
  override val id = "flexBox"
  override val defaultVersion = "425149b588"
  override val url = "https://github.com/lyze237/gdx-FlexBox"
  override val repository = Repository.JitPack
  override val group = "com.github.lyze327"
  override val name = "gdx-FlexBox"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.lyze237:gdx-FlexBox")
    addDependency(project, GWT.ID, "com.github.lyze237:gdx-FlexBox:sources")
    addGwtInherit(project, "dev.lyze.flexbox")
  }
}

/**
 * Couples Unity's behaviour system and execution order with Box2D.
 * @author Lyze
 */
@Extension
class GdxUnBox2D : ThirdPartyExtension() {
  override val id = "unbox2d"
  override val defaultVersion = "e09473c1d0"
  override val url = "https://github.com/lyze237/gdx-UnBox2D"
  override val repository = Repository.JitPack
  override val group = "com.github.lyze327"
  override val name = "gdx-UnBox2D"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.lyze237:gdx-UnBox2D")
    addDependency(project, GWT.ID, "com.github.lyze237:gdx-UnBox2D:sources")
    addGwtInherit(project, "dev.lyze.gdxUnBox2d")
    Box2D().initiate(project)
    ShapeDrawer().initiate(project)
  }
}

/**
 * Support for the Basis Universal supercompressed texture format.
 * This form of texture compression works best for extremely large 3D textures, and works
 * quite badly on pixel art. You might see big improvements in memory usage, you might not.
 * You may need to change the dependencies for Desktop, LWJGL3, Headless,
 * and/or iOS from `implementation` to `runtimeOnly`.
 * @author Anton Chekulaev/metaphore
 */
@Extension
class GdxBasisUniversal : ThirdPartyExtension() {
  override val id = "gdxBasisUniversal"
  override val defaultVersion = "1.0.0"
  override val url = "https://github.com/crashinvaders/gdx-basis-universal"
  override val group = "com.crashinvaders.basisu"
  override val name = "basisu-wrapper"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.crashinvaders.basisu:basisu-wrapper")
    addDependency(project, Core.ID, "com.crashinvaders.basisu:basisu-gdx")
    addDependency(project, Lwjgl2.ID, "com.crashinvaders.basisu:basisu-wrapper:natives-desktop")
    addDependency(project, Lwjgl3.ID, "com.crashinvaders.basisu:basisu-wrapper:natives-desktop")
    addDependency(project, Headless.ID, "com.crashinvaders.basisu:basisu-wrapper:natives-desktop")
    addDependency(project, IOS.ID, "com.crashinvaders.basisu:basisu-wrapper:natives-ios")
    addNativeAndroidDependency(project, "com.crashinvaders.basisu:basisu-wrapper:natives-armeabi-v7a")
    addNativeAndroidDependency(project, "com.crashinvaders.basisu:basisu-wrapper:natives-arm64-v8a")
    addNativeAndroidDependency(project, "com.crashinvaders.basisu:basisu-wrapper:natives-x86")
    addNativeAndroidDependency(project, "com.crashinvaders.basisu:basisu-wrapper:natives-x86_64")
    addSpecialDependency(project, GWT.ID, "implementation(\"com.crashinvaders.basisu:basisu-gdx-gwt:\$${id}Version:sources\"){exclude group: \"com.badlogicgames.gdx\", module: \"gdx-backend-gwt\"}")
    addDependency(project, GWT.ID, "com.crashinvaders.basisu:basisu-gdx:sources")
    addDependency(project, GWT.ID, "com.crashinvaders.basisu:basisu-wrapper:sources")
    addDependency(project, GWT.ID, "com.crashinvaders.basisu:basisu-wrapper:natives-web")
    addGwtInherit(project, "com.crashinvaders.basisu.BasisuGdxGwt")
  }
}

/**
 * Adds support for Lombok annotations in the core module; meant to reduce boilerplate code.
 * @author The Project Lombok Authors
 */
@Extension
class Lombok : ThirdPartyExtension() {
  override val id = "lombok"
  override val defaultVersion = "1.18.30"
  override val url = "https://projectlombok.org/"
  override val group = "org.projectlombok"
  override val name = "lombok"

  override fun initiateDependencies(project: Project) {
    addSpecialDependency(project, Core.ID, "compileOnly \"org.projectlombok:lombok:\$${id}Version\"")
    addSpecialDependency(project, Core.ID, "annotationProcessor \"org.projectlombok:lombok:\$${id}Version\"")
    project.rootGradle.buildDependencies.add("\"io.freefair.gradle:lombok-plugin:8.3\"")
    project.rootGradle.plugins.add("io.freefair.lombok")
  }
}

/**
 * Add support for HyperLap2D libGDX runtime.
 * @author fgnm
 */
@Extension
class HyperLap2DRuntime : ThirdPartyExtension() {
  override val id = "h2d"
  override val defaultVersion = "0.1.1"
  override val url = "https://github.com/rednblackgames/hyperlap2d-runtime-libgdx"
  override val group = "games.rednblack.hyperlap2d"
  override val name = "runtime-libgdx"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "games.rednblack.hyperlap2d:runtime-libgdx")
    addDependency(project, GWT.ID, "games.rednblack.hyperlap2d:runtime-libgdx:sources")
    addGwtInherit(project, "HyperLap2D")

    Box2D().initiate(project)
    Freetype().initiate(project)
    ArtemisOdb().initiate(project)
  }
}

/**
 * Add support for HyperLap2D Spine extension for libGDX runtime.
 * @author fgnm
 */
@Extension
class HyperLap2DSpineExtension : ThirdPartyExtension() {
  override val id = "h2dSpineExtension"
  override val defaultVersion = "0.1.1"
  override val url = "https://github.com/rednblackgames/h2d-libgdx-spine-extension"
  override val group = "games.rednblack.hyperlap2d"
  override val name = "libgdx-spine-extension"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "games.rednblack.hyperlap2d:libgdx-spine-extension")
    addDependency(project, GWT.ID, "games.rednblack.hyperlap2d:libgdx-spine-extension:sources")
    addGwtInherit(project, "HyperLap2D.spine")

    SpineRuntime().initiate(project)
  }
}

/**
 * Add support for HyperLap2D TinyVG extension for libGDX runtime.
 * @author fgnm
 */
@Extension
class HyperLap2DTinyVGExtension : ThirdPartyExtension() {
  override val id = "h2dTinyVGExtension"
  override val defaultVersion = "0.1.1"
  override val url = "https://github.com/rednblackgames/h2d-libgdx-tinyvg-extension"
  override val group = "games.rednblack.hyperlap2d"
  override val name = "libgdx-tinyvg-extension"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "games.rednblack.hyperlap2d:libgdx-tinyvg-extension")
    addDependency(project, GWT.ID, "games.rednblack.hyperlap2d:libgdx-tinyvg-extension:sources")

    TinyVG().initiate(project)
  }
}

/**
 * Add support for HyperLap2D Typing Label extension for libGDX runtime.
 * @author fgnm
 */
@Extension
class HyperLap2DTypingLabelExtension : ThirdPartyExtension() {
  override val id = "h2dTypingLabelExtension"
  override val defaultVersion = "0.1.1"
  override val url = "https://github.com/rednblackgames/h2d-libgdx-typinglabel-extension"
  override val group = "games.rednblack.hyperlap2d"
  override val name = "libgdx-typinglabel-extension"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "games.rednblack.hyperlap2d:libgdx-typinglabel-extension")
    addDependency(project, GWT.ID, "games.rednblack.hyperlap2d:libgdx-typinglabel-extension:sources")

    TypingLabel().initiate(project)
  }
}

/**
 * Advanced Cross Platform Audio Engine for libGDX based on MiniAudio.
 * @author fgnm
 */
@Extension
class GdxMiniAudio : ThirdPartyExtension() {
  override val id = "miniaudio"
  override val defaultVersion = "0.2"
  override val url = "https://github.com/rednblackgames/gdx-miniaudio"
  override val group = "games.rednblack.miniaudio"
  override val name = "miniaudio"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "games.rednblack.miniaudio:miniaudio")
    addDependency(project, Lwjgl3.ID, "games.rednblack.miniaudio:miniaudio:natives-desktop")
    addDependency(project, IOS.ID, "games.rednblack.miniaudio:miniaudio:natives-ios")
    addNativeAndroidDependency(project, "games.rednblack.miniaudio:miniaudio:natives-armeabi-v7a")
    addNativeAndroidDependency(project, "games.rednblack.miniaudio:miniaudio:natives-arm64-v8a")
    addNativeAndroidDependency(project, "games.rednblack.miniaudio:miniaudio:natives-x86")
    addNativeAndroidDependency(project, "games.rednblack.miniaudio:miniaudio:natives-x86_64")
  }

  override fun addNativeAndroidDependency(project: Project, dependency: String) {
    if (dependency.count { it == ':' } > 1) {
      super.addNativeAndroidDependency(project, dependency.substringBeforeLast(':') + ":\$${id}Version:" + dependency.substringAfterLast(':'))
    } else {
      super.addNativeAndroidDependency(project, dependency + ":\$${id}Version")
    }
  }
}

/**
 * Aurelien Ribon's Universal Tween Engine.
 * @author Aurelien Ribon
 * @author Tom Cashman
 */
@Extension
class UniversalTween : ThirdPartyExtension() {
  override val id = "universalTween"
  override val defaultVersion = "6.3.3"
  override val url = "https://github.com/mini2Dx/universal-tween-engine"
  override val group = "org.mini2Dx"
  override val name = "universal-tween-engine"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "org.mini2Dx:universal-tween-engine")

    addDependency(project, GWT.ID, "org.mini2Dx:universal-tween-engine:sources")
    addGwtInherit(project, "aurelienribon.tweenengine")
  }
}

/**
 * Shared interfaces for points, such as Vector2.
 * @author Tommy Ettinger
 */
@Extension
class Crux : ThirdPartyExtension() {
  override val id = "crux"
  override val defaultVersion = "0.0.1"
  override val url = "https://github.com/tommyettinger/crux"
  override val group = "com.github.tommyettinger"
  override val name = "crux"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:crux")

    addDependency(project, GWT.ID, "com.github.tommyettinger:crux:sources")
    addGwtInherit(project, "com.github.tommyettinger.crux")
  }
}

/**
 * Pathfinding combining simple-graphs and gdx-ai.
 * @author Tommy Ettinger
 */
@Extension
class Gand : ThirdPartyExtension() {
  override val id = "gand"
  override val defaultVersion = "0.1.1"
  override val url = "https://github.com/tommyettinger/gand"
  override val group = "com.github.tommyettinger"
  override val name = "gand"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:gand")

    addDependency(project, GWT.ID, "com.github.tommyettinger:gand:sources")
    addGwtInherit(project, "com.github.tommyettinger.gand")

    Crux().initiate(project)
  }
}

/**
 * Random generation, noise, "encarption..."
 * @author Tommy Ettinger
 */
@Extension
class Cringe : ThirdPartyExtension() {
  override val id = "cringe"
  override val defaultVersion = "0.1.1"
  override val url = "https://github.com/tommyettinger/cringe"
  override val group = "com.github.tommyettinger"
  override val name = "cringe"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:cringe")

    addDependency(project, GWT.ID, "com.github.tommyettinger:cringe:sources")
    addGwtInherit(project, "com.github.tommyettinger.cringe")
  }
}

/**
 * Common code for math and showing numbers.
 * Optimal in projects that don't depend on libGDX, like server modules, because it duplicates some libGDX math code.
 * @author Tommy Ettinger
 */
@Extension
class Digital : ThirdPartyExtension() {
  override val id = "digital"
  override val defaultVersion = "0.4.7"
  override val url = "https://github.com/tommyettinger/digital"
  override val group = "com.github.tommyettinger"
  override val name = "digital"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:digital")

    addDependency(project, GWT.ID, "com.github.tommyettinger:digital:sources")
    addGwtInherit(project, "com.github.tommyettinger.digital")
  }
}

/**
 * Many Java 8 FunctionalInterface-s for primitives.
 * @author Tommy Ettinger
 */
@Extension
class Funderby : ThirdPartyExtension() {
  override val id = "funderby"
  override val defaultVersion = "0.1.1"
  override val url = "https://github.com/tommyettinger/funderby"
  override val group = "com.github.tommyettinger"
  override val name = "funderby"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:funderby")

    addDependency(project, GWT.ID, "com.github.tommyettinger:funderby:sources")
    addGwtInherit(project, "com.github.tommyettinger.funderby")
  }
}

/**
 * Random number generators with easy serialization.
 * @author Tommy Ettinger
 */
@Extension
class Juniper : ThirdPartyExtension() {
  override val id = "juniper"
  override val defaultVersion = "0.5.0"
  override val url = "https://github.com/tommyettinger/juniper"
  override val group = "com.github.tommyettinger"
  override val name = "juniper"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:juniper")

    addDependency(project, GWT.ID, "com.github.tommyettinger:juniper:sources")
    addGwtInherit(project, "com.github.tommyettinger.juniper")

    Digital().initiate(project)
  }
}

/**
 * JDK interface support for GDX-style Data Structures. JDK 8+.
 * @author Tommy Ettinger
 */
@Extension
class Jdkgdxds : ThirdPartyExtension() {
  override val id = "jdkgdxds"
  override val defaultVersion = "1.4.8"
  override val url = "https://github.com/tommyettinger/jdkgdxds"
  override val group = "com.github.tommyettinger"
  override val name = "jdkgdxds"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:jdkgdxds")

    addDependency(project, GWT.ID, "com.github.tommyettinger:jdkgdxds:sources")
    addGwtInherit(project, "com.github.tommyettinger.jdkgdxds")

    Funderby().initiate(project)
    Digital().initiate(project)
  }
}

/**
 * JSON support for jdkgdxds/juniper to/from GDX. JDK 8+.
 * @author Tommy Ettinger
 */
@Extension
class JdkgdxdsInterop : ThirdPartyExtension() {
  override val id = "jdkgdxdsInterop"
  override val defaultVersion = "1.4.8.0"
  override val url = "https://github.com/tommyettinger/jdkgdxds_interop"
  override val group = "com.github.tommyettinger"
  override val name = "jdkgdxds_interop"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:jdkgdxds_interop")

    addDependency(project, GWT.ID, "com.github.tommyettinger:jdkgdxds_interop:sources")
    addGwtInherit(project, "com.github.tommyettinger.jdkgdxds_interop")

    Jdkgdxds().initiate(project)
    Juniper().initiate(project)
  }
}

/**
 * Kryo support for RegExodus types.
 * @author Tommy Ettinger
 */
@Extension
class KryoRegExodus : ThirdPartyExtension() {
  override val id = "kryoRegExodus"
  override val defaultVersion = "0.1.15.1"
  override val url = "https://github.com/tommyettinger/kryo-more"
  override val group = "com.github.tommyettinger"
  override val name = "kryo-regexodus"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:kryo-regexodus")

    Kryo().initiate(project)
    RegExodus().initiate(project)
  }
}

/**
 * Kryo support for digital's types.
 * @author Tommy Ettinger
 */
@Extension
class KryoDigital : ThirdPartyExtension() {
  override val id = "kryoDigital"
  override val defaultVersion = "0.4.7.1"
  override val url = "https://github.com/tommyettinger/kryo-more"
  override val group = "com.github.tommyettinger"
  override val name = "kryo-digital"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:kryo-digital")

    Kryo().initiate(project)
    Digital().initiate(project)
  }
}

/**
 * Kryo support for juniper's types.
 * @author Tommy Ettinger
 */
@Extension
class KryoJuniper : ThirdPartyExtension() {
  override val id = "kryoJuniper"
  override val defaultVersion = "0.5.0.1"
  override val url = "https://github.com/tommyettinger/kryo-more"
  override val group = "com.github.tommyettinger"
  override val name = "kryo-juniper"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:kryo-juniper")

    Kryo().initiate(project)
    Juniper().initiate(project)
    KryoDigital().initiate(project)
  }
}

/**
 * Kryo support for jdkgdxds's types.
 * @author Tommy Ettinger
 */
@Extension
class KryoJdkgdxds : ThirdPartyExtension() {
  override val id = "kryoJdkgdxds"
  override val defaultVersion = "1.4.8.1"
  override val url = "https://github.com/tommyettinger/kryo-more"
  override val group = "com.github.tommyettinger"
  override val name = "kryo-jdkgdxds"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:kryo-jdkgdxds")

    Kryo().initiate(project)
    Jdkgdxds().initiate(project)
    KryoDigital().initiate(project)
  }
}

/**
 * Kryo support for cringe's types.
 * @author Tommy Ettinger
 */
@Extension
class KryoCringe : ThirdPartyExtension() {
  override val id = "kryoCringe"
  override val defaultVersion = "0.1.1.1"
  override val url = "https://github.com/tommyettinger/kryo-more"
  override val group = "com.github.tommyettinger"
  override val name = "kryo-cringe"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:kryo-cringe")

    Kryo().initiate(project)
    Cringe().initiate(project)
  }
}

/**
 * Tantrum support for libGDX types.
 * @author Tommy Ettinger
 */
@Extension
class TantrumLibgdx : ThirdPartyExtension() {
  override val id = "tantrumLibgdx"
  override val defaultVersion = "1.12.1.0"
  override val url = "https://github.com/tommyettinger/tantrum"
  override val group = "com.github.tommyettinger"
  override val name = "tantrum-libgdx"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:tantrum-libgdx")

    Fury().initiate(project)
  }
}

/**
 * Tantrum support for RegExodus types.
 * @author Tommy Ettinger
 */
@Extension
class TantrumRegExodus : ThirdPartyExtension() {
  override val id = "tantrumRegExodus"
  override val defaultVersion = "0.1.15.0"
  override val url = "https://github.com/tommyettinger/tantrum"
  override val group = "com.github.tommyettinger"
  override val name = "tantrum-regexodus"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:tantrum-regexodus")

    Fury().initiate(project)
    RegExodus().initiate(project)
  }
}

/**
 * Tantrum support for digital's types.
 * @author Tommy Ettinger
 */
@Extension
class TantrumDigital : ThirdPartyExtension() {
  override val id = "tantrumDigital"
  override val defaultVersion = "0.4.7.0"
  override val url = "https://github.com/tommyettinger/tantrum"
  override val group = "com.github.tommyettinger"
  override val name = "tantrum-digital"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:tantrum-digital")

    Fury().initiate(project)
    Digital().initiate(project)
  }
}

/**
 * Tantrum support for juniper's types.
 * @author Tommy Ettinger
 */
@Extension
class TantrumJuniper : ThirdPartyExtension() {
  override val id = "tantrumJuniper"
  override val defaultVersion = "0.5.0.0"
  override val url = "https://github.com/tommyettinger/tantrum"
  override val group = "com.github.tommyettinger"
  override val name = "tantrum-juniper"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:tantrum-juniper")

    Fury().initiate(project)
    Juniper().initiate(project)
    TantrumDigital().initiate(project)
  }
}

/**
 * Tantrum support for jdkgdxds's types.
 * @author Tommy Ettinger
 */
@Extension
class TantrumJdkgdxds : ThirdPartyExtension() {
  override val id = "tantrumJdkgdxds"
  override val defaultVersion = "1.4.8.0"
  override val url = "https://github.com/tommyettinger/tantrum"
  override val group = "com.github.tommyettinger"
  override val name = "tantrum-jdkgdxds"

  override fun initiateDependencies(project: Project) {
    addDependency(project, Core.ID, "com.github.tommyettinger:tantrum-jdkgdxds")

    Fury().initiate(project)
    Jdkgdxds().initiate(project)
    TantrumDigital().initiate(project)
  }
}

//
//    /**
//     * An immediate-mode GUI library (LWJGL3-only!) that can be an alternative to scene2d.ui.
//     * NOTE: this is only accessible from the lwjgl3 project, and may require unusual
//     * project configuration to use.
//     * @author SpaiR
//     */
//    @Extension
//    class Imgui : ThirdPartyExtension() {
//        override val id = "imgui"
//        override val defaultVersion = "1.82.2"
//        override val url = "https://github.com/SpaiR/imgui-java"
//
//        override fun initiateDependencies(project: Project) {
//
//            addDependency(project, LWJGL3.ID, "io.github.spair:imgui-java-binding");
//            addDependency(project, LWJGL3.ID, "io.github.spair:imgui-java-lwjgl3");
//            addDependency(project, LWJGL3.ID, "io.github.spair:imgui-java-natives-linux");
//            addDependency(project, LWJGL3.ID, "io.github.spair:imgui-java-natives-linux-x86");
//            addDependency(project, LWJGL3.ID, "io.github.spair:imgui-java-natives-macos");
//            addDependency(project, LWJGL3.ID, "io.github.spair:imgui-java-natives-windows");
//            addDependency(project, LWJGL3.ID, "io.github.spair:imgui-java-natives-windows-x86");
//
// //            addDependency(project, Core.ID, "com.github.kotlin-graphics.imgui:core")
// //            addDependency(project, LWJGL3.ID, "com.github.kotlin-graphics.imgui:gl")
// //            addDependency(project, LWJGL3.ID, "com.github.kotlin-graphics.imgui:glfw")
//        }
//    }
