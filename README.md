[![Build status](https://github.com/tommyettinger/gdx-liftoff/workflows/build/badge.svg)](https://github.com/tommyettinger/gdx-liftoff/actions/workflows/build.yml)

# 🚀 gdx-liftoff 🚀

A setup tool for [libGDX](https://libgdx.com/) Gradle projects.

[![Screenshot of gdx-liftoff](.github/screenshot.png)](https://github.com/tommyettinger/gdx-liftoff/releases)

<h1 style="text-align: center;">
    📥
    <strong><a href="https://github.com/tommyettinger/gdx-liftoff/releases">DOWNLOAD</a></strong>
    📥
</h1>

To generate a project, [download](https://github.com/tommyettinger/gdx-liftoff/releases) the latest application
`jar` and run it (usually double-clicking will do), or run the following command manually (replacing the `VERSION` appropriately):

```shell
java -jar gdx-liftoff-VERSION.jar
```

**You should use Java 17+ !** Android tools now require your installed JDK to be version 17 or higher.
You can still target other releases, as low as 8 typically, while building with any of the JDK versions 17 and up.
Java 21 works now, but when Java 22 is released (or if you are using an early access build), it won't work right
away. That's just how Gradle is. When Gradle releases a version that expands JVM compatibility, we try to update
quickly to that version.

If you have any trouble, you can try our new [🐛Troubleshooting Guide🐛](Troubleshooting.md).

## Features

In addition to most features of the official `gdx-setup`, the `gdx-liffoff` tool offers:
 
- **Project templates.** You can choose one of many project skeletons highlighting various libGDX features.
- **Instant input validation.** Your project data is validated as you type it in.
- **Other JVM languages support.** You can choose additional languages for your project, like Kotlin or Scala.
Their standard libraries, Gradle plugins, and appropriate source folders will be included.
- **Customization.** You have more control over the versions of software used by your application.
- **More third-party extensions.** Their versions are fetched from Maven Central or JitPack, so your project is always 
generated up-to-date.
- **Automatic configuration for tricky extensions.** If you're having trouble setting up
Artemis-ODB, Lombok, or several other libraries, Liftoff does some extra work, so you don't have to. 
- **Preferences support.** Basic data of your application is saved, so you don't have to fill it each time
you generate a project.
- **Optional Gradle runner.** You can optionally execute Gradle tasks after project generation. Gradle doesn't have to
run for a project to be created.
- **Supports all libGDX backends.** Do you need the LWJGL2, LWJGL3, and/or Headless backends? Liftoff
provides simple checkboxes to add any and all official platforms, plus some special other modules.
- **Convenience features.** If you have JetBrains Toolbox, you can open a just-generated project in IDEA with one click
(it needs "Generate Shell Scripts" set in Toolbox). If you use Eclipse, you can still import Gradle projects normally.
- **Up-to-date.** This project prides itself on updating quickly after releases of major external components such
as Gradle or libGDX itself.
- **More backends are supported.** MOE is pretty much gone from gdx-setup, but that iOS backend's Community Edition
is still supported here. TeaVM, an increasingly-popular alternative to GWT for browser-based applications, is also here,
and works with Kotlin and other JVM languages as well as it does with Java.

What's more, there are no major *structural differences* between any generated projects, regardless of the platforms
you initially used. The official `gdx-setup` previously put assets in the `android` module, or the `core` module if Android is
not selected. If you don't start with the Android platform, adding it to an existing project would require modification
of the Gradle scripts. To avoid this issue, `gdx-liftoff` puts `assets` in the root folder. Adding a new platform
to an existing application never forces you to modify any of the other modules. It should be noted that this feature was
brought into gdx-setup in part because it worked in gdx-liftoff first.

## Guide

For more details on how to use the application and how it works, see the [usage guide](Guide.md).
If you would like to contribute to the project, you might find the [architecture document](Architecture.md)
helpful.

When submitting a pull request, please format the application with the `ktlintFormat` Gradle task.

## Credits

The project was forked from the [`czyzby/gdx-setup`](https://github.com/czyzby/gdx-setup) repository.
[@czyzby](https://github.com/czyzby) and [@kotcrab](https://github.com/kotcrab) have created the original application,
as well as a set of libraries that it depends on (`gdx-lml` and VisUI respectively). Since then, the project is
maintained by [@tommyettinger](https://github.com/tommyettinger).

[@raeleus](https://github.com/raeleus) created the
[Particle Park skin for scene2d.ui](https://ray3k.wordpress.com/particle-park-ui-skin-for-scene2d-ui/),
which was adapted to be the default skin added to new projects (if the _"Generate UI Assets"_ option is selected).
"Accademia di Belle Arti di Urbino and students of MA course of Visual design" has created the _Titillium Web_
font that the skin uses (under SIL OFL license).

Other project contributors include [@Mr00Anderson](https://github.com/Mr00Anderson), [@lyze237](https://github.com/lyze237),
[@metaphore](https://github.com/metaphore), and [@payne911](https://github.com/payne911). People who haven't directly
contributed code have still helped a lot by spending their time to test on platforms like macOS and iOS;
[@JojoIce](https://github.com/JojoIce) is one of several people who made a difference regarding iOS. And of course, many
thanks go to all the early adopters for putting up with any partially-working releases early on!

---

Good luck, and we hope you make something great!
