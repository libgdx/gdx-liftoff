package gdx.liftoff.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class UserData {
    public static String projectName;
    public static String packageName;
    public static String mainClassName;
    public static ArrayList<String> platforms;
    public static ArrayList<String> languages;
    public static LinkedHashMap<String, String> languageVersions;
    public static ArrayList<String> extensions;
    public static String template;
    public static LinkedHashSet<String> thirdPartyLibs;
    public static String libgdxVersion;
    public static String javaVersion;
    public static String appVersion;
    public static String androidPluginVersion;
    public static String robovmVersion;
    public static String gwtPluginVersion;
    public static boolean addGuiAssets;
    public static boolean addReadme;
    public static String gradleTasks;
    public static String projectPath;
    public static String androidPath;
    public static String log;

    public static ArrayList<String> getLanguages() {
        ArrayList<String> returnValue = new ArrayList<>();
        for (String language : languages) {
            returnValue.add(language + " " + languageVersions.get(language));
        }
        return returnValue;
    }
}
