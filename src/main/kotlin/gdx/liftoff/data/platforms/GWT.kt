package gdx.liftoff.data.platforms

import com.badlogic.gdx.Gdx
import gdx.liftoff.data.files.SourceFile
import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents GWT backend.
 */
@GdxPlatform
class GWT : Platform {
  companion object {
    const val ID = "html"
    const val ORDER = IOS.ORDER + 1
    const val BASIC_INHERIT = "com.badlogic.gdx.backends.gdx_backends_gwt"
    val INHERIT_COMPARATOR =
      Comparator<String> { a, b ->
        // Basic GWT inherit has to be first:
        if (a == BASIC_INHERIT) {
          -1
        } else if (b == BASIC_INHERIT) {
          1
        } else {
          a.compareTo(b)
        }
      }
  }

  override val id = ID
  override val description = "Web platform using GWT and WebGL. Supports only Java projects."
  override val order = ORDER
  override val isStandard = false

  override fun createGradleFile(project: Project): GradleFile = GWTGradleFile(project)

  override fun initiate(project: Project) {
    addGradleTaskDescription(
      project,
      "superDev",
      "compiles GWT sources and runs the application in SuperDev mode. It will be available at [localhost:8080/$id](http://localhost:8080/$id). Use only during development.",
    )
    addGradleTaskDescription(
      project,
      "dist",
      "compiles GWT sources. The compiled application can be found at `$id/build/dist`: you can use any HTTP server to deploy it.",
    )

    project.gwtInherits.add(BASIC_INHERIT)
    project.properties["gwtFrameworkVersion"] = project.advanced.gwtVersion
    project.properties["gwtPluginVersion"] = project.advanced.gwtPluginVersion

    // Adding GWT definition to core project:
    project.files.add(
      SourceFile(
        projectName = Core.ID,
        packageName = project.basic.rootPackage,
        fileName = "${project.basic.mainClass}.gwt.xml",
        content = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//Google Inc.//DTD Google Web Toolkit 2.11.0//EN" "https://www.gwtproject.org/doctype/2.11.0/gwt-module.dtd">
<module>
  <!-- Paths to source are relative to this file and separated by slashes ('/'). -->
  <source path="" />

  <!-- Reflection includes may be needed for your code or library code. Each value is separated by periods ('.'). -->
  <!-- You can include a full package by not including the name of a type at the end. -->
${(project.reflectedClasses + project.reflectedPackages).joinToString(
          separator = "\n",
          prefix = "",
        ) { "  <extend-configuration-property name=\"gdx.reflect.include\" value=\"$it\" />" }}
</module>""",
      ),
    )
    project.gwtInherits.add("${project.basic.rootPackage}.${project.basic.mainClass}")

    // Adding GWT definition to shared project:
    if (project.hasPlatform(Shared.ID)) {
      project.files.add(
        SourceFile(
          projectName = Shared.ID,
          packageName = project.basic.rootPackage,
          fileName = "Shared.gwt.xml",
          content = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//Google Inc.//DTD Google Web Toolkit 2.11.0//EN" "https://www.gwtproject.org/doctype/2.11.0/gwt-module.dtd">
<module>
  <!-- Paths to source are relative to this file and separated by slashes ('/'). -->
  <source path="" />

  <!-- Reflection includes may be needed for your code or library code. Each value is separated by periods ('.'). -->
  <!-- You can include a full package by not including the name of a type at the end. -->
  <!-- <extend-configuration-property name="gdx.reflect.include" value="fully.qualified.TypeName" /> -->

  <!-- Rarely, projects may need to include files but do not have access to the complete assets. -->
  <!-- This happens for libraries and shared projects, typically, and the configuration goes in that project. -->
  <!-- You can include individual files like this, and access them with Gdx.files.classpath("path/to/file.png") : -->
  <!-- <extend-configuration-property name="gdx.files.classpath" value="path/to/file.png" /> -->
</module>""",
        ),
      )
      project.gwtInherits.add("${project.basic.rootPackage}.Shared")
    }

