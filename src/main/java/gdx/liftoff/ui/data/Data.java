package gdx.liftoff.ui.data;

import com.badlogic.gdx.utils.Array;

public class Data {
    //common
    public static String liftoffVersion = "v1.12.1.7";
    public static String subtitle = "a modern setup tool for libGDX Gradle projects";
    public static String updateUrl = "https://github.com/libgdx/gdx-liftoff/releases";

    //landing table
    public static String logoTooltipDescription = "Developed and maintained by TEttinger\nUI design by Raeleus";
    public static String updateTooltipDescription = "A new update is available\nClick to view GitHub for more details";
    public static String newProjectTooltipDescription = "Create a new libGDX project using a wizard style setup.";
    public static String quickProjectTooltipDescription = "Create a new libGDX project without any advanced options. The resulting project contains Desktop, Android, iOS, and HTML5 backends.";

    //project panel
    public static String projectNameTooltipDescription = "Name has to be a valid file name. Can contain letters, numbers, underscores and dashes. For example: \"YourGame\", \"your-game\".";
    public static String packageTooltipDescription = "Package has to be a valid Java identifier. Should contain at least one dot for Android compatibility. For example: \"com.yourcompany.yourgame\".";
    public static String mainClassTooltipDescription = "Main class has to be a valid Java identifier. Cannot start with a number. For example: \"Center\", \"Main\", \"TopLevel\", \"YourGame\".";

    //social panel
    public static String libgdxURL = "https://libgdx.com/";
    public static String discordURL = "https://libgdx.com/community/discord/";
    public static String wikiURL = "https://libgdx.com/wiki/";
    public static String libgdxTooltipDescription = "The official libGDX website";
    public static String discordTooltipDescription = "The libGDX community Discord server";
    public static String wikiTooltipDescription = "The libGDX wiki\nLearn through tutorials, articles, and links";

    //add-ons panel
    //todo: These lists are temporary and should be filled by some other means. Consult TEttinger.
    public static Array<String> platformsNames = new Array<>(new String[] {"CORE", "ANDROID", "DESKTOP"});
    public static Array<String> languagesNames = new Array<>(new String[] {"GROOVY 3.0", "KOTLIN 1.4"});
    public static Array<String> extensionsNames = new Array<>(new String[] {"ASHLEY", "TOOLS"});
    public static String gwtLanguageWarning = "Note: unlike TeaVM, the GWT web backend does not support other languages.";

    //platforms dialog
    public static String coreDescription = "Main module shared by all platforms.";
    public static String desktopDescription = "Primary desktop backend using LWJGL3.";
    public static String androidDescription = "Android mobile backend. Needs Android SDK.";
    public static String iosDescription = "iOS mobile backend using RoboVM.";
    public static String htmlDescription = "Web backend using GWT and WebGL.";
    public static String headlessDescription = "Desktop backend without a graphical interface.";
    public static String htmlTeavmDescription = "Experimental web backend using TeaVM and WebGL.";
    public static String desktopLegacyDescription = "Legacy desktop backend using LWJGL2.";
    public static String serverDescription = "Optional server project without libGDX libraries.";
    public static String sharedDescription = "Optional module shared by Core and Server.";
    public static String iosMultiosDescription = "iOS mobile backend using Multi-OS Engine.";

    //languages dialog
    public static String groovyDefaultVersion = "4.0.2";
    public static String kotlinDefaultVersion = "1.9.22";
    public static String scalaDefaultVersion = "2.13.8";
    public static String clojureLinkText = "play-clj: a Clojure libGDX wrapper";
    public static String clojureLinkURL = "https://github.com/oakes/play-clj";
    public static String otherJVMLinkText = "Using other JVM languages in libGDX";
    public static String otherJVMLinkURL = "https://libgdx.com/wiki/jvm-langs/using-libgdx-with-other-jvm-languages";
}
