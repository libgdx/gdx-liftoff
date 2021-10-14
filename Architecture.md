# `gdx-liftoff` architecture

`gdx-liftoff` is a copy of [czyzby/gdx-setup](https://github.com/czyzby/gdx-setup) application, sharing the code
general structure and technology stack. The main frameworks used throughout the application include:

* [libGDX](https://libgdx.com/).
* [LML](https://github.com/crashinvaders/gdx-lml/tree/master/lml): GUI builder. `scene2d.ui` templates with HTML-like
syntax.
* [Autumn](https://github.com/crashinvaders/gdx-lml/tree/master/autumn): dependency injection framework.
* [Autumn MVC](https://github.com/crashinvaders/gdx-lml/tree/master/mvc): model-view-controller framework based on
Autumn and LML.
* [VisUI](https://github.com/kotcrab/vis-ui/): flat design `scene2d.ui` skin with numerous additional widgets.

### Motivation

By today's standards, `gdx-liftoff` is not a typical Kotlin libGDX application. The tool was started when Kotlin
was a relatively new language, and Kotlin extensions for libGDX such as [KTX](https://github.com/libktx/ktx) did not
exist. The project was a joint effort between [@czyzby](https://github.com/libktx) (the author of the `gdx-lml`
libraries) and [@kotcrab](https://github.com/kotcrab) (the author of the VisUI framework).

This application was also meant as an example use case of the Autumn MVC framework, which admittedly was _not_ the best
tool for the job. Autumn MVC does have some advantages, such as UI hot reload or multi-platform dependency injection
with automatic scanning that significantly cuts down on glue code and boilerplate. However, it is a heavily opinionated
framework that is rather complex, and does quite a lot under the hood. Additionally, the framework itself was
implemented with Java in mind, making many APIs feel clunky in Kotlin.

Using a non-standard technology stack, `gdx-liftoff` can be difficult to extend for new contributors. This document
aims to make it a bit easier by explaining how the application actually works.

## Application flow

### Initiation

- [main.kt](src/main/kotlin/gdx/liftoff/main.kt) is the main entrypoint of the application. It tries to run a libGDX
application, which some additional code to avoid platform-specific issues.
- `AutumnApplication` is the implementation of the libGDX `ApplicationListener`. It handles the lifecycle and rendering
of the application, delegating each application event to an internal Autumn MVC service.
- `DesktopClassScanner` is used to scan the classpath in search of classes annotated with Autumn component annotations.
  - Application components can be annotated with annotations such as `@Component`, `@Property`, `@View`, `@ViewDialog`,
  or `@ViewActionContainer`.
  - The list of standard Autumn and Autumn MVC annotations is extended with custom application-specific annotations such
  as `@Extension`, `@ProjectTemplate`, `@JvmLanguage`, and `@GdxPlatform`.
- All the annotated classes are found in the classpath during the application startup, constructed with reflection,
filled using dependency injection, and registered in the Autumn context as singletons.
  - Autumn injects values into fields annotated with `@Inject`.
  - Autumn MVC processors handles some fields annotated with annotations such as `@Preference` or `@StageViewport`.
  These are standard Autumn MVC features that are mostly used to configure the Autumn MVC application settings in
  [Configuration.kt](src/main/kotlin/gdx/liftoff/config/Configuration.kt).
  - Autumn MVC handles application preferences from the [`preferences`](src/main/kotlin/gdx/liftoff/preferences)
  package. They include some values such as package name of the generated project which are cached from the previous
  tool usage.
  - Custom annotation processors ensure that this step also creates classes representing official and third-party
  extensions, project templates, languages, and libGDX platforms. A respective annotation is sufficient to register
  them in the application, and they require no further setup.
- Component methods annotated with `@Initiate` are called after the components are constructed.
  - [Configuration.initiate](src/main/kotlin/gdx/liftoff/config/Configuration.kt) loads a custom VisUI skin and extends
  the LML parser with custom tags representing additional GUI widgets.
- Application views and dialogs are constructed from LML templates.
  - `LmlParser` configured in [Configuration.kt](src/main/kotlin/gdx/liftoff/config/Configuration.kt) handles LML
  templates and associated controllers.
  - LML templates are stored in [`resources/templates/`](src/main/resources/templates).
    - Tags within LML templates represent individual Scene2D widgets displayed in the views.
    - Tags starting with `:` are macros that implement basic scripting functionalities such as iteration or conditionals.
  - Classes annotated with `@ViewActionContainer` such as
  [GlobalActionContainer](src/main/kotlin/gdx/liftoff/actions/GlobalActionContainer.kt) define functions available in
  the LML templates. `@LmlAction` annotation specifies IDs of the functions as they appear in the templates.
  - Classes annotated with `@View` and `@ViewDialog` represent view controllers. They point to their corresponding LML
  templates. Their fields annotated with `@LmlActor` and `@LmlInject` are constructed and injected by the `LmlParser`
  after their template is parsed.
    - View methods annotated with `@LmlAction` can be executed from within LML templates. Notably, their results can
    also be handled by the parser to display Scene2D widgets. That is how data such as available libraries, platforms,
    languages, or project templates are exposed to and displayed by the LML templates.
    - Types of fields annotated with `@LmlInject` can have nested LML annotations such as `@LmlActor`. They will be
    handled recursively by the `LmlParser`.
    - If a value of `@LmlInject`-annotated field is `null`, a new instance will be created using reflection. If it
    already exists, it's annotated field will be handled by the parser, but the instance will not be replaced. That is
    why some fields can be annotated with both `@LmlInject` and `@Inject` - in such cases Autumn will handle dependency
    injection and `LmlParser` will process nested fields.
    - LML annotations are strictly related to the UI and mostly handle injection of Scene2D actor instances.
- [MainView](src/main/kotlin/gdx/liftoff/views/MainView.kt) is set as the initial (and the only) application view.
  - This view will be continuously rendered by Autumn MVC `InterfaceService`.
  - Actors defined by the [main.lml](src/main/resources/templates/main.lml) template are displayed on the application's
  Scene2D `Stage` and handle user input.

The results of the initiation are as follows:

- All annotated application components are created via reflection and filled by a dependency injection framework.
- All components such as libGDX libraries, platforms, languages, or project templates are created and registered.
- [MainView](src/main/kotlin/gdx/liftoff/views/MainView.kt) is constructed from a LML template and rendered.

### Project generation

- Clicking on the non-disabled "Generate project!" button shows the
[GenerationPrompt](src/main/kotlin/gdx/liftoff/views/dialogs/generationPrompt.kt) dialog.
- Before the dialog is shown, it launches a task on another thread that handles actual project generation.
- A [Project](src/main/kotlin/gdx/liftoff/data/project/project.kt) is created with the current application data.
  - Project data is based on the state of the GUI. The selected libraries, platforms, languages, and template are passed
  to the project.
- `Project.generate` generates the project files.
  - `addBasicFiles` handles generic files such as `.gitignore`.
  - `addJvmLanguagesSupport` configures Java and additional selected JVM languages based on
  [`Language`](src/main/kotlin/gdx/liftoff/data/languages/Language.kt) classes.
  - `addExtensions` handles official and third-party extensions defined by
  [`Library`](src/main/kotlin/gdx/liftoff/data/libraries/library.kt) classes, setting Gradle properties with library
  versions, and adding appropriate Gradle dependencies to defined modules.
    - This step also fetches the latest extension versions from the corresponding repository such as Maven Central
    or JitPack.
  - `template.apply` generates project sources according to the chosen
  [`Template`](src/main/kotlin/gdx/liftoff/data/templates/Template.kt).
  - `addPlatforms` adds platform-specific files of each selected libGDX
  [`Platform`](src/main/kotlin/gdx/liftoff/data/platforms/Platform.kt).
  - `addSkinAssets` optionally adds Scene2D skin assets to the project.
  - `addReadmeFile` defines a README file based on the selected project configuration.
  - `saveProperties` generates a `gradle.properties` file.
  - `saveFiles` saves the defined project files in the selected project location.
- `Project.includeGradleWrapper` adds the Gradle wrapper files.
- The prompt dialog buttons are no longer disabled after the project is generated. The dialog can be closed.

## Adding content

### JVM language support

Add a `@JvmLanguage` annotated class implementing the `Language` interface to the
[`languages`](src/main/kotlin/gdx/liftoff/data/languages) package. Add the necessary dependencies and files to the
project in the `initiate` function.

### Official libGDX extension

Add an `@Extension(official = true)` annotated class extending the `OfficialExtension` class to the
[officialExtensions.kt](src/main/kotlin/gdx/liftoff/data/libraries/official/officialExtensions.kt) file. Add library
dependencies in the `initiate` method. Add a Gradle property with the library version if the extension does not share
the libGDX version.

The `id` property defines the unique ID of the library throughout the project. To ensure that the library is properly
described in the tool, add `id=` and `idTip=` entries to the [nls.properties](src/main/resources/i18n/nls.properties)
file (replacing the `id` with a custom one) with the formatted library name and short description. For example:

```properties
myLibrary=My Library
myLibraryTip=Generic libGDX utilities.
```

### Third-party extension

Add an `@Extension` annotated class extending the `ThirdPartyExtension` class to the
[thirdPartyExtensions.kt](src/main/kotlin/gdx/liftoff/data/libraries/unofficial/thirdPartyExtensions.kt) file.
Add library dependencies in the `initiateDependencies` method. Note that the library version based on the chosen `id`
will already be added to `gradle.properties`, and the `addDependency` method does not require passing the version.
If you need to define a dependency with other version than the library itself, use the `addExternalDependency` method.

The `id` property defines the unique ID of the library throughout the project. To ensure that the library is properly
described in the tool, add `id=` and `idTip=` entries to the [nls.properties](src/main/resources/i18n/nls.properties)
file (replacing the `id` with yours) with the formatted library name and short description.

### New libGDX backend

Add a `@GdxPlatform` annotated class implementing the `Platform` interface along with a class representing the Gradle
file that extends `GradleFile`. Add necessary files in the `initiate` method. If any platform-specific libraries have
to be added to support the backend, modify the necessary extension classes.

See the current backends in the [`platforms`](src/main/kotlin/gdx/liftoff/data/platforms) package.

### Project template

Add a `@ProjectTemplate` annotated class implementing the `Template` interface to the
[`templates`](src/main/kotlin/gdx/liftoff/data/templates) package. If the template uses only official libGDX APIs,
use `@ProjectTemplate(official = true)` annotation and put the class in the `official` package. Otherwise, the template
file should be in the `unofficial` package. Implement the `Template` interface methods generating all the necessary
files. Include any dependencies that are required to run the template. See existing project templates for details.

### GUI changes

All GUI changes have to be made in the [LML templates](src/main/resources/templates) and the corresponding
[view controllers](src/main/kotlin/gdx/liftoff/views).

See the [LML wiki](https://github.com/czyzby/gdx-lml/wiki/LibGDX-Markup-Language) for more info about the framework.

### Tool translation

Translate the [nls.properties](src/main/resources/i18n/nls.properties) file. Create an issue or submit a pull request
with the translation. While the application is capable of supporting different locales, currently only English
translation exists.

## Additional documentation

- [LML wiki](https://github.com/czyzby/gdx-lml/wiki/LibGDX-Markup-Language).
- [Autumn documentation and sources](https://github.com/crashinvaders/gdx-lml/tree/master/autumn).
- [Autumn MVC documentation and sources](https://github.com/crashinvaders/gdx-lml/tree/master/mvc).