    // Adding GWT definition:
    project.files.add(
      SourceFile(
        projectName = ID,
        packageName = project.basic.rootPackage,
        fileName = "GdxDefinition.gwt.xml",
        content = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//Google Inc.//DTD Google Web Toolkit 2.11.0//EN" "https://www.gwtproject.org/doctype/2.11.0/gwt-module.dtd">
<module rename-to="html">
  <!-- Paths to source are relative to this file and separated by slashes ('/'). -->
  <source path="" />

  <!-- Any resources placed under package public_html, relative to this file, will be copied verbatim into the final webapp folder. -->
  <!-- This is where you can place your JavaScript, CSS and other resources for advanced JS integration. -->
  <public path="public_html" />

  <!-- "Inherits" lines are how GWT knows where to look for code and configuration in other projects or libraries. -->
${project.gwtInherits.sortedWith(INHERIT_COMPARATOR).joinToString(separator = "\n") { "  <inherits name=\"$it\" />" }}

  <!-- You must change this if you rename packages later, or rename GwtLauncher. -->
  <entry-point class="${project.basic.rootPackage}.gwt.GwtLauncher" />

  <!-- Reflection includes may be needed for your code or library code. Each value is separated by periods ('.'). -->
  <!-- You can include a full package by not including the name of a type at the end. -->
  <!-- This is a feature of libGDX, so these lines go after the above "inherits" that brings in libGDX. -->
  <!-- <extend-configuration-property name="gdx.reflect.include" value="fully.qualified.TypeName" /> -->

  <!-- Rarely, projects may need to include files but do not have access to the complete assets. -->
  <!-- This happens for libraries and shared projects, typically, and the configuration goes in that project. -->
  <!-- The value is a path, separated by forward slashes, where the root is your html project's resources root. -->
  <!-- You can include individual files like this, and access them with Gdx.files.classpath("path/to/file.png") : -->
  <!-- This is also a feature of libGDX, so these lines go after the above "inherits" that brings in libGDX. -->
  <!-- <extend-configuration-property name="gdx.files.classpath" value="path/to/file.png" /> -->

  <!-- You usually won't need to make changes to the rest of this. -->
  <set-configuration-property name="gdx.assetpath" value="../assets" />
  <set-configuration-property name="xsiframe.failIfScriptTag" value="FALSE"/>
  <!-- These two lines reduce the work GWT has to do during compilation and also shrink output size. -->
  <set-property name="user.agent" value="gecko1_8, safari"/>
  <collapse-property name="user.agent" values="*" />
  <!-- Remove the "user.agent" lines above if you encounter issues with Safari or other Gecko browsers. -->
</module>""",
      ),
    )

    // Adding SuperDev definition:
    project.files.add(
      SourceFile(
        projectName = ID,
        packageName = project.basic.rootPackage,
        fileName = "GdxDefinitionSuperdev.gwt.xml",
        content = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//Google Inc.//DTD Google Web Toolkit 2.11.0//EN" "https://www.gwtproject.org/doctype/2.11.0/gwt-module.dtd">
<module rename-to="html">
  <inherits name="${project.basic.rootPackage}.GdxDefinition" />
  <collapse-all-properties />
  <add-linker name="xsiframe"/>
  <set-configuration-property name="devModeRedirectEnabled" value="true"/>
  <set-configuration-property name="xsiframe.failIfScriptTag" value="FALSE"/>
</module>""",
      ),
    )

    // Copying webapp files:
    addCopiedFile(project, "webapp", "refresh.png")
    project.files.add(
      SourceFile(
        projectName = id,
        fileName = "index.html",
        sourceFolderPath = "webapp",
        packageName = "",
        content =
          Gdx.files
            .internal(path("generator", id, "webapp", "index.html"))
            .readString("UTF8")
            .replaceFirst("@@libGDX application@@", project.basic.name),
      ),
    )

    addCopiedFile(project, "webapp", "styles.css")
    addCopiedFile(project, "webapp", "WEB-INF", "web.xml")
  }
}

