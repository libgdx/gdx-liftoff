package com.github.czyzby.setup.data.project

import com.badlogic.gdx.Files
import com.badlogic.gdx.utils.GdxRuntimeException
import com.github.czyzby.setup.data.files.*
import com.github.czyzby.setup.data.gradle.GradleFile
import com.github.czyzby.setup.data.gradle.RootGradleFile
import com.github.czyzby.setup.data.langs.Java
import com.github.czyzby.setup.data.libs.unofficial.USL
import com.github.czyzby.setup.data.platforms.Android
import com.github.czyzby.setup.data.platforms.Assets
import com.github.czyzby.setup.data.platforms.Platform
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.AdvancedData
import com.github.czyzby.setup.views.BasicProjectData
import com.github.czyzby.setup.views.ExtensionsData
import com.github.czyzby.setup.views.LanguagesData
import com.kotcrab.vis.ui.util.OsUtils
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Contains data about the generated project.
 * @author MJ
 */
class Project(val basic: BasicProjectData, val platforms: Map<String, Platform>, val advanced: AdvancedData,
              val languages: LanguagesData, val extensions: ExtensionsData, val template: Template) {
    private val gradleFiles: Map<String, GradleFile>
    val files = mutableListOf<ProjectFile>()
    val rootGradle: RootGradleFile
    val properties = mutableMapOf(
            "org.gradle.daemon" to "false",
            "org.gradle.jvmargs" to "-Xms128m -Xmx512m",
            "org.gradle.configureondemand" to "false")
    val postGenerationTasks = mutableListOf<(Project) -> Unit>()
    val gwtInherits = mutableSetOf<String>()
    val androidPermissions = mutableSetOf<String>()

    val reflectedClasses = mutableSetOf<String>()
    val reflectedPackages = mutableSetOf<String>()

    // README.md:
    var readmeDescription = ""
    val gradleTaskDescriptions = mutableMapOf<String, String>()

    init {
        gradleFiles = mutableMapOf<String, GradleFile>()
        rootGradle = RootGradleFile(this)
        platforms.forEach { gradleFiles[it.key] = it.value.createGradleFile(this) }
        addBasicGradleTasksDescriptions()
    }

    private fun addBasicGradleTasksDescriptions() {
        if (advanced.generateReadme) {
            arrayOf("idea" to "generates IntelliJ project data.",
                    "cleanIdea" to "removes IntelliJ project data.",
                    "eclipse" to "generates Eclipse project data.",
                    "cleanEclipse" to "removes Eclipse project data.",
                    "clean" to "removes `build` folders, which store compiled classes and built archives.",
                    "test" to "runs unit tests (if any).",
                    "build" to "builds sources and archives of every project.",
                    "--daemon" to "thanks to this flag, Gradle daemon will be used to run chosen tasks.",
                    "--offline" to "when using this flag, cached dependency archives will be used.",
                    "--continue" to "when using this flag, errors will not stop the tasks from running.",
                    "--refresh-dependencies" to "this flag forces validation of all dependencies. Useful for snapshot versions.")
                    .forEach { gradleTaskDescriptions[it.first] = it.second }
        }
    }

    fun hasPlatform(id: String): Boolean = platforms.containsKey(id)

    fun getGradleFile(id: String): GradleFile = gradleFiles.get(id)!!

    fun addGradleTaskDescription(task: String, description: String) {
        if (advanced.generateReadme) {
            gradleTaskDescriptions[task] = description
        }
    }

    fun generate() {
        addBasicFiles()
        addJvmLanguagesSupport()
        addExtensions()
        template.apply(this)
        addPlatforms()
        
        addSkinAssets()
        addReadmeFile()
        saveProperties()
        saveFiles()
        // Invoking post-generation tasks:
        postGenerationTasks.forEach { it(this) }
    }

    private fun addBasicFiles() {
        // Adding global assets folder:
        files.add(SourceDirectory(Assets.ID, ""))
        // Adding .gitignore:
        files.add(CopiedFile(path = ".gitignore", original = path("generator", "gitignore")))
    }

    private fun addJvmLanguagesSupport() {
        Java().initiate(this) // Java is supported by default.
        languages.getSelectedLanguages().forEach {
            it.initiate(this)
            properties[it.id + "Version"] = languages.getVersion(it.id)
        }
        languages.appendSelectedLanguagesVersions(this)
    }

    private fun addExtensions() {
        extensions.getSelectedOfficialExtensions().forEach { it.initiate(this) }
        extensions.getSelectedThirdPartyExtensions().forEach { it.initiate(this) }
    }

    private fun addPlatforms() {
        platforms.values.forEach { it.initiate(this) }
        SettingsFile(platforms.values).save(basic.destination)
    }

    private fun saveFiles() {
        rootGradle.save(basic.destination)
        gradleFiles.values.forEach { it.save(basic.destination) }
        files.forEach { it.save(basic.destination) }
    }

    private fun saveProperties() {
        // Adding LibGDX version property:
        properties["gdxVersion"] = advanced.gdxVersion
        PropertiesFile(properties).save(basic.destination)
    }

    private fun addSkinAssets() {
        if (advanced.generateSkin || advanced.generateUsl) {
            // Adding raw assets directory:
            files.add(SourceDirectory("raw", "ui"))
            // Adding GUI assets directory:
            files.add(SourceDirectory(Assets.ID, "ui"))
        }

        if (advanced.generateSkin) {
            // Adding JSON only if USL is not checked
            if (!advanced.generateUsl) {
                files.add(CopiedFile(projectName = Assets.ID, path = path("ui", "skin.json"),
                        original = path("generator", "assets", "ui", "skin.json")))
            }
            // Android does not support classpath fonts loading through skins.
            // Explicitly copying Arial font if Android platform is included:
            if (hasPlatform(Android.ID)) {
                arrayOf("png", "fnt").forEach {
                    val path = path("com", "badlogic", "gdx", "utils", "arial-15.$it")
                    files.add(CopiedFile(projectName = Assets.ID, path = path, original = path,
                            fileType = Files.FileType.Classpath))
                }
            }

            // README task description:
            gradleTaskDescriptions["pack"] = "packs GUI assets from `raw/ui`. Saves the atlas file at `assets/ui`."

            // Copying raw assets - internal files listing doesn't work, so we're hard-coding raw/ui content:
            arrayOf("check.png", "check-on.png", "dot.png", "knob-h.png", "knob-v.png", "line-h.png", "line-v.png",
                    "pack.json", "rect.png", "select.9.png", "square.png", "tree-minus.png", "tree-plus.png",
                    "window-border.9.png", "window-resize.9.png").forEach {
                files.add(CopiedFile(projectName = "raw", path = "ui${File.separator}$it",
                        original = path("generator", "raw", "ui", it)))
            }

            // Appending "pack" task to root Gradle:
            postGenerationTasks.add({
                basic.destination.child(rootGradle.path).writeString("""
// Run `gradle pack` task to generate skin.atlas file at assets/ui.
import com.badlogic.gdx.tools.texturepacker.TexturePacker
task pack {
	doLast {
		com.badlogic.gdx.tools.texturepacker.TexturePacker.process(
						file("raw").absolutePath,
						file("src/main/resources/skin").absolutePath,
						"tinted"
		)
	}
}
""", true, "UTF-8");
            })
        }

        if (advanced.generateUsl) {
            // Make sure USL extension is added
            USL().initiate(this)
            // Copy USL file:
            files.add(CopiedFile(projectName = "raw", path = path("ui", "skin.usl"),
                    original = path("generator", "raw", "ui", "skin.usl")))

            gradleTaskDescriptions["compileSkin"] = "compiles USL skin from `raw/ui`. Saves the result json file at `assets/ui`."

            // Add "compileSkin" task to root Gradle file:
            postGenerationTasks.add({
                basic.destination.child(rootGradle.path).writeString("""
// Run `gradle compileSkin` task to generate skin.json at assets/ui.
task compileSkin {
	doLast {
		// Convert USL skin file into JSON
		String[] uslArgs = [
			projectDir.path + '/raw/ui/skin.usl',		 // Input USL file
			projectDir.path + '/assets/ui/skin.json'	// Output JSON file
		]
		com.kotcrab.vis.usl.Main.main(uslArgs)
	}
}""", true, "UTF-8");
            })
        }

        if (advanced.generateSkin && advanced.generateUsl) {
            gradleTaskDescriptions["packAndCompileSkin"] = "pack GUI assets and compiles USL skin from `raw/ui`. Saves the result files at `assets/ui`."

            // Add "packAndCompileSkin" task to root Gradle file:
            postGenerationTasks.add({
                basic.destination.child(rootGradle.path).writeString("""
// Run `gradle packAndCompileSkin` to generate skin atlas and compile USL into JSON
task packAndCompileSkin(dependsOn: [pack, compileSkin])""", true, "UTF-8");
            })
        }
    }

    private fun addReadmeFile() {
        if (advanced.generateReadme) {
            files.add(SourceFile(projectName = "", fileName = "README.md", content = """# ${basic.name}

A [LibGDX](http://libgdx.badlogicgames.com/) project generated with [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff).

${readmeDescription}

## Gradle

This project uses [Gradle](http://gradle.org/) to manage dependencies. ${if (advanced.addGradleWrapper)
                "Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands."
            else
                "Gradle wrapper was not included by default, so you have to install Gradle locally."} Useful Gradle tasks and flags:

${gradleTaskDescriptions.map { "- `${it.key}`: ${it.value}" }.sorted().joinToString(separator = "\n")}

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project."""))
        }
    }

    fun includeGradleWrapper(logger: ProjectLogger) {
        if (advanced.addGradleWrapper) {
            arrayOf("gradlew", "gradlew.bat", path("gradle", "wrapper", "gradle-wrapper.jar"),
                    path("gradle", "wrapper", "gradle-wrapper.properties")).forEach {
                CopiedFile(path = it, original = path("generator", it)).save(basic.destination)
            }
            basic.destination.child("gradlew").file().setExecutable(true)
            basic.destination.child("gradlew.bat").file().setExecutable(true)
            logger.logNls("copyGradle")
        }
        val gradleTasks = advanced.gradleTasks
        if (gradleTasks.isNotEmpty()) {
            logger.logNls("runningGradleTasks")
            val commands = determineGradleCommand() + advanced.gradleTasks
            logger.log(commands.joinToString(separator = " "))
            val process = ProcessBuilder(*commands).directory(basic.destination.file())
                    .redirectErrorStream(true).start()
            val stream = BufferedReader(InputStreamReader(process.inputStream))
            var line = stream.readLine();
            while (line != null) {
                logger.log(line)
                line = stream.readLine();
            }
            process.waitFor()
            if (process.exitValue() != 0) {
                throw GdxRuntimeException("Gradle process ended with non-zero value.")
            }
        }
    }

    private fun determineGradleCommand(): Array<String> {
        return if (OsUtils.isWindows()) {
            arrayOf("cmd", "/c", if (advanced.addGradleWrapper) "gradlew" else "gradle")
        } else {
            arrayOf(if (advanced.addGradleWrapper) "./gradlew" else "gradle")
        }
    }
}

interface ProjectLogger {
    fun log(message: String)
    fun logNls(bundleLine: String)
}
