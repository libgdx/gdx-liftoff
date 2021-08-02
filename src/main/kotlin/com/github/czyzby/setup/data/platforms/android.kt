package com.github.czyzby.setup.data.platforms

import com.github.czyzby.setup.data.files.SourceFile
import com.github.czyzby.setup.data.gradle.GradleFile
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.GdxPlatform

/**
 * Represents Android backend.
 * @author MJ
 */
@GdxPlatform
class Android : Platform {
	companion object {
		const val ID = "android"
	}

	override val id = ID
	override val isStandard = false // user should only jump through android hoops on request
	override fun initiate(project: Project) {
		project.rootGradle.buildDependencies.add("\"com.android.tools.build:gradle:\$androidPluginVersion\"")
		project.properties["androidPluginVersion"] = project.advanced.androidPluginVersion

		addGradleTaskDescription(project, "lint", "performs Android project validation.")

		addCopiedFile(project, "ic_launcher-web.png")
		addCopiedFile(project, "proguard-rules.pro")
		addCopiedFile(project, "project.properties")
		addCopiedFile(project, "res", "drawable-hdpi", "ic_launcher.png")
		addCopiedFile(project, "res", "drawable-mdpi", "ic_launcher.png")
		addCopiedFile(project, "res", "drawable-xhdpi", "ic_launcher.png")
		addCopiedFile(project, "res", "drawable-xxhdpi", "ic_launcher.png")
		addCopiedFile(project, "res", "values", "styles.xml")

		project.files.add(SourceFile(projectName = "", sourceFolderPath = "", packageName = "", fileName = "local.properties",
				content = "# Location of the Android SDK:\nsdk.dir=${project.basic.androidSdk}"))
		project.files.add(SourceFile(projectName = ID, sourceFolderPath = "res", packageName = "values", fileName = "strings.xml",
				content = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">${project.basic.name}</string>
</resources>
"""))
		project.files.add(SourceFile(projectName = ID, sourceFolderPath = "", packageName = "", fileName = "AndroidManifest.xml",
				content = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:tools="http://schemas.android.com/tools"
		package="${project.basic.rootPackage}">
	<uses-feature android:glEsVersion="0x00020000" android:required="true"/>
	<application
			android:allowBackup="true"
        	android:fullBackupContent="true"
			android:icon="@drawable/ic_launcher"
			android:isGame="true"
			android:appCategory="game"
        	android:label="@string/app_name"
        	tools:ignore="UnusedAttribute"
			android:theme="@style/GdxTheme">
		<activity
				android:name="${project.basic.rootPackage}.AndroidLauncher"
				android:label="@string/app_name"
				android:screenOrientation="landscape"
				android:configChanges="keyboard|keyboardHidden|navigation|orientation|screenSize|screenLayout"
          		android:exported="true">
				<intent-filter>
				<action android:name="android.intent.action.MAIN"/>
				<category android:name="android.intent.category.LAUNCHER"/>
			</intent-filter>
		</activity>
	</application>
${project.androidPermissions.joinToString(separator = "\n") { "    <uses-permission android:name=\"${it}\" />" }}
</manifest>
"""))
	}

	override fun createGradleFile(project: Project): GradleFile = AndroidGradleFile(project)
}

/**
 * Gradle file of the Android project.
 * @author MJ
 */
class AndroidGradleFile(val project: Project) : GradleFile(Android.ID) {
	val plugins = mutableListOf<String>()
	val srcFolders = mutableListOf("'src/main/java'")
	val nativeDependencies = mutableSetOf<String>()
	var latePlugin = false
	init {
		dependencies.add("project(':${Core.ID}')")
		addDependency("com.badlogicgames.gdx:gdx-backend-android:\$gdxVersion")
		addNativeDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-armeabi-v7a")
		addNativeDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-arm64-v8a")
		addNativeDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-x86")
		addNativeDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-x86_64")
		plugins.add("com.android.application")
	}

	fun insertLatePlugin() { latePlugin = true }
	/**
	 * @param dependency will be added as "natives" dependency, quoted.
	 */
	fun addNativeDependency(dependency: String) = nativeDependencies.add("\"$dependency\"")

