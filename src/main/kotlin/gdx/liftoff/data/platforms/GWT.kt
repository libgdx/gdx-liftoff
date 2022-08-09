package gdx.liftoff.data.platforms

import gdx.liftoff.config.GdxVersion
import gdx.liftoff.data.files.CopiedFile
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
        const val ORDER = iOS.ORDER + 1
        const val BASIC_INHERIT = "com.badlogic.gdx.backends.gdx_backends_gwt"
        val INHERIT_COMPARATOR = Comparator<String> { a, b ->
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
    override val order = ORDER
    override val isStandard = false

    override fun createGradleFile(project: Project): GradleFile = GWTGradleFile(project)

    override fun initiate(project: Project) {
        project.rootGradle.buildDependencies.add("\"org.wisepersist:gwt-gradle-plugin:\$gwtPluginVersion\"")

        addGradleTaskDescription(project, "superDev", "compiles GWT sources and runs the application in SuperDev mode. It will be available at [localhost:8080/$id](http://localhost:8080/$id). Use only during development.")
        addGradleTaskDescription(project, "dist", "compiles GWT sources. The compiled application can be found at `$id/build/dist`: you can use any HTTP server to deploy it.")

        project.gwtInherits.add(BASIC_INHERIT)
        project.properties["gwtFrameworkVersion"] = project.advanced.gwtVersion
        project.properties["gwtPluginVersion"] = project.advanced.gwtPluginVersion

        // Adding GWT definition to core project:
        project.files.add(
            SourceFile(
                projectName = Core.ID, packageName = project.basic.rootPackage,
                fileName = "${project.basic.mainClass}.gwt.xml",
                content = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//Google Inc.//DTD Google Web Toolkit ${project.advanced.gwtVersion}//EN" "http://www.gwtproject.org/doctype/${project.advanced.gwtVersion}/gwt-module.dtd">
<module>
    <source path="" />${(project.reflectedClasses + project.reflectedPackages).joinToString(separator = "\n", prefix = "\n") { "    <extend-configuration-property name=\"gdx.reflect.include\" value=\"$it\" />" }}
</module>"""
            )
        )
        project.gwtInherits.add("${project.basic.rootPackage}.${project.basic.mainClass}")

        // Adding GWT definition to shared project:
        if (project.hasPlatform(Shared.ID)) {
            project.files.add(
                SourceFile(
                    projectName = Shared.ID, packageName = project.basic.rootPackage,
                    fileName = "Shared.gwt.xml",
                    content = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//Google Inc.//DTD Google Web Toolkit ${project.advanced.gwtVersion}//EN" "http://www.gwtproject.org/doctype/${project.advanced.gwtVersion}/gwt-module.dtd">
<module>
    <source path="" />
</module>"""
                )
            )
            project.gwtInherits.add("${project.basic.rootPackage}.Shared")
        }

        // Adding GWT definition:
        project.files.add(
            SourceFile(
                projectName = ID, packageName = project.basic.rootPackage,
                fileName = "GdxDefinition.gwt.xml",
                content = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//Google Inc.//DTD Google Web Toolkit ${project.advanced.gwtVersion}//EN" "http://www.gwtproject.org/doctype/${project.advanced.gwtVersion}/gwt-module.dtd">
<module rename-to="html">
    <source path="" />
${project.gwtInherits.sortedWith(INHERIT_COMPARATOR).joinToString(separator = "\n") { "\t<inherits name=\"$it\" />" }}
    <entry-point class="${project.basic.rootPackage}.gwt.GwtLauncher" />
    <set-configuration-property name="gdx.assetpath" value="../assets" />
    <set-configuration-property name="xsiframe.failIfScriptTag" value="FALSE"/>
    <!-- These two lines reduce the work GWT has to do during compilation and also shrink output size. -->
    <set-property name="user.agent" value="gecko1_8, safari"/>
    <collapse-property name="user.agent" values="*" />
    <!-- Remove the "user.agent" lines above if you encounter issues with Safari or other Gecko browsers. -->
</module>"""
            )
        )

        // Adding SuperDev definition:
        project.files.add(
            SourceFile(
                projectName = ID, packageName = project.basic.rootPackage,
                fileName = "GdxDefinitionSuperdev.gwt.xml",
                content = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//Google Inc.//DTD Google Web Toolkit ${project.advanced.gwtVersion}//EN" "http://www.gwtproject.org/doctype/${project.advanced.gwtVersion}/gwt-module.dtd">
<module rename-to="html">
    <inherits name="${project.basic.rootPackage}.GdxDefinition" />
    <collapse-all-properties />
    <add-linker name="xsiframe"/>
    <set-configuration-property name="devModeRedirectEnabled" value="true"/>
    <set-configuration-property name="xsiframe.failIfScriptTag" value="FALSE"/>
</module>"""
            )
        )

        // Copying webapp files:
        addCopiedFile(project, "webapp", "refresh.png")
        val version = GdxVersion.parseGdxVersion(project.advanced.gdxVersion)
        if (version != null && version < GdxVersion(major = 1, minor = 9, revision = 12)) {
            addCopiedFile(project, "webapp", "soundmanager2-setup.js")
            project.files.add(
                CopiedFile(
                    projectName = id,
                    original = path("generator", id, "webapp", "index_old.html"),
                    path = path("webapp", "index.html")
                )
            )
        } else
            addCopiedFile(project, "webapp", "index.html")
        addSoundManagerSource(project)
        addCopiedFile(project, "webapp", "styles.css")
        addCopiedFile(project, "webapp", "WEB-INF", "web.xml")
    }

    private fun addSoundManagerSource(project: Project) {
        val version = GdxVersion.parseGdxVersion(project.advanced.gdxVersion)
        val soundManagerSource = when {
            // Invalid, user-entered libGDX version - defaulting to current lack of SoundManager:
            version == null -> ""
            // Pre-1.9.6: using old SoundManager sources:
            version < GdxVersion(major = 1, minor = 9, revision = 6) -> "soundmanager2-jsmin_old.js"
            // Recent libGDX version - using latest SoundManager:
            version < GdxVersion(major = 1, minor = 9, revision = 12) -> "soundmanager2-jsmin.js"
            // after 1.9.11, soundmanager is no longer used
            else -> ""
        }
        if (soundManagerSource.isNotEmpty())
            project.files.add(
                CopiedFile(
                    projectName = id,
                    original = path("generator", id, "webapp", soundManagerSource),
                    path = path("webapp", "soundmanager2-jsmin.js")
                )
            )
    }
}

