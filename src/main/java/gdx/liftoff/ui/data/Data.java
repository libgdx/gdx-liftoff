package gdx.liftoff.ui.data;

import com.badlogic.gdx.utils.Array;

//todo: Remove this file and use nls.properties and other sources
public class Data {
    //common
    public static String liftoffVersion = "v1.12.1.7";

    //add-ons panel
    public static Array<String> platformsNames = new Array<>(new String[] {"CORE", "ANDROID", "DESKTOP"});
    public static Array<String> languagesNames = new Array<>(new String[] {"GROOVY 3.0", "KOTLIN 1.4"});
    public static Array<String> extensionsNames = new Array<>(new String[] {"ASHLEY", "TOOLS", "ASHLEY", "TOOLS", "ASHLEY", "TOOLS", "ASHLEY", "TOOLS"});

    //languages dialog
    public static String groovyDefaultVersion = "4.0.2";
    public static String kotlinDefaultVersion = "1.9.22";
    public static String scalaDefaultVersion = "2.13.8";
}
