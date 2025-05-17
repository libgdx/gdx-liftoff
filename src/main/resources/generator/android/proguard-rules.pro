# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   https://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-verbose

-dontwarn android.support.**
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication

# Needed by the gdx-controllers official extension.
-keep class com.badlogic.gdx.controllers.android.AndroidControllers

# Needed by the Box2D official extension.
-keepclassmembers class com.badlogic.gdx.physics.box2d.World {
   boolean contactFilter(long, long);
   boolean getUseDefaultContactFilter();
   void    beginContact(long);
   void    endContact(long);
   void    preSolve(long, long);
   void    postSolve(long, long);
   boolean reportFixture(long);
   float   reportRayFixture(long, float, float, float, float, float);
}

# You will need the next three lines if you use scene2d for UI or gameplay.
# If you don't use scene2d at all, you can remove or comment out the next line:
-keep public class com.badlogic.gdx.scenes.scene2d.** { *; }
# You will need the next two lines if you use BitmapFont or any scene2d.ui text:
-keep public class com.badlogic.gdx.graphics.g2d.BitmapFont { *; }
# You will probably need this line in most cases:
-keep public class com.badlogic.gdx.graphics.Color { *; }

# These two lines are used with mapping files; see https://developer.android.com/build/shrink-code#retracing
-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile
