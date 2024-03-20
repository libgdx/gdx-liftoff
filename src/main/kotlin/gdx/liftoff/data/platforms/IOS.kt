@file:Suppress("ClassName") // iOS starts with a lowercase letter.

package gdx.liftoff.data.platforms

import gdx.liftoff.config.GdxVersion
import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.SourceFile
import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents iOS backend.
 */
@GdxPlatform
class IOS : Platform {
  companion object {
    const val ID = "ios"
    const val ORDER = Android.ORDER + 1
  }

  override val id = ID
  override val description = "iOS mobile platform using RoboVM."
  override val order = ORDER
  override val isStandard = false

  override fun createGradleFile(project: Project): GradleFile = IOSGradleFile(project)
  override fun initiate(project: Project) {
    project.properties["robovmVersion"] = project.advanced.robovmVersion

    // Including RoboVM config files:
    project.files.add(
      CopiedFile(
        projectName = ID,
        path = "Info.plist.xml",
        original = path("generator", "ios", "Info.plist.xml")
      )
    )
    project.files.add(
      SourceFile(
        projectName = ID,
        fileName = "robovm.properties",
        content = """app.version=${project.advanced.version.replace("[^0-9\\.]", "")}
app.id=${project.basic.rootPackage}
app.mainclass=${project.basic.rootPackage}.ios.IOSLauncher
app.executable=IOSLauncher
app.build=1
app.name=${project.basic.name}"""
      )
    )
    project.files.add(
      SourceFile(
        projectName = ID,
        fileName = "robovm.xml",
        content = """<config>
  <executableName>${'$'}{app.executable}</executableName>
  <mainClass>${'$'}{app.mainclass}</mainClass>
  <os>ios</os>
  <target>ios</target>
  <iosInfoPList>Info.plist.xml</iosInfoPList>
  <treeShaker>conservative</treeShaker>
  <resources>
    <resource>
      <directory>../assets</directory>
      <includes>
        <include>**</include>
      </includes>
      <skipPngCrush>true</skipPngCrush>
    </resource>
    <resource>
      <directory>data</directory>
    </resource>
  </resources>
  <forceLinkClasses>
    <pattern>com.badlogic.gdx.scenes.scene2d.ui.*</pattern>
    <pattern>com.badlogic.gdx.graphics.g3d.particles.**</pattern>
    <pattern>com.android.okhttp.HttpHandler</pattern>
    <pattern>com.android.okhttp.HttpsHandler</pattern>
    <pattern>com.android.org.conscrypt.**</pattern>
    <pattern>com.android.org.bouncycastle.jce.provider.BouncyCastleProvider</pattern>
    <pattern>com.android.org.bouncycastle.jcajce.provider.keystore.BC${'$'}Mappings</pattern>
    <pattern>com.android.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi</pattern>
    <pattern>com.android.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi${'$'}Std</pattern>
    <pattern>com.android.org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi</pattern>
    <pattern>com.android.org.bouncycastle.crypto.digests.AndroidDigestFactoryOpenSSL</pattern>
    <pattern>org.apache.harmony.security.provider.cert.DRLCertFactory</pattern>
    <pattern>org.apache.harmony.security.provider.crypto.CryptoProvider</pattern>
${if (project.extensions.officialExtensions.any { it.id == "gdx-controllers" }) "    <pattern>com.badlogic.gdx.controllers.IosControllerManager</pattern>" else ""}
  </forceLinkClasses>
  <libs>
      <lib>z</lib>
  </libs>
  <frameworks>
    <framework>UIKit</framework>
${if (GdxVersion.parseGdxVersion(project.advanced.gdxVersion) != null && GdxVersion.parseGdxVersion(project.advanced.gdxVersion)!! < GdxVersion(1, 12, 0)) "    <framework>OpenGLES</framework>" else ""}
    <framework>QuartzCore</framework>
    <framework>CoreGraphics</framework>
    <framework>OpenAL</framework>
    <framework>AudioToolbox</framework>
    <framework>AVFoundation</framework>
    <framework>GameController</framework>
${if (project.extensions.officialExtensions.any { it.id == "gdx-controllers" }) "    <framework>GameKit</framework>" else ""}
  </frameworks>
</config>"""
      )
    )

    project.files.add(
      CopiedFile(
        projectName = ID,
        path = path("data", "Media.xcassets", "Contents.json"),
        original = path("generator", "ios", "data", "Media.xcassets", "Contents.json")
      )
    )
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
          path = path("data", "Media.xcassets", "AppIcon.appiconset", it),
          original = path("generator", "ios", "data", "Media.xcassets", "AppIcon.appiconset", it)
        )
      )
    }
    arrayOf(
      "Contents.json",
      "libgdx@1x.png",
      "libgdx@2x.png",
      "libgdx@3x.png"
    ).forEach {
      project.files.add(
        CopiedFile(
          projectName = ID,
          path = path("data", "Media.xcassets", "Logo.imageset", it),
          original = path("generator", "ios", "data", "Media.xcassets", "Logo.imageset", it)
        )
      )
    }
    project.files.add(
      CopiedFile(
        projectName = ID,
        path = path("data", "Base.lproj", "LaunchScreen.storyboard"),
        original = path("generator", "ios", "data", "Base.lproj", "LaunchScreen.storyboard")
      )
    )
    project.files.add(
      CopiedFile(
        projectName = ID,
        path = path("data", "PrivacyInfo.xcprivacy"),
        original = path("generator", "ios", "data", "PrivacyInfo.xcprivacy")
      )
    )

    // Including reflected classes:
    if (project.reflectedClasses.isNotEmpty() || project.reflectedPackages.isNotEmpty()) {
      project.files.add(
        SourceFile(
          projectName = ID,
          sourceFolderPath = path("src", "main", "resources"),
          packageName = "META-INF.robovm.ios",
          fileName = "robovm.xml",
          content = """<config>
  <forceLinkClasses>
${project.reflectedPackages.joinToString(separator = "\n") { "    <pattern>$it.**</pattern>" }}
${project.reflectedClasses.joinToString(separator = "\n") { "    <pattern>$it</pattern>" }}
  </forceLinkClasses>
</config>"""
        )
      )
    }
  }
}

class IOSGradleFile(val project: Project) : GradleFile(IOS.ID) {
  init {
    dependencies.add("project(':${Core.ID}')")
    addDependency("com.mobidevelop.robovm:robovm-rt:\$robovmVersion")
    addDependency("com.mobidevelop.robovm:robovm-cocoatouch:\$robovmVersion")
    addDependency("com.badlogicgames.gdx:gdx-backend-robovm:\$gdxVersion")
    addDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-ios")
  }

  override fun getContent() = """buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath "com.mobidevelop.robovm:robovm-gradle-plugin:${'$'}robovmVersion"
  }
}
apply plugin: 'robovm'

[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'

ext {
  mainClassName = "${project.basic.rootPackage}.ios.IOSLauncher"
}

launchIPhoneSimulator.dependsOn build
launchIPadSimulator.dependsOn build
launchIOSDevice.dependsOn build
createIPA.dependsOn build

eclipse.project {
  name = appName + "-ios"
  natures 'org.robovm.eclipse.RoboVMNature'
}

dependencies {
${joinDependencies(dependencies)}}
"""
}
