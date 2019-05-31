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
    override fun initiate(project: Project) {
        project.rootGradle.buildDependencies.add("\"com.android.tools.build:gradle:\$androidPluginVersion\"")
        project.properties["androidPluginVersion"] = project.advanced.androidPluginVersion

        addGradleTaskDescription(project, "lint", "performs Android project validation.")

        addCopiedFile(project, "ic_launcher-web.png")
        addCopiedFile(project, "proguard-project.txt")
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
    package="${project.basic.rootPackage}"
    android:versionCode="1"
    android:versionName="1.0" >
    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/GdxTheme" >
        <activity
            android:name="${project.basic.rootPackage}.android.AndroidLauncher"
            android:label="@string/app_name"
            android:screenOrientation="landscape"
            android:configChanges="keyboard|keyboardHidden|orientation|screenSize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
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
    val nativeDependencies = mutableSetOf<String>()
    var latePlugin = false
    init {
        dependencies.add("project(':${Core.ID}')")
        addDependency("com.badlogicgames.gdx:gdx-backend-android:\$gdxVersion")
        addNativeDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-armeabi")
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

android {
  compileSdkVersion ${project.advanced.androidSdkVersion}
  sourceSets {
    main {
      manifest.srcFile 'AndroidManifest.xml'
      java.srcDirs = ['src/main/java']
      aidl.srcDirs = ['src/main/java']
      renderscript.srcDirs = ['src/main/java']
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
    minSdkVersion 14
    targetSdkVersion ${project.advanced.androidSdkVersion}
  }
}

repositories {
  // needed for AAPT2, may be needed for other tools
  google()
}

configurations { natives }

dependencies {
${joinDependencies(dependencies)}
${joinDependencies(nativeDependencies, "natives")}
}

// Called every time gradle gets executed, takes the native dependencies of
// the natives configuration, and extracts them to the proper libs/ folders
// so they get packed with the APK.
task copyAndroidNatives() {
  doFirst {
    file("libs/armeabi/").mkdirs()
    file("libs/armeabi-v7a/").mkdirs()
    file("libs/arm64-v8a/").mkdirs()
    file("libs/x86_64/").mkdirs()
    file("libs/x86/").mkdirs()
    
    configurations.natives.files.each { jar ->
      def outputDir = null
      if(jar.name.endsWith("natives-arm64-v8a.jar")) outputDir = file("libs/arm64-v8a")
      if(jar.name.endsWith("natives-armeabi-v7a.jar")) outputDir = file("libs/armeabi-v7a")
      if(jar.name.endsWith("natives-armeabi.jar")) outputDir = file("libs/armeabi")
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
    ${if(latePlugin == true)"apply plugin: \'kotlin-android\'" else ""}
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
  commandLine "${'$'}adb", 'shell', 'am', 'start', '-n', '${project.basic.rootPackage}/${project.basic.rootPackage}.android.AndroidLauncher'
}

// Sets up the Android Eclipse project using the old Ant based build.
eclipse {
  // needs to specify Java source sets explicitly, SpringSource Gradle Eclipse plugin
  // ignores any nodes added in classpath.file.withXml
  sourceSets {
    main {
      java.srcDirs 'src/main/java', 'gen'
    }
  }

  jdt {
    sourceCompatibility = ${project.advanced.javaVersion}
    targetCompatibility = ${project.advanced.javaVersion}
  }

  classpath {
    plusConfigurations += [ project.configurations.compileClasspath ]
    containers 'com.android.ide.eclipse.adt.ANDROID_FRAMEWORK', 'com.android.ide.eclipse.adt.LIBRARIES'
  }

  project {
    name = appName + "-android"
    natures 'com.android.ide.eclipse.adt.AndroidNature'
    buildCommands.clear()
    buildCommand "com.android.ide.eclipse.adt.ResourceManagerBuilder"
    buildCommand "com.android.ide.eclipse.adt.PreCompilerBuilder"
    buildCommand "org.eclipse.jdt.core.javabuilder"
    buildCommand "com.android.ide.eclipse.adt.ApkBuilder"
  }
}

// Sets up the Android Idea project using the old Ant based build.
idea {
  module {
    sourceDirs += file("src/main/java")
    scopes = [ COMPILE: [plus:[project.configurations.compileClasspath]]]
    iml {
      withXml {
        def node = it.asNode()
        def builder = NodeBuilder.newInstance()
        builder.current = node
        builder.component(name: "FacetManager") {
          facet(type: "android", name: "Android") {
            configuration {
              option(name: "UPDATE_PROPERTY_FILES", value:"true")
            }
          }
        }
      }
    }
  }
}
"""
}
