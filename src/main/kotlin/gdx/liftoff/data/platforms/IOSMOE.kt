@file:Suppress("ClassName") // iOS starts with a lowercase letter.

package gdx.liftoff.data.platforms

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.project.Project

/**
 * Represents iOS backend.
 */
// @GdxPlatform
class IOSMOE : Platform {
  companion object {
    const val ID = "ios-moe"
    const val ORDER = Shared.ORDER + 1
  }

  override val id = ID
  override val description = "iOS mobile backend using Multi-OS Engine."
  override val order = ORDER
  override val isStandard = false

  override fun createGradleFile(project: Project): GradleFile = IOSMOEGradleFile(project)
  override fun initiate(project: Project) {
    project.rootGradle.buildDependencies.add("\"org.multi-os-engine:moe-gradle:1.9.0\"")
    // Best would be to just copy the "xcode" directory
    arrayOf(
      "app-store-icon-1024@1x.png",
      "Contents.json",
      "ipad-app-icon-76@1x.png",
      "ipad-app-icon-76@2x.png",
      "ipad-notifications-icon-20@1x.png",
      "ipad-notifications-icon-20@2x.png",
      "ipad-pro-app-icon-83.5@2x.png",
      "ipad-settings-icon-29@1x.png",
      "ipad-settings-icon-29@2x.png",
      "ipad-spotlight-icon-40@1x.png",
      "ipad-spotlight-icon-40@2x.png",
      "iphone-app-icon-60@2x.png",
      "iphone-app-icon-60@3x.png",
      "iphone-notification-icon-20@2x.png",
      "iphone-notification-icon-20@3x.png",
      "iphone-spotlight-icon-40@2x.png",
      "iphone-spotlight-icon-40@3x.png",
      "iphone-spotlight-settings-icon-29@2x.png",
      "iphone-spotlight-settings-icon-29@3x.png"
    ).forEach {
      project.files.add(
        CopiedFile(
          projectName = ID,
          path = path("xcode", "ios-moe", "Media.xcassets", "AppIcon.appiconset", it),
          original = path("generator", "ios-moe", "xcode", "ios-moe", "Media.xcassets", "AppIcon.appiconset", it)
        )
      )
    }

    project.files.add(
      CopiedFile(
        projectName = ID,
        path = path("xcode", "ios-moe", "Media.xcassets", "Contents.json"),
        original = path("generator", "ios-moe", "xcode", "ios-moe", "Media.xcassets", "Contents.json")
      )
    )

    arrayOf(
      "Default-1024w-1366h@2x~ipad.png",
      "Default@2x~ipad.png",
      "Default@2x.png",
      "Default-375w-667h@2x.png",
      "Default-375w-812h@3x.png",
      "Default-414w-736h@3x.png",
      "Default-568h@2x.png",
      "Default~ipad.png",
      "Default.png",
      "main.cpp"
    ).forEach {
      project.files.add(
        CopiedFile(
          projectName = ID,
          path = path("xcode", "ios-moe", it),
          original = path("generator", "ios-moe", "xcode", "ios-moe", it)
        )
      )
    }

    project.files.add(
      ReplacedContentFile(
        projectName = ID,
        path = path("xcode", "ios-moe", "Info.plist"),
        original = path("generator", "ios-moe", "xcode", "ios-moe", "Info.plist"),
        replaceMap = mapOf(Pair("%PACKAGE%", project.basic.rootPackage))
      )
    )

    project.files.add(
      ReplacedContentFile(
        projectName = ID,
        path = path("xcode", "ios-moe.xcodeproj", "project.pbxproj"),
        original = path("generator", "ios-moe", "xcode", "ios-moe.xcodeproj", "project.pbxproj"),
        replaceMap = mapOf(
          Pair("%PACKAGE%", project.basic.rootPackage),
          Pair("%ASSET_PATH%", "assets/"),
          Pair("%APP_NAME%", project.basic.name)
        )
      )
    )

    arrayOf("Info.plist", "main.cpp").forEach {
      project.files.add(
        CopiedFile(
          projectName = ID,
          path = path("xcode", "ios-moe-Test", it),
          original = path("generator", "ios-moe", "xcode", "ios-moe-Test", it)
        )
      )
    }
    project.advanced.gradleTasks.add("ios-moe:moeUpdateXcodeSettings")
    project.advanced.gradleTasks.add("ios-moe:copyNatives")
  }
}

class ReplacedContentFile(projectName: String = "", path: String, original: String, fileType: Files.FileType = Files.FileType.Internal, private val replaceMap: Map<String, String>) : CopiedFile(projectName, path, original, fileType) {
  override fun save(destination: FileHandle) {
    var input = Gdx.files.getFileHandle(original, fileType).readString("UTF-8")
    replaceMap.forEach { (t, u) -> input = input.replace(t, u) }
    destination.child(path).writeString(input, false, "UTF-8")
  }
}

class IOSMOEGradleFile(val project: Project) : GradleFile(IOSMOE.ID) {
  init {
    dependencies.add("project(':${Core.ID}')")
    addDependency("io.github.berstanio:gdx-backend-moe:\$gdxVersion")
    addSpecialDependency("natives \"com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-ios\"")
  }

  override fun getContent() = """apply plugin: 'moe'

// Exclude all files from Gradle's test runner
test { exclude '**' }

// Copy all xcframeworks to xcode/native/ios
// They need to be picked up from there for linking in XCode
task copyNatives  {
  doLast {
    file("xcode/native/ios/").mkdirs();
    def subDir = "META-INF/robovm/ios/libs/"
    configurations.natives.files.each { jar ->
      copy {
        from zipTree(jar)
        include "${"$"}subDir/*.xcframework/**"
        into("xcode/native/ios/")
        eachFile { file ->
          file.path = file.path.replaceFirst("^${"$"}subDir", '')
        }
        includeEmptyDirs(false)
      }
    }

    def LD_FLAGS = "LIBGDX_NATIVES = -Wl,-all_load"
    def outFlags = file("xcode/ios-moe/custom.xcconfig");
    outFlags.write LD_FLAGS

    def proguard = file("proguard.append.cfg")
    if (!proguard.exists()) {
      proguard = new File("proguard.append.cfg")
      proguard << "\n-keep class com.badlogic.** { *; }\n"
      proguard << "-keep enum com.badlogic.** { *; }\n"
    }
  }
}

configurations { natives }

dependencies {
${joinDependencies(dependencies)}
}

// Setup Multi-OS Engine
moe {
  xcode {
    project 'xcode/ios-moe.xcodeproj'
    mainTarget 'ios-moe'
    testTarget 'ios-moe-Test'
  }
}

moeMainReleaseIphoneosXcodeBuild.dependsOn copyNatives
moeMainDebugIphoneosXcodeBuild.dependsOn copyNatives
moeMainReleaseIphonesimulatorXcodeBuild.dependsOn copyNatives
moeMainDebugIphonesimulatorXcodeBuild.dependsOn copyNatives

if (System.getenv('PLATFORM_NAME') != null) {
  moeXcodeInternal.dependsOn copyNatives
}
"""
}