class GWTGradleFile(
  val project: Project,
) : GradleFile(GWT.ID) {
  init {
    buildDependencies.add("project(':${Core.ID}')")
    dependencies.add("project(':${Core.ID}')")

    addDependency("com.badlogicgames.gdx:gdx:\$gdxVersion:sources")
    addDependency("com.badlogicgames.gdx:gdx-backend-gwt:\$gdxVersion")
    addDependency("com.badlogicgames.gdx:gdx-backend-gwt:\$gdxVersion:sources")
    addDependency("com.google.jsinterop:jsinterop-annotations:2.1.0:sources")
  }

  override fun getContent(): String =
    """
buildscript {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  dependencies {
    classpath "org.docstr.gwt:org.docstr.gwt.gradle.plugin:${'$'}gwtPluginVersion"

  }
}
apply plugin: "org.docstr.gwt"
apply plugin: "war"

// This next line can be changed if you want to, for instance, always build into the
// docs/ folder of a Git repo, which can be set to automatically publish on GitHub Pages.
// This is relative to the html/ folder.
var outputPath = "build/dist/"

gwt {
  gwtVersion = "${'$'}gwtFrameworkVersion" // Should match the version used for building the GWT backend. See gradle.properties.
  maxHeapSize = '1G' // Default 256m is not enough for the GWT compiler. GWT is HUNGRY.
  minHeapSize = '1G'

  extraSourceDirs = files(
    file('src/main/java'),
    project(":core").file('src/main/java'),
    files(project(':core').sourceSets.main.allJava.srcDirs),
    files("../core/build/generated/sources/annotationProcessor/java/main"),
    files(sourceSets.main.output.resourcesDir),
${if (project.hasPlatform(Shared.ID)) "    files(project(':shared').sourceSets.main.allJava.srcDirs)" else ""}
  )
  modules = ["${project.basic.rootPackage}.GdxDefinition"]

  //// This affects where the final resulting build will be placed.
  war = file(outputPath)
  //// You
  compiler.optimize = 9
  compiler.strict = true
  //// The next line can be useful to uncomment if you want output that hasn't been obfuscated.
//  compiler.style = "DETAILED"
${if (project.advanced.gwtVersion == "2.10.0" || project.advanced.gwtVersion == "2.11.0") "\n  sourceLevel = '1.11'\n" else ""}
  devMode {
    port = 8080
    startupUrl = "http://127.0.0.1:8080/index.html"
  }
  superDev {
    modules = ["${project.basic.rootPackage}.GdxDefinitionSuperdev"]
  }
}

dependencies {
${joinDependencies(dependencies)}
  constraints {
    implementation "jakarta.servlet:jakarta.servlet-api:6.1.0"
  }
}

//// We delete the (temporary) war/ folder because if any extra files get into it, problems occur.
//// The war/ folder shouldn't be committed to version control.
clean.delete += [file("war")]

//// The compileJava task is run at the start of any GWT build; we need to clean the output directory BEFORE
//// anything can be built into that output directory, so the rest of the build can use it.
tasks.named("compileJava").get().dependsOn("clean")

//// You should always use the dist task (which could be folded into the "other" group), not the build task.
tasks.register('dist') {
  //// This can't depend on the "clean" task because that would remove things it needs.
  //// You may want to run "gradlew html:clean" before builds if you encounter leftovers from previous runs.
  dependsOn(["build"])
  doLast {
    // Uncomment the next line if you have changed outputPath and know that its contents
    // should be replaced by a new dist build. Some large JS files are not cleaned up by
    // default unless the outputPath is inside build/ (then the clean task removes them).
    // Do not uncomment the next line if you changed outputPath to a folder that has
    // any files that you want to keep!
    //delete(file(outputPath))

    file(outputPath).mkdirs()
    copy {
      from("build/gwt/out") {
        exclude '**/*.symbolMap' // Not used by a dist, and these can be large.
      }
      into outputPath
    }
    copy {
      from("webapp") {
        exclude 'index.html' // We edit this HTML file later.
        exclude 'refresh.png' // We don't need this button; this saves some bytes.
      }
      into outputPath
    }
    copy {
      from("webapp") {
        // These next two lines take the index.html page and remove the superdev refresh button.
        include 'index.html'
        filter { String line -> line.replaceAll('<a class="superdev" .+', '') }
        // This does not modify the original index.html, only the copy in the dist.
        // If you decide to manually remove or comment out the superdev button from index.html, you should also
        // either remove or comment out only the "filter" line above this.
      }
      into outputPath
    }
    copy {
      from "war"
      into outputPath
    }
  }
}

tasks.register("distZip", Zip) {
  dependsOn("dist")
  //// This uses the output of the dist task, which removes the superdev button from index.html .
  from(outputPath)
  archiveVersion = projectVersion
  archiveBaseName.set("${'$'}{appName}-dist")
  //// The result will be in html/build/ with a name containing "-dist".
  destinationDirectory.set(file("build"))
}

//// The superDev task, which may be grouped under "gwt", allows updating your sources in your editor and
//// seeing your changes as soon as you refresh in the browser. In the current form of development mode,
//// this depends on dist, which may change in the future, and also pops up a Swing form (by GWT itself)
//// to handle serving the local files and updating when you save changes in your editor.
tasks.register('superDev') {
  dependsOn("dist", "gwtDevMode")
  group("gwt")
}

tasks.compileJava.dependsOn("processResources")

java.sourceCompatibility = ${if (project.advanced.gwtVersion == "2.10.0" || project.advanced.gwtVersion == "2.11.0") "JavaVersion.VERSION_11" else "JavaVersion.VERSION_1_8"}
java.targetCompatibility = ${if (project.advanced.gwtVersion == "2.10.0" || project.advanced.gwtVersion == "2.11.0") "JavaVersion.VERSION_11" else "JavaVersion.VERSION_1_8"}
sourceSets.main.java.srcDirs = [ "src/main/java/" ]

eclipse.project.name = appName + "-html"
""" + (
      if (project.extensions.isSelected("lombok")) {
        """

configurations { lom }
dependencies {
  lom "org.projectlombok:lombok:${'$'}{lombokVersion}"
  implementation configurations.lom.dependencies
  annotationProcessor configurations.lom.dependencies
}

tasks.register('draftCompileGwt') {
  doFirst {
    jvmArgs "-javaagent:${'$'}{configurations.lom.asPath}=ECJ"
  }
}

tasks.register('compileGwt') {
  doFirst {
    jvmArgs "-javaagent:${'$'}{configurations.lom.asPath}=ECJ"
  }
}

superDev {
  doFirst {
    jvmArgs "-javaagent:${'$'}{configurations.lom.asPath}=ECJ"
  }
}
"""
      } else {
        ""
      }
    )
}
