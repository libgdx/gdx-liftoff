package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.SourceFile
import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents Android backend.
 */
@GdxPlatform
class Android : Platform {
  companion object {
    const val ID = "android"
    const val ORDER = Lwjgl3.ORDER + 1
  }

  override val id = ID
  override val description = "Android mobile platform. Needs Android SDK."
  override val order = ORDER
  override val isStandard = false // user should only jump through android hoops on request
  override fun initiate(project: Project) {
    project.rootGradle.buildDependencies.add("\"com.android.tools.build:gradle:\$androidPluginVersion\"")
    project.properties["androidPluginVersion"] = project.advanced.androidPluginVersion
    project.properties["android.useAndroidX"] = "true"
    project.properties["android.enableR8.fullMode"] = "false"
    addGradleTaskDescription(project, "lint", "performs Android project validation.")

    addCopiedFile(project, "ic_launcher-web.png")
    addCopiedFile(project, "proguard-rules.pro")
    addCopiedFile(project, "project.properties")
    addCopiedFile(project, "res", "drawable-hdpi", "ic_launcher.png")
    addCopiedFile(project, "res", "drawable-mdpi", "ic_launcher.png")
    addCopiedFile(project, "res", "drawable-xhdpi", "ic_launcher.png")
    addCopiedFile(project, "res", "drawable-xxhdpi", "ic_launcher.png")
    addCopiedFile(project, "res", "values", "styles.xml")

    project.files.add(
      SourceFile(
        projectName = "",
        sourceFolderPath = "",
        packageName = "",
        fileName = "local.properties",
        content = "# Location of the Android SDK:\nsdk.dir=${project.basic.androidSdk}"
      )
    )
    project.files.add(
      SourceFile(
        projectName = ID,
        sourceFolderPath = "res",
        packageName = "values",
        fileName = "strings.xml",
        content = """<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="app_name">${project.basic.name}</string>
</resources>
"""
      )
    )
    project.files.add(
      SourceFile(
        projectName = ID,
        sourceFolderPath = "",
        packageName = "",
        fileName = "AndroidManifest.xml",
        content = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
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
        android:name="${project.basic.rootPackage}.android.AndroidLauncher"
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
${project.androidPermissions.joinToString(separator = "\n") { "  <uses-permission android:name=\"${it}\" />" }}
</manifest>
"""
      )
    )
  }

  override fun createGradleFile(project: Project): GradleFile = AndroidGradleFile(project)
}

/**
 * Gradle file of the Android project.
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

  override fun getContent(): String {
    // The core library desugaring feature depends heavily on the current Android Gradle Plugin version.
    val agpVersion = project.advanced.androidPluginVersion.split('.').map { it.toInt() }
    return """${plugins.joinToString(separator = "\n") { "apply plugin: '$it'" }}
${if (latePlugin)"apply plugin: \'kotlin-android\'" else ""}

android {
  compileSdk ${project.advanced.androidSdkVersion}
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
    // These are only used by GWT, and not Android.
    exclude '**/*.gwt.xml'
  }
  defaultConfig {
    applicationId '${project.basic.rootPackage}'
    minSdkVersion 19
    targetSdkVersion ${project.advanced.androidSdkVersion}
    versionCode 1
    versionName "1.0"
    multiDexEnabled true
  }
  namespace "${project.basic.rootPackage}"
  compileOptions {
    sourceCompatibility "${project.advanced.javaVersion}"
    targetCompatibility "${project.advanced.javaVersion}"
    ${if (project.advanced.javaVersion != "1.6" && project.advanced.javaVersion != "1.7")"coreLibraryDesugaringEnabled true" else ""}
  }
  buildTypes {
    release {
      minifyEnabled true
      proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
    }
  }${
    if (latePlugin) {
      """

  kotlin {
    jvmToolchain(${project.advanced.javaVersion.removePrefix("1.")})
  }"""
    } else {
      ""
    }}
}

repositories {
  // needed for AAPT2, may be needed for other tools
  google()
}

configurations { natives }

dependencies {
  ${if (project.advanced.javaVersion != "1.6" && project.advanced.javaVersion != "1.7") {
      "coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:" +
        (
          if (agpVersion[0] < 7 || (agpVersion[0] == 7) && agpVersion[1] < 3) {
            "1.1.5"
          } else if (agpVersion[0] == 7 && agpVersion[1] == 3) {
            "1.2.2"
          } else {
            "2.0.3"
          }
          ) + "'"
    } else {
      ""
    }}
${joinDependencies(dependencies)}
${joinDependencies(nativeDependencies, "natives")}
}

// Called every time gradle gets executed, takes the native dependencies of
// the natives configuration, and extracts them to the proper libs/ folders
// so they get packed with the APK.
tasks.register('copyAndroidNatives') {
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
tasks.matching { it.name.contains("merge") && it.name.contains("JniLibFolders") }.configureEach { packageTask ->
  packageTask.dependsOn 'copyAndroidNatives'
}

tasks.register('run', Exec) {
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
      path = "${'$'}System.env.ANDROID_SDK_ROOT"
    }
  } else {
    path = "${'$'}System.env.ANDROID_SDK_ROOT"
  }

  def adb = path + "/platform-tools/adb"
  commandLine "${'$'}adb", 'shell', 'am', 'start', '-n', '${project.basic.rootPackage}/${project.basic.rootPackage}.android.AndroidLauncher'
}

eclipse.project.name = appName + "-android"
"""
  }
}
