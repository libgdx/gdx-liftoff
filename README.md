# gdx-liftoff
A modern setup tool for libGDX Gradle projects, forked from czyzby/gdx-setup

![Screenshot of gdx-liftoff](https://i.imgur.com/F2c7BDM.png)

If you've used libGDX for even a short time, you've probably used the official `gdx-setup.jar` made by the libGDX team. You may have used alternative setup tools,
like [czyzby/gdx-setup](https://github.com/czyzby/gdx-setup). The problem these two setup tools share is that they currently use outdated Gradle versions, both old
enough that they won't work with Java 13 or newer, and neither seems to be updated very often. This project provides another alternative setup tool based on
[SquidSetup](https://github.com/tommyettinger/SquidSetup), but removing the close ties to the SquidLib libraries to make it more general-use. Using SquidSetup's
code, which is built on czyzby's code, gives us working projects that use Gradle 6.3, instead of 5.4 for the official setup or 4.0.2 for czyzby's gdx-setup.
Currently, gdx-liftoff depends on libGDX 1.9.10 by default, and allows using snapshots as well.

Projects default to using LWJGL3 instead of LWJGL2 (the old 'desktop' platform), since code tends to be very similar between the two, but LWJGL3 generally offers
more features. This code is tested for compatibility with GWT, including the various changes that Gradle needs with this version. It is sometimes tested on Android,
but Android Studio is often incompatible with recent Gradle releases, and Android certainly doesn't support Java 13 or 14 features across the board. Issues
with iOS, either RoboVM or MOE, will have to be addressed by someone sending a pull request, because I can't reproduce any iOS issues without an iOS device.

The current version of gdx-liftoff uses LWJGL2 internally in order to hopefully support packing UI textures from Gradle.
This has made the app window less nice to look at, because LWJGL2 doesn't support decorating the window yourself.
It's still recommended that you use LWJGL3 for games unless you need texture packing at runtime and PixmapPacker won't
work for your use case.

## Usage

  - Get the latest `gdx-liftoff.jar` from the [Releases tab](https://github.com/tommyettinger/gdx-liftoff/releases) of this project.
  - Regardless of what platforms you intend to target, make sure the steps
    [described by the LibGDX wiki here](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29)
    are taken care of.
  - Run the JAR. Plug in whatever options you see fit:
    - For the Platforms tab, you can use the "Toggle Commonly-Used Platforms" button to enable or disable
      LWJGL3 (which works on all desktop/laptop platforms), and can manually check other platforms to match your needs. 
      If you target iOS, it will only build on a MacOS machine. Downloading iOS (and/or HTML) dependencies can take some
      time, so just check the platforms you want to target. You can re-run the setup, make a new project with the same 
      settings (in a different folder), and copy in the existing code if you want to quickly change platforms.
      - Desktop and/or LWJGL3 should usually be checked, so you can test on the same computer
        you develop on.
        - LWJGL3 is almost the same as Desktop, but because it has better support for new hardware
          (such as high-DPI displays), it should probably be preferred. It also allows multiple windows and drag+drop.
        - Desktop should mostly be preferred if you need to also depend on gdx-tools, such as if you need to run the
          texture packer at runtime. Some machines have issues with an inconsistent or very high framerate with LWJGL3,
          and using the "Legacy" desktop can fix that. 
      - iOS should probably not be checked if you aren't running MacOS and don't intend to later build an iOS
        app on a Mac. It needs some large dependencies to be downloaded when you first import the project.
      - Android should only be checked if you've set up your computer for Android development. Unlike with some other
        setup tools, since gdx-liftoff uses Gradle 6.3, having an Android project present shouldn't interfere with
        other platforms or IDE integration, as long as your IDE supports Gradle 6.3 (Android Studio does not currently).
      - HTML is a more-involved target, with some perfectly-normal code on all other platforms acting completely
        different on HTML due to the tool used, Google Web Toolkit (GWT). It's almost always possible to work around
        these differences and make things like random seeds act the same on all platforms, but it takes work. Mostly,
        you need to be careful with the `long` and `int` number types, and relates to `int` not overflowing as it
        would on desktop, and `long` not being visible to reflection. See [this small guide to GWT](https://github.com/libgdx/libgdx/wiki/HTML5-Backend-and-GWT-Specifics)
        for more. It's very likely that you won't notice any difference unless you try to make behavior identical on GWT
        and other platforms, and even then there may be nothing apparent.
    - For dependencies, you don't need LibGDX checked (the tool is set up to download LibGDX and set it as a
      dependency in all cases).
    - In Advanced, you can set the libGDX version (it defaults to 1.9.10, but can be set lower or higher) and
      various other versions, including the default Java compatibility. Typically, `Java version` is the minimum across
      all platforms, and should be 7 or more (8 is generally safe). You can set `Desktop Java version` to any version at
      least equal to `Java version`, and similarly for `Server Java version`; these only affect the Desktop/LWJGL3 and
      Server modules, respectively. You can set `Java version` to 14 if you have Java 14 installed, but it will require
      users to also have Java 14 or for you to distribute a Java 14 JRE with your game.
  - Click generate, and very soon a window should pop up with instructions for what to do.
    - Generation is very fast here, relative to gdx-setup, because it doesn't run Gradle tasks at this point. When you
      import the generated `build.gradle` project file, tasks will run then.
    
Now you'll have a project all set up with a sample. In IntelliJ IDEA or Android Studio, you can choose to open the
`build.gradle` file and select "Open as Project" to get started. In Eclipse or Netbeans, the process should be similar;
see [libGDX's documentation](https://libgdx.badlogicgames.com/documentation/gettingstarted/Importing%20into%20IDE.html).

  - The way to run a game project that's probably the most reliable is to use Gradle tasks
    to do any part of the build/run process. The simplest way to do this is in the IDE itself,
    via `View -> Tool Windows -> Gradle`, and selecting tasks to perform, such as
    `lwjgl3 -> Tasks -> application -> run.` If you try to run a specific class' `main()`
    method, you may encounter strange issues, but this shouldn't happen with Gradle tasks.
  - If you had the LWJGL3 (or Desktop) option checked in the setup and you chose a non-empty
    template in the Templates tab, you can run the LWJGL3 or Desktop module right away.
    - You can build a runnable jar that includes all it needs to run using
      `lwjgl3 -> Tasks -> build -> jar`; this jar will be in `lwjgl3/build/libs/` when it finishes.
      Note: this is the command-line option `gradlew lwjgl3:jar`, not the `dist` command
      used by the official setup jar. Substitute `desktop` where `lwjgl3` is if you use the legacy
      LWJGL2 version.
  - If you had the Android option checked in the setup and have a non-empty template,
    you can try to run the Android module on an emulator or a connected Android device.
  - If you had the GWT option checked in the setup and have a non-empty template,
    you can go through the slightly slow, but simple, build for GWT, probably using the `superDev`
    task for the `gwt` module, or also possibly the `dist` task in that module.
      - GWT builds have gotten much faster with Gradle 6.3 and some adjustments to configuration, so
        if you were avoiding GWT builds because of slow compile times, you might want to try again.
  - If you had the iOS option checked in the setup, you're running Mac OS X,
    and you have followed all the steps for iOS development with libGDX, maybe you can run
    an iOS task? I can't try myself without a Mac or iOS device, so if you can get this to
    work, posting an issue with any info for other iOS targeters would be greatly appreciated.
  - All builds currently use Gradle 6.3 with the "api/implementation/compile fiasco" resolved. Adding dependencies
    will use the `api` keyword instead of the `compile` keyword it used in earlier versions. All modules use the
    `java-library` plugin, which enables the `api` keyword for dependencies.
  - You may need to refresh the Gradle project after the initial import if some dependencies timed-out;
    JitPack dependencies in particular may take up to 15 minutes to become available if you're using any of those,
    like SquidLib. In IntelliJ IDEA, the `Reimport all Gradle projects` button is a pair of circling arrows in the
    Gradle tool window, which can be opened with `View -> Tool Windows -> Gradle`.
  - Out of an abundance of caution, [the dependency impersonation issue reported here by Márton
    Braun](https://blog.autsoft.hu/a-confusing-dependency/) is handled the way he handled it, by putting
    `jcenter()` last in the repositories lists. I don't know if any other tools have done the same, but it's
    an easy fix and I encourage them to do so.
    
## Known Issues

  - MacOS does not like the legacy desktop apps, showing all sorts of visual glitches.
    It seems to work fine with LWJGL3, in part because that platform had special attention
    paid to it so the `gradlew lwjgl3:run` command can work at all on MacOS.
  - Android hasn't been tested enough, and the generated manifest is probably not very good.

Good luck, and I hope you make something great!