	override fun getContent(): String = """${plugins.joinToString(separator = "\n") { "apply plugin: '$it'" }}
${if(latePlugin)"apply plugin: \'kotlin-android\'" else ""}

android {
	compileSdkVersion ${project.advanced.androidSdkVersion}
	sourceSets {
		main {
			manifest.srcFile 'AndroidManifest.xml'
			java.srcDirs = [${srcFolders.joinToString(separator = ", ")}]
			aidl.srcDirs = [${srcFolders.joinToString(separator = ", ")}]
			renderscript.srcDirs = [${srcFolders.joinToString(separator = ", ")}]
			res.srcDirs = ['res']
			assets.srcDirs = ['../assets']
			jniLibs.srcDirs = ['libs']
		}
	}
	packagingOptions {
		// Preventing from license violations (more or less):
		pickFirst 'META-INF/LICENSE.txt'
		pickFirst 'META-INF/LICENSE'
		pickFirst 'META-INF/license.txt'
		pickFirst 'META-INF/LGPL2.1'
		pickFirst 'META-INF/NOTICE.txt'
		pickFirst 'META-INF/NOTICE'
		pickFirst 'META-INF/notice.txt'
		// Excluding unnecessary meta-data:
		exclude 'META-INF/robovm/ios/robovm.xml'
		exclude 'META-INF/DEPENDENCIES.txt'
		exclude 'META-INF/DEPENDENCIES'
		exclude 'META-INF/dependencies.txt'
	}
	defaultConfig {
		applicationId '${project.basic.rootPackage}'
		minSdkVersion 19
		targetSdkVersion ${project.advanced.androidSdkVersion}
		versionCode 1
		versionName "1.0"
		multiDexEnabled true
	}
	compileOptions {
		sourceCompatibility "${project.advanced.javaVersion}"
		targetCompatibility "${project.advanced.javaVersion}"
		${if(project.advanced.javaVersion != "1.6" && project.advanced.javaVersion != "1.7")"coreLibraryDesugaringEnabled true" else ""}
	}
	${if(latePlugin && project.advanced.javaVersion != "1.6" && project.advanced.javaVersion != "1.7")"kotlinOptions.jvmTarget = \"1.8\"" else ""}
	buildTypes {
		release {
			minifyEnabled false
			proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
		}
	}

}

repositories {
	// needed for AAPT2, may be needed for other tools
	google()
}

configurations { natives }

dependencies {
	${if(project.advanced.javaVersion != "1.6" && project.advanced.javaVersion != "1.7")"coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:1.1.5'" else ""}
${joinDependencies(dependencies)}
${joinDependencies(nativeDependencies, "natives")}
}

// Called every time gradle gets executed, takes the native dependencies of
// the natives configuration, and extracts them to the proper libs/ folders
// so they get packed with the APK.
task copyAndroidNatives() {
	doFirst {
		file("libs/armeabi-v7a/").mkdirs()
		file("libs/arm64-v8a/").mkdirs()
		file("libs/x86_64/").mkdirs()
		file("libs/x86/").mkdirs()
		
		configurations.getByName("natives").copy().files.each { jar ->
    	    def outputDir = null
    	    if(jar.name.endsWith("natives-armeabi-v7a.jar")) outputDir = file("libs/armeabi-v7a")
    	    if(jar.name.endsWith("natives-arm64-v8a.jar")) outputDir = file("libs/arm64-v8a")
    	    if(jar.name.endsWith("natives-x86_64.jar")) outputDir = file("libs/x86_64")
    	    if(jar.name.endsWith("natives-x86.jar")) outputDir = file("libs/x86")
    	    if(outputDir != null) {
    	        copy {
    	            from zipTree(jar)
    	            into outputDir
    	            include "*.so"
    	        }
    	    }
    	}
	}
}

tasks.whenTaskAdded { packageTask ->
  if (packageTask.name.contains("package")) {
    packageTask.dependsOn 'copyAndroidNatives'
  }
}

task run(type: Exec) {
	def path
	def localProperties = project.file("../local.properties")
	if (localProperties.exists()) {
		Properties properties = new Properties()
		localProperties.withInputStream { instr ->
			properties.load(instr)
		}
		def sdkDir = properties.getProperty('sdk.dir')
		if (sdkDir) {
			path = sdkDir
		} else {
			path = "${'$'}System.env.ANDROID_HOME"
		}
	} else {
		path = "${'$'}System.env.ANDROID_HOME"
	}

	def adb = path + "/platform-tools/adb"
	commandLine "${'$'}adb", 'shell', 'am', 'start', '-n', '${project.basic.rootPackage}/${project.basic.rootPackage}.AndroidLauncher'
}

eclipse.project.name = appName + "-android"
"""
}
