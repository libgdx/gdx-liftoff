@file:Suppress("unused") // Extension classes accessed via reflection.

package gdx.liftoff.data.libraries.official

import com.badlogic.gdx.Version
import gdx.liftoff.data.libraries.Library
import gdx.liftoff.data.libraries.Repository
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.platforms.GWT
import gdx.liftoff.data.platforms.Headless
import gdx.liftoff.data.platforms.IOS
import gdx.liftoff.data.platforms.Lwjgl2
import gdx.liftoff.data.platforms.Lwjgl3
import gdx.liftoff.data.platforms.TeaVM
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.Extension

/**
 * Abstract base for official extensions.
 */
abstract class OfficialExtension : Library {
  override val defaultVersion = Version.VERSION
  override val official = true
  override val repository = Repository.MavenCentral
  override val group = "com.badlogicgames.gdx"
  override val name: String
    get() = id
}

/**
 * Official AI utilities.
 */
@Extension(official = true)
class AI : OfficialExtension() {
  override val id = "gdx-ai"
  override val url = "https://github.com/libgdx/gdx-ai"
  override val defaultVersion = "1.8.2"

  override fun initiate(project: Project) {
    project.properties["aiVersion"] = version

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
  override val group = "com.badlogicgames.ashley"
  override val defaultVersion = "1.7.4"

  override fun initiate(project: Project) {
    project.properties[id + "Version"] = version

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
  override val url = "https://libgdx.com/wiki/extensions/physics/box2d"

  override fun initiate(project: Project) {
    addDependency(project, Core.ID, "com.badlogicgames.gdx:gdx-box2d:\$gdxVersion")

    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-armeabi-v7a")
    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-arm64-v8a")
    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-x86")
    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-x86_64")

    addDesktopDependency(project, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-desktop")
    addDependency(project, Headless.ID, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-desktop")

    addDependency(project, GWT.ID, "com.badlogicgames.gdx:gdx-box2d:\$gdxVersion:sources")
    addSpecialDependency(project, GWT.ID, "implementation(\"com.badlogicgames.gdx:gdx-box2d-gwt:\$gdxVersion:sources\") {exclude group: \"com.google.gwt\", module: \"gwt-user\"}")
    addGwtInherit(project, "com.badlogic.gdx.physics.box2d.box2d-gwt")

    addDependency(project, IOS.ID, "com.badlogicgames.gdx:gdx-box2d-platform:\$gdxVersion:natives-ios")

    // TeaVM version of the Box2D is not feature complete; however, TeaVM can compile the GWT Box2D library.
    // addDependency(project, TeaVM.ID, "com.github.xpenatan.gdx-teavm:gdx-box2d-teavm:1.0.0-b6")
    addDependency(project, TeaVM.ID, "com.badlogicgames.gdx:gdx-box2d-gwt:\$gdxVersion")
  }
}

/**
 * Official 2D lights extension to Box2D.
 */
@Extension(official = true)
class Box2DLights : OfficialExtension() {
  override val id = "box2dlights"
  override val url = "https://github.com/libgdx/box2dlights"
  override val group = "com.badlogicgames.box2dlights"
  override val defaultVersion = "1.5"

  override fun initiate(project: Project) {
    project.properties[id + "Version"] = version

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
  override val url = "https://libgdx.com/wiki/extensions/physics/bullet/bullet-physics"

  override fun initiate(project: Project) {
    addDependency(project, Core.ID, "com.badlogicgames.gdx:gdx-bullet:\$gdxVersion")

    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-armeabi-v7a")
    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-arm64-v8a")
    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-x86")
    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-x86_64")

    addDesktopDependency(project, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-desktop")

    addDependency(project, IOS.ID, "com.badlogicgames.gdx:gdx-bullet-platform:\$gdxVersion:natives-ios")

    addDependency(project, TeaVM.ID, "com.github.xpenatan.gdx-teavm:gdx-bullet-teavm:1.0.0-b6")

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
  override val group = "com.badlogicgames.gdx-controllers"
  override val name = "gdx-controllers-core"
  override val defaultVersion = "2.2.3"

  override fun initiate(project: Project) {
    project.properties["gdxControllersVersion"] = version

    addDependency(project, Core.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-core:\$gdxControllersVersion")

    addDependency(project, Android.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-android:\$gdxControllersVersion")

    addDependency(project, Lwjgl2.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-desktop:\$gdxControllersVersion")

    addDependency(project, Lwjgl3.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-desktop:\$gdxControllersVersion")

    addDependency(project, GWT.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-core:\$gdxControllersVersion:sources")
    addSpecialDependency(project, GWT.ID, "implementation(\"com.badlogicgames.gdx-controllers:gdx-controllers-gwt:\$gdxControllersVersion:sources\"){exclude group: \"com.badlogicgames.gdx\", module: \"gdx-backend-gwt\"}")
    addGwtInherit(project, "com.badlogic.gdx.controllers")
    addGwtInherit(project, "com.badlogic.gdx.controllers.controllers-gwt")

    addDependency(project, IOS.ID, "com.badlogicgames.gdx-controllers:gdx-controllers-ios:\$gdxControllersVersion")
  }
}

/**
 * Official TTF fonts support.
 */
@Extension(official = true)
class Freetype : OfficialExtension() {
  override val id = "gdx-freetype"
  override val url = "https://libgdx.com/wiki/extensions/gdx-freetype"

  override fun initiate(project: Project) {
    addDependency(project, Core.ID, "com.badlogicgames.gdx:gdx-freetype:\$gdxVersion")

    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-armeabi-v7a")
    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-arm64-v8a")
    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-x86")
    addNativeAndroidDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-x86_64")

    addDesktopDependency(project, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-desktop")

    addDependency(project, IOS.ID, "com.badlogicgames.gdx:gdx-freetype-platform:\$gdxVersion:natives-ios")

    addDependency(project, TeaVM.ID, "com.github.xpenatan.gdx-teavm:gdx-freetype-teavm:\$gdxTeaVMVersion")

    // Other platforms are not officially supported (GWT).
  }
}

/**
 * Official libGDX tools extension; only usable by Desktop ("Legacy" LWJGL 2) modules.
 */
@Extension(official = true)
class Tools : OfficialExtension() {
  override val id = "gdx-tools"
  override val url = "https://libgdx.com/wiki/tools/texture-packer"

  override fun initiate(project: Project) {
    addDependency(project, Lwjgl2.ID, "com.badlogicgames.gdx:gdx-tools:\$gdxVersion")

    // Headless is unlikely to work because gdx-tools relies on graphics classes.
    // addDependency(project, Headless.ID, "com.badlogicgames.gdx:gdx-tools:\$gdxVersion")
  }
}