class GWTGradleFile(val project: Project) : GradleFile(GWT.ID) {
    init {
        buildDependencies.add("project(':${Core.ID}')")
        dependencies.add("project(':${Core.ID}')")

        addDependency("com.badlogicgames.gdx:gdx:\$gdxVersion:sources")
        addDependency("com.badlogicgames.gdx:gdx-backend-gwt:\$gdxVersion")
        addDependency("com.badlogicgames.gdx:gdx-backend-gwt:\$gdxVersion:sources")
    }

    override fun getContent(): String = """
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.gretty:gretty:3.0.7"
    }
}
apply plugin: "gwt"
apply plugin: "war"
apply plugin: "org.gretty"

gwt {
    gwtVersion = "${'$'}gwtFrameworkVersion" // Should match the version used for building the GWT backend. See gradle.properties.
    maxHeapSize = '1G' // Default 256m is not enough for the GWT compiler. GWT is HUNGRY.
    minHeapSize = '1G'

    src = files(file('src/main/java')) // Needs to be in front of "modules" below.
    modules '${project.basic.rootPackage}.GdxDefinition'
    devModules '${project.basic.rootPackage}.GdxDefinitionSuperdev'
    project.webAppDirName = 'webapp'

    compiler.strict = true
    compiler.disableCastChecking = true
    //// The next line can be useful to uncomment if you want output that hasn't been obfuscated.
//	compiler.style = org.wisepersist.gradle.plugins.gwt.Style.DETAILED
}

dependencies {
${joinDependencies(dependencies)}
//// You can use the lines below instead of the "com.badlogicgames.gdx:gdx-backend-gwt" dependencies.
//// If you do, follow the steps at https://github.com/tommyettinger/gdx-backends#gwt-290-support
//// and you can use GWT 2.9.0, which gives you access to Java 11 language features.
//// These releases use libGDX 1.10.0, and are not compatible with other versions.
//	implementation "com.github.tommyettinger:gdx-backend-gwt:1.100.1"
//	implementation "com.github.tommyettinger:gdx-backend-gwt:1.100.1:sources"
}

import org.akhikhl.gretty.AppBeforeIntegrationTestTask
import org.wisepersist.gradle.plugins.gwt.GwtSuperDev

gretty.httpPort = 8080
gretty.resourceBase = project.buildDir.path + "/gwt/draftOut"
gretty.contextPath = "/"
gretty.portPropertiesFileName = "TEMP_PORTS.properties"

task startHttpServer (dependsOn: [draftCompileGwt]) {
    doFirst {
        copy {
            from "webapp"
            into gretty.resourceBase
        }
        copy {
            from "war"
            into gretty.resourceBase
        }
    }
}
task beforeRun(type: AppBeforeIntegrationTestTask, dependsOn: startHttpServer) {
    // The next line allows ports to be reused instead of
    // needing a process to be manually terminated.
    file("build/TEMP_PORTS.properties").delete()
    // Somewhat of a hack; uses Gretty's support for wrapping a task in
    // a start and then stop of a Jetty server that serves files while
    // also running the SuperDev code server.
    integrationTestTask 'superDev'

    interactive false
}

task superDev(type: GwtSuperDev) {
    doFirst {
        gwt.modules = gwt.devModules
    }
}

//// We delete the (temporary) war/ folder because if any extra files get into it, problems occur.
//// The war/ folder shouldn't be committed to version control.
clean.delete += [file("war")]

// This next line can be changed if you want to, for instance, always build into the
// docs/ folder of a Git repo, which can be set to automatically publish on GitHub Pages.
// This is relative to the html/ folder.
var outputPath = "build/dist/"

task dist(dependsOn: [clean, compileGwt]) {
    doLast {
        // Uncomment the next line if you have changed outputPath and know that its contents
        // should be replaced by a new dist build. Some large JS files are not cleaned up by
        // default unless the outputPath is inside build/ (then the clean task removes them).
        // Do not uncomment the next line if you changed outputPath to a folder that has
        // non-generated files that you want to keep!
        //delete(file(outputPath))

        file(outputPath).mkdirs()
        copy {
            from("build/gwt/out"){
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

task addSource {
    doLast {
        sourceSets.main.compileClasspath += files(project(':core').sourceSets.main.allJava.srcDirs)
        ${if (project.hasPlatform(Shared.ID)) "sourceSets.main.compileClasspath += files(project(':shared').sourceSets.main.allJava.srcDirs)" else ""}
    }
}

task distZip(type: Zip, dependsOn: dist){
    //// This uses the output of the dist task, which removes the superdev button from index.html .
    from(outputPath)
    archiveBaseName.set("${'$'}{appName}-dist")
    //// The result will be in html/build/ with a name containing "-dist".
    destinationDir(file("build"))
}

tasks.compileGwt.dependsOn(addSource)
tasks.draftCompileGwt.dependsOn(addSource)
tasks.checkGwt.dependsOn(addSource)

// You can change the version below to JavaVersion.VERSION_11 if you use the 2.9.0 backend.
sourceCompatibility = JavaVersion.VERSION_1_8
sourceSets.main.java.srcDirs = [ "src/main/java/" ]

eclipse.project.name = appName + "-html"
""" + (
        if (project.extensions.hasExtensionSelected("lombok")) """

configurations { lom }
dependencies {
    lom "org.projectlombok:lombok:${'$'}{lombokVersion}"
    implementation configurations.lom.dependencies
    annotationProcessor configurations.lom.dependencies
}

draftCompileGwt {
    doFirst {
        jvmArgs "-javaagent:${'$'}{configurations.lom.asPath}=ECJ"
    }
}

compileGwt {
    doFirst {
        jvmArgs "-javaagent:${'$'}{configurations.lom.asPath}=ECJ"
    }
}

superDev {
    doFirst {
        jvmArgs "-javaagent:${'$'}{configurations.lom.asPath}=ECJ"
    }
}
""" else ""
        )
}
