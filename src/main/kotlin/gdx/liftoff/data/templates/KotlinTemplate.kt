package gdx.liftoff.data.templates

import gdx.liftoff.data.files.path
import gdx.liftoff.data.languages.Kotlin
import gdx.liftoff.data.project.Project

/**
 * Basic interface for Kotlin project templates. Adds a Kotlin launcher for each platform.
 */
interface KotlinTemplate : Template {
	override val applicationListenerExtension: String
		get() = "kt"
	override val launcherExtension: String
		get() = "kt"
	override val defaultSourceFolder: String
		get() = path("src", "main", "kotlin")

	override fun apply(project: Project) {
		super.apply(project)
		project.languages.selectLanguage<Kotlin>()
	}

	override fun getDesktopLauncherContent(project: Project): String = """@file:JvmName("DesktopLauncher")

package ${project.basic.rootPackage}.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the desktop (LWJGL) application. */
fun main() {
	LwjglApplication(${project.basic.mainClass}(), LwjglApplicationConfiguration().apply {
		title = "${project.basic.name}"
		width = $width
		height = $height
		intArrayOf(128, 64, 32, 16).forEach{
			addIcon("libgdx${"$"}it.png", Files.FileType.Internal)
		}
	})
}
"""

	override fun getAndroidLauncherContent(project: Project): String = """package ${project.basic.rootPackage}.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initialize(${project.basic.mainClass}(), AndroidApplicationConfiguration().apply {
			// Configure your application here.
		})
	}
}
"""

	override fun getHeadlessLauncherContent(project: Project): String = """@file:JvmName("HeadlessLauncher")

package ${project.basic.rootPackage}.headless

import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the headless application. Can be converted into a server application or a scripting utility. */
fun main() {
	HeadlessApplication(${project.basic.mainClass}(), HeadlessApplicationConfiguration().apply {
		// When this value is negative, ${project.basic.mainClass}#render() is never called:
		updatesPerSecond = -1
	})
}
"""

	override fun getLwjgl3LauncherContent(project: Project): String = """@file:JvmName("Lwjgl3Launcher")

package ${project.basic.rootPackage}.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the desktop (LWJGL3) application. */
fun main() {
	Lwjgl3Application(${project.basic.mainClass}(), Lwjgl3ApplicationConfiguration().apply {
		setTitle("${project.basic.name}")
		setWindowedMode($width, $height)
		setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx${"$"}it.png" }.toTypedArray()))
	})
}
"""

	override fun getIOSLauncherContent(project: Project): String = """@file:JvmName("IOSLauncher")

package ${project.basic.rootPackage}.ios

import org.robovm.apple.foundation.NSAutoreleasePool
import org.robovm.apple.uikit.UIApplication

import com.badlogic.gdx.backends.iosrobovm.IOSApplication
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration
import ${project.basic.rootPackage}.${project.basic.mainClass}

/** Launches the iOS (RoboVM) application. */
class IOSLauncher : IOSApplication.Delegate() {
	override fun createApplication(): IOSApplication {
		return IOSApplication(${project.basic.mainClass}(), IOSApplicationConfiguration().apply {
			// Configure your application here.
		})
	}

	companion object {
		@JvmStatic fun main(args: Array<String>) {
			val pool = NSAutoreleasePool()
			val principalClass: Class<UIApplication>? = null
			val delegateClass = IOSLauncher::class.java
			UIApplication.main(args, principalClass, delegateClass)
			pool.close()
		}
	}
}"""

	override fun getGwtLauncherContent(project: Project): String = """package ${project.basic.rootPackage}.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.utils.GdxRuntimeException;
// import ${project.basic.rootPackage}.${project.basic.mainClass};

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration($width, $height);
	}

	@Override
	public ApplicationListener createApplicationListener () {
		throw new GdxRuntimeException("Kotlin is currently not supported by GWT.");
		// return new ${project.basic.mainClass}();
	}
}
"""

	override fun getServerLauncherContent(project: Project) = """@file:JvmName("ServerLauncher")

package ${project.basic.rootPackage}.server

/** Launches the server application. */
fun main() {
	TODO("Implement server application.")
}
"""

	override fun getTeaVMLauncherContent(project: Project): String = """@file:JvmName("TeaVMLauncher")

package ${project.basic.rootPackage}.teavm

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.github.xpenatan.gdx.backends.web.WebApplication
import ${project.basic.rootPackage}.${project.basic.mainClass}

fun main() {
	val config = TeaApplicationConfiguration("canvas")
	config.width = $width
	config.height = $height
	WebApplication(${project.basic.mainClass}(), config)
}
"""

	override fun getTeaVMBuilderContent(project: Project) = """@file:JvmName("TeaVMBuilder")

package ${project.basic.rootPackage}.teavm

import java.io.File
import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier

fun main() {
	val teaBuildConfiguration = TeaBuildConfiguration()
	teaBuildConfiguration.assetsPath.add(File("../assets"))
	teaBuildConfiguration.webappPath = File("build/dist").canonicalPath
	// You can switch this setting during development:
	teaBuildConfiguration.obfuscate = true

	// Register any extra classpath assets here:
	// teaBuildConfiguration.additionalAssetsClasspathFiles += "${project.basic.rootPackage.replace('.', '/')}/asset.extension"

	// Register any classes or packages that require reflection here:
${generateTeaVMReflectionIncludes(project, indent = "\t", trailingSemicolon = false)}

	val tool = TeaBuilder.config(teaBuildConfiguration)
	tool.setMainClass("${project.basic.rootPackage}.teavm.TeaVMLauncher")
	TeaBuilder.build(tool)
}
"""
}
