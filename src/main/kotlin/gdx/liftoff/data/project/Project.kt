package gdx.liftoff.data.project

import com.badlogic.gdx.Files
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import com.badlogic.gdx.utils.GdxRuntimeException
import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.ProjectFile
import gdx.liftoff.data.files.PropertiesFile
import gdx.liftoff.data.files.SettingsFile
import gdx.liftoff.data.files.SourceDirectory
import gdx.liftoff.data.files.SourceFile
import gdx.liftoff.data.files.gradle.GradleFile
import gdx.liftoff.data.files.gradle.RootGradleFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.languages.Java
import gdx.liftoff.data.platforms.Android
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.platforms.GWT
import gdx.liftoff.data.platforms.Platform
import gdx.liftoff.data.platforms.TeaVM
import gdx.liftoff.data.templates.Template
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Contains data about the generated project.
 */
class Project(
  val basic: BasicProjectData,
  val platforms: Map<String, Platform>,
  val advanced: AdvancedProjectData,
  val languages: LanguagesData,
  val extensions: ExtensionsData,
  val template: Template,
) {
  private val gradleFiles: Map<String, GradleFile>
  val files = mutableListOf<ProjectFile>()
  val rootGradle: RootGradleFile
  val properties = mutableMapOf<String, String>()
  val gwtInherits = mutableSetOf<String>()
  val androidPermissions = mutableSetOf<String>()

  val reflectedClasses = mutableSetOf<String>()
  val reflectedPackages = mutableSetOf<String>()

  // README.md:
  var readmeDescription = ""
  private val gradleTaskDescriptions = mutableMapOf<String, String>()

  init {
    gradleFiles = mutableMapOf()
    rootGradle = RootGradleFile(this)
    platforms.forEach { gradleFiles[it.key] = it.value.createGradleFile(this) }
    addBasicGradleTasksDescriptions()
  }

  private fun addBasicGradleTasksDescriptions() {
    if (advanced.generateReadme) {
      arrayOf(
        "idea" to "generates IntelliJ project data.",
        "cleanIdea" to "removes IntelliJ project data.",
        "eclipse" to "generates Eclipse project data.",
        "cleanEclipse" to "removes Eclipse project data.",
        "clean" to "removes `build` folders, which store compiled classes and built archives.",
        "test" to "runs unit tests (if any).",
        "build" to "builds sources and archives of every project.",
        "--daemon" to "thanks to this flag, Gradle daemon will be used to run chosen tasks.",
        "--offline" to "when using this flag, cached dependency archives will be used.",
        "--continue" to "when using this flag, errors will not stop the tasks from running.",
        "--refresh-dependencies" to "this flag forces validation of all dependencies. Useful for snapshot versions.",
      ).forEach {
        gradleTaskDescriptions[it.first] = it.second
      }
    }
  }

  fun hasPlatform(id: String): Boolean = platforms.containsKey(id)

  fun getGradleFile(id: String): GradleFile = gradleFiles[id]!!

  fun addGradleTaskDescription(
    task: String,
    description: String,
  ) {
    if (advanced.generateReadme) {
      gradleTaskDescriptions[task] = description
    }
  }

  fun generate() {
    addBasicFiles()
    template.apply(this)
    addJvmLanguagesSupport()
    addExtensions()
    addPlatforms()
    addSkinAssets()
    addReadmeFile()
    addEditorConfig()
    saveProperties()
    saveFiles()
  }

  private fun addBasicFiles() {
    // Adding global assets folder:
    files.add(SourceDirectory(Assets.ID, ""))
    // Adding .gitignore and .gitattributes:
    files.add(CopiedFile(path = ".gitignore", original = path("generator", "gitignore")))
    files.add(CopiedFile(path = ".gitattributes", original = path("generator", "gitattributes")))
  }

  private fun addJvmLanguagesSupport() {
    Java().initiate(this) // Java is supported by default.
    languages.list.forEach {
      it.initiate(this)
      properties[it.id + "Version"] = languages.getVersion(it.id)
    }
    appendSelectedLanguagesVersions()
  }

  private fun appendSelectedLanguagesVersions() {
    languages.list.forEach {
      properties[it.id + "Version"] = languages.getVersion(it.id)
    }
  }

  private fun addExtensions() {
    extensions.officialExtensions.forEach { it.initiate(this) }
    extensions.thirdPartyExtensions.forEach { it.initiate(this) }
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
    // Adding libGDX version property:
    properties["gdxVersion"] = advanced.gdxVersion
    // This property can be changed as the created project updates:
    properties["projectVersion"] = advanced.version
    val prepend =
      """
      # This doesn't need to be false, and some projects may be able to take advantage of setting daemon to true.
      # We set it to false by default in order to avoid too many daemons from being created and persisting; each needs RAM.
      org.gradle.daemon=false
      # Sets starting memory usage to 512MB, maximum memory usage to 1GB, and tries to set as much to use Unicode as we can.
      org.gradle.jvmargs=-Xms512M -Xmx1G -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8
      # "Configure on-demand" must be false because it breaks projects that have Android modules. The default is also false.
      org.gradle.configureondemand=false
      # The logging level determines which messages get shown about how Gradle itself is working, such as if build.gradle
      # files are fully future-proof (which they never are, because Gradle constantly deprecates working APIs).
      # You can change 'quiet' below to 'lifecycle' to use Gradle's default behavior, which shows some confusing messages.
      # You could instead change 'quiet' below to 'info' to see info that's important mainly while debugging build files.
      # Note that if you want to use Gradle Build Scans, you should set the below logging level to 'lifecycle', otherwise
      # the link to the scan won't get shown at all.
      # Documented at: https://docs.gradle.org/current/userguide/command_line_interface.html#sec:command_line_logging
      org.gradle.logging.level=quiet

      """.trimIndent()
    PropertiesFile(properties, prepend).save(basic.destination)
  }

  private fun addSkinAssets() {
    if (advanced.generateSkin) {
      // Adding GUI assets directory:
      files.add(SourceDirectory(Assets.ID, "ui"))

      files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("ui", "uiskin.atlas"),
          original = path("generator", "assets", "ui", "uiskin.atlas"),
        ),
      )
      files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("ui", "uiskin.json"),
          original = path("generator", "assets", "ui", "uiskin.json"),
        ),
      )
      files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("ui", "uiskin.png"),
          original = path("generator", "assets", "ui", "uiskin.png"),
        ),
      )
      files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("ui", "font.fnt"),
          original = path("generator", "assets", "ui", "font.fnt"),
        ),
      )
      files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("ui", "font-list.fnt"),
          original = path("generator", "assets", "ui", "font-list.fnt"),
        ),
      )
      files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("ui", "font-subtitle.fnt"),
          original = path("generator", "assets", "ui", "font-subtitle.fnt"),
        ),
      )
      files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path("ui", "font-window.fnt"),
          original = path("generator", "assets", "ui", "font-window.fnt"),
        ),
      )

      // Android does not support classpath fonts loading through skins.
      // Explicitly copying Liberation Sans font if Android platform is included:
      if (hasPlatform(Android.ID)) {
        arrayOf("png", "fnt").forEach {
          val path = path("com", "badlogic", "gdx", "utils", "lsans-15.$it")
          files.add(
            CopiedFile(
              projectName = Assets.ID,
              path = path,
              original = path,
              fileType = Files.FileType.Classpath,
            ),
          )
        }
      }
    }
  }

  private fun addReadmeFile() {
    if (advanced.generateReadme) {
      files.add(
        SourceFile(
          projectName = "",
          fileName = "README.md",
          content = """# ${basic.name}

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

$readmeDescription

## Platforms

${platforms.values.filter { it.description.isNotBlank() }
            .sortedBy { it.order }
            .joinToString(separator = "\n") { "- `${it.id}`: ${it.description}" }}

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

${gradleTaskDescriptions.map { "- `${it.key}`: ${it.value}" }.sorted().joinToString(separator = "\n")}

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
""",
        ),
      )
    }
  }

  private fun addEditorConfig() {
    if (!advanced.generateEditorConfig) return
    files.add(
      SourceFile(
        projectName = "",
        fileName = ".editorconfig",
        content = """# https://editorconfig.org
root = true

[*]
indent_style = space
end_of_line = lf
charset = utf-8
trim_trailing_whitespace = true
insert_final_newline = true

[*.{java,scala,groovy,kt,kts}]
indent_size = ${advanced.indentSize}

[*.gradle]
indent_size = 2

[*.md]
trim_trailing_whitespace = false
""",
      ),
    )
  }

  fun getAlertCodes(): List<String> {
    val alerts = mutableListOf<String>()
    if (GWT.ID in platforms && languages.list.any { it.id != Java().id }) {
      alerts += "warningNonJavaGwt"
    }
    if (TeaVM.ID in platforms) {
      val coreDependencies = gradleFiles[Core.ID]!!.dependencies
      if (coreDependencies.any { "kotlinx-coroutines-core" in it }) {
        alerts += "warningTeaVMCoroutines"
      }
    }
    return alerts
  }

  fun includeGradleWrapper(
    logger: ProjectLogger,
    executeGradleTasks: Boolean = true,
  ) {
    arrayOf(
      "gradlew",
      "gradlew.bat",
      path("gradle", "gradle-daemon-jvm.properties"),
      path("gradle", "wrapper", "gradle-wrapper.jar"),
      path("gradle", "wrapper", "gradle-wrapper.properties"),
    ).forEach {
      CopiedFile(path = it, original = path("generator", it)).save(basic.destination)
    }
    basic.destination
      .child("gradlew")
      .file()
      .setExecutable(true)
    basic.destination
      .child("gradlew.bat")
      .file()
      .setExecutable(true)
    logger.logNls("copyGradle")
    val gradleTasks = advanced.gradleTasks
    if (executeGradleTasks && gradleTasks.isNotEmpty()) {
      logger.logNls("runningGradleTasks")
      val commands = determineGradleCommand() + gradleTasks
      logger.log(commands.joinToString(separator = " "))
      val process =
        ProcessBuilder(*commands)
          .directory(basic.destination.file())
          .redirectErrorStream(true)
          .start()
      val stream = BufferedReader(InputStreamReader(process.inputStream))
      var line = stream.readLine()
      while (line != null) {
        logger.log(line)
        line = stream.readLine()
      }
      process.waitFor()
      if (process.exitValue() != 0) {
        throw GdxRuntimeException("Gradle process ended with non-zero value.")
      }
    }
  }

  private fun determineGradleCommand(): Array<String> =
    if (UIUtils.isWindows) {
      arrayOf("cmd", "/c", "gradlew")
    } else {
      arrayOf("./gradlew")
    }
}

interface ProjectLogger {
  fun log(message: String)

  fun logNls(bundleLine: String)
}
