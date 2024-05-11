package gdx.liftoff.ui;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

public class UserData {
    public static String projectName;
    public static String packageName;
    public static String mainClassName;
    public static Array<String> platforms;
    public static Array<String> languages;
    public static OrderedMap<String, String> languageVersions;
    public static Array<String> extensions;
    public static String template;
    public static Array<String> thirdPartyLibs;
    public static String libgdxVersion;
    public static String javaVersion;
    public static String appVersion;
    public static String robovmVersion;
    public static boolean addGuiAssets;
    public static boolean addReadme;
    public static String gradleTasks;
    public static String projectPath;
    public static String androidPath;
    public static String log;

    public static Array<String> getLanguages() {
        Array<String> returnValue = new Array<>();
        for (String language : languages) {
            returnValue.add(language + " " + languageVersions.get(language));
        }
        return returnValue;
    }
}
