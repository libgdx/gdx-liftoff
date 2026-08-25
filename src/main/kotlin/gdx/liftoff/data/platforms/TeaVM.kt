package gdx.liftoff.data.platforms

import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.project.Project
import gdx.liftoff.views.GdxPlatform

/**
 * Represents the unofficial TeaVM web backend created by xpenatan.
 */
@GdxPlatform
class TeaVM : Platform {
  companion object {
    const val ID = "teavm"
    const val ORDER = Headless.ORDER + 1
  }

  override val id = ID
  override val description = "Web backend that supports most JVM languages."
  override val order = ORDER
  override val isStandard = false

  override fun createGradleFile(project: Project) = TeaVMGradleFile(project)

  override fun initiate(project: Project) {
    project.properties["gdxTeaVMVersion"] = project.advanced.gdxTeaVMVersion
    addGradleTaskDescription(
      project,
      "run",
      "serves the JavaScript application at http://localhost:8080 via a local Jetty server.",
    )
    addGradleTaskDescription(
      project,
      "build",
      "builds the JavaScript application into the build/dist/webapp folder.",
    )
  }
}

class TeaVMGradleFile(
  val project: Project,
) : GradleFile(TeaVM.ID) {
  init {
    dependencies.add("project(':${Core.ID}')")

    addDependency("com.github.xpenatan.gdx-teavm:backend-web:\$gdxTeaVMVersion")
  }

  fun generateTeaVMReflectionIncludes(
    indent: String = " ".repeat(2),
  ): String = if (project.reflectedPackages.isEmpty() && project.reflectedClasses.isEmpty()) {
    "$indent//reflection.add(\"${project.basic.rootPackage}.reflect\")"
  } else {
    (project.reflectedPackages + project.reflectedClasses).joinToString(separator = "\n") {
      "${indent}reflection.add(\"$it\")"
    }
  }

  override fun getContent() =
    $$"""import org.teavm.gradle.api.OptimizationLevel

plugins {
  id 'java'
  id("com.github.xpenatan.gdx-teavm") version "$gdxTeaVMVersion"
}

eclipse.project.name = appName + "-teavm"

// This must be at least 17, and no higher than the JDK version this project is built with.
java.targetCompatibility = "$${17.coerceAtLeast(project.advanced.javaVersion.toInt())}"
// This should probably be equal to targetCompatibility, above. This only affects the TeaVM module.
java.sourceCompatibility = "$${17.coerceAtLeast(project.advanced.javaVersion.toInt())}"

dependencies {
$${joinDependencies(dependencies)}
}

/// The tasks set up for debugging are gdx_teavm_web_js_build and gdx_teavm_web_js_run .
/// These auto-build and reload changed sources; they don't obfuscate their output.
/// The tasks for releases are gdx_teavm_web_js_release_build, gdx_teavm_web_js_release_run,
/// gdx_teavm_web_wasm_release_build, and gdx_teavm_web_wasm_release_run .
/// The build tasks will place a build in build/dist/js/release/webapp or build/dist/wasm/release/webapp .
/// The run tasks (including debug) will provide a link to click in the build output.
/// The run tasks won't end on their own; you can open the link multiple times until you stop the build.

gdxTeaVM {
  assets.from(rootProject.files('assets'))

  /// You can add additional classes or packages that need reflection here.
$${generateTeaVMReflectionIncludes()}

  webDefaults {
    mainClass.set("$${project.basic.rootPackage}.teavm.TeaVMLauncher")
    htmlTitle.set(appName)
  }

  js {
    devServer {
      enabled.set(true)
      autoBuild.set(true)
      autoReload.set(true)
      optimization.set(OptimizationLevel.NONE)
    }
  }

  js("release") {
    obfuscated.set(true)
    serverPort.set(8180)
    optimization.set(OptimizationLevel.BALANCED)
  }

  wasm("release") {
    obfuscated.set(true)
    serverPort.set(8181)
    optimization.set(OptimizationLevel.BALANCED)
  }
}

// For backwards compatibility with the earlier run and build tasks.
tasks.register("run"){
  dependsOn("gdx_teavm_web_wasm_release_run")
}
build.dependsOn("gdx_teavm_web_wasm_release_build")
"""
}
