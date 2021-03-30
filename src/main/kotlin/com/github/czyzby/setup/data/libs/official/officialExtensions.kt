package com.github.czyzby.setup.data.libs.official

import com.badlogic.gdx.Version
import com.github.czyzby.setup.data.libs.Library
import com.github.czyzby.setup.data.platforms.*
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.Extension

/**
 * Abstract base for official extensions.
 * @author MJ
 */
abstract class OfficialExtension : Library {
    override val defaultVersion = Version.VERSION
    override val official = true
}

/**
 * Official AI utilities.
 */
@Extension(official = true)
class AI : OfficialExtension() {
    override val id = "gdx-ai"
    override val url = "https://github.com/libgdx/gdx-ai"

    override fun initiate(project: Project) {
        project.properties["aiVersion"] = "1.8.2"

        addDependency(project, Core.ID, "com.badlogicgames.gdx:gdx-ai:\$aiVersion")

        addDependency(project, GWT.ID, "com.badlogicgames.gdx:gdx-ai:\$aiVersion:sources")
        addGwtInherit(project, "com.badlogic.gdx.ai")
    }
}

/**
 * Official entity system.
 */
@Extension(official = true)
class Ashley : OfficialExtension() {
    override val id = "ashley"
    override val url = "https://github.com/libgdx/ashley"

    override fun initiate(project: Project) {
        project.properties[id + "Version"] = "1.7.3"

        addDependency(project, Core.ID, "com.badlogicgames.ashley:ashley:\$ashleyVersion")

        addDependency(project, GWT.ID, "com.badlogicgames.ashley:ashley:\$ashleyVersion:sources")
        addGwtInherit(project, "com.badlogic.ashley_gwt")
    }
}

/**
 * Official 2D physics engine.
 */
@Extension(official = true)
class Box2D : OfficialExtension() {
    override val id = "gdx-box2d"
    override val url = "https://github.com/libgdx/libgdx/wiki/Box2d"

    override fun initiate(project: Project) {
        addDependency(project, Core.ID, "com.badlogicgames.gdx:gdx-box2d:\$gdxVersion")

        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-armeabi")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-armeabi-v7a")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-arm64-v8a")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-x86")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-x86_64")

        addDesktopDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-desktop")
        addDependency(project, Headless.ID, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-desktop")

        addDependency(project, GWT.ID, "com.badlogicgames.gdx:gdx-box2d:\$gdxVersion:sources")
        addDependency(project, GWT.ID, "com.badlogicgames.gdx:gdx-box2d-gwt:\$gdxVersion")
        addDependency(project, GWT.ID, "com.badlogicgames.gdx:gdx-box2d-gwt:\$gdxVersion:sources")
        addGwtInherit(project, "com.badlogic.gdx.physics.box2d.box2d-gwt")

        addDependency(project, iOS.ID, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-ios")
    }
}

/**
 * Official 2D lights extension to Box2D.
 */
@Extension(official = true)
class Box2DLights : OfficialExtension() {
    override val id = "box2dlights"
    override val url = "https://github.com/libgdx/box2dlights"

    override fun initiate(project: Project) {
        project.properties[id + "Version"] = "1.5"

        addDependency(project, Core.ID, "com.badlogicgames.box2dlights:box2dlights:\$box2dlightsVersion")

        addDependency(project, GWT.ID, "com.badlogicgames.box2dlights:box2dlights:\$box2dlightsVersion:sources")
        addGwtInherit(project, "Box2DLights")

        // Making sure Box2D is included as well:
        Box2D().initiate(project)
    }
}

/**
 * Official 3D physics engine.
 */
@Extension(official = true)
class Bullet : OfficialExtension() {
    override val id = "gdx-bullet"
    override val url = "https://github.com/libgdx/libgdx/wiki/Bullet-physics"

    override fun initiate(project: Project) {
        addDependency(project, Core.ID, "com.badlogicgames.gdx:gdx-bullet:\$gdxVersion")

        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-armeabi")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-armeabi-v7a")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-arm64-v8a")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-x86")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-x86_64")

        addDesktopDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-desktop")

        addDependency(project, iOS.ID, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-ios")
        
        // Other platforms are not officially supported (GWT).
    }
}

/**
 * Official controllers support. See https://github.com/libgdx/gdx-controllers for Android ProGuard info.
 * Note, the code for gdx-controllers (and its version) are separate from the earlier gdx-controllers extension.
 * If you used a version of gdx-controllers before 2.0.0, there may be some important changes.
 */
@Extension(official = true)
class Controllers : OfficialExtension() {
    override val id = "gdx-controllers"
    override val url = "https://github.com/libgdx/gdx-controllers"

    override fun initiate(project: Project) {
        project.properties["gdxControllersVersion"] = "2.2.0"

        addDependency(project, Core.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-core:\$gdxControllersVersion")

        addDependency(project, Android.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-android:\$gdxControllersVersion")

        addDependency(project, Desktop.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-desktop:\$gdxControllersVersion")

        addDependency(project, LWJGL3.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-desktop:\$gdxControllersVersion")

        addDependency(project, GWT.ID,"com.badlogicgames.gdx-controllers:gdx-controllers-core:\$gdxControllersVersion:sources")
        addDependency(project, GWT.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-gwt:\$gdxControllersVersion:sources")
        addGwtInherit(project, "com.badlogic.gdx.controllers")
        addGwtInherit(project, "com.badlogic.gdx.controllers.controllers-gwt")

        addDependency(project, iOS.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-ios:\$gdxControllersVersion")
    }
}

/**
 * Official TTF fonts support.
 */
@Extension(official = true)
class Freetype : OfficialExtension() {
    override val id = "gdx-freetype"
    override val url = "https://github.com/libgdx/libgdx/wiki/Gdx-freetype"

    override fun initiate(project: Project) {
        addDependency(project, Core.ID, "com.badlogicgames.gdx:gdx-freetype:\$gdxVersion")

        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-armeabi")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-armeabi-v7a")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-arm64-v8a")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-x86")
        addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-x86_64")

        addDesktopDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-desktop")

        addDependency(project, iOS.ID, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-ios")

        // Other platforms are not officially supported (GWT).
    }
}

/**
 * Official libGDX tools extension; only usable by Desktop ("Legacy" LWJGL 2) modules.
 */
@Extension(official = true)
class Tools : OfficialExtension() {
    override val id = "gdx-tools"
    override val url = "https://github.com/libgdx/libgdx/wiki/Texture-packer"

    override fun initiate(project: Project) {
        addDependency(project, Desktop.ID, "com.badlogicgames.gdx:gdx-tools:\$gdxVersion")

        //// Headless is unlikely to work because gdx-tools relies on graphics classes.
        // addDependency(project, Headless.ID, "com.badlogicgames.gdx:gdx-tools:\$gdxVersion")
    }
}
