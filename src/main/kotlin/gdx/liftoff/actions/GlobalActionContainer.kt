package gdx.liftoff.actions

import com.badlogic.gdx.Gdx
import com.github.czyzby.autumn.mvc.stereotype.ViewActionContainer
import com.github.czyzby.kiwi.util.common.Exceptions
import com.github.czyzby.kiwi.util.common.Strings
import com.github.czyzby.lml.annotation.LmlAction
import com.github.czyzby.lml.parser.action.ActionContainer
import kotlin.system.exitProcess

/**
 * Contains actions available for all dialogs and views.
 */
@ViewActionContainer("global")
@Suppress("unused") // Class accessed via reflection.
class GlobalActionContainer : ActionContainer {
  @LmlAction("showSite")
  fun showLibGdxWebsite() = Gdx.net.openURI("https://libgdx.com/")

  @LmlAction("fileNameFilter")
  fun isValidFileNameCharacter(character: Char): Boolean = Character.isDigit(character) ||
    Character.isLetter(character) || character == '-' || character == '_'

  @LmlAction("isValidFile")
  fun isValidFileName(input: String): Boolean = Strings.isNotBlank(input) && input.matches(Regex("[-\\w]+"))

  @LmlAction("isSdk")
  fun isAndroidSdkDirectory(path: String): Boolean {
    try {
      val file = Gdx.files.absolute(path)
      if (file.isDirectory) {
        return (
          file.child("tools").isDirectory ||
            file.child("cmdline-tools").isDirectory ||
            file.child("build-tools").isDirectory
          ) && file.child("platforms").isDirectory
      }
    } catch (exception: Exception) {
      Exceptions.ignore(exception) // Probably not the Android SDK.
    }
    return false
  }

  @LmlAction("javaClassFilter")
  fun isValidJavaCharacter(character: Char): Boolean = Character.isJavaIdentifierPart(character)

  @LmlAction("javaPackageFilter")
  fun isValidJavaPackageCharacter(character: Char): Boolean = Character.isJavaIdentifierPart(character) || character == '.'

  @LmlAction("isValidClass")
  fun isValidClassName(input: String): Boolean {
    if (Strings.isBlank(input) || !Character.isJavaIdentifierStart(input[0])) {
      return false
    } else if (input.length == 1) {
      return true
    }
    for (id in 1 until input.length) {
      if (!Character.isJavaIdentifierPart(input[id])) {
        return false
      }
    }
    return true
  }

  @LmlAction("isValidPackage")
  fun isValidPackageName(input: String): Boolean {
    if (Strings.isBlank(input) || !Character.isJavaIdentifierStart(input[0]) || input.contains("..") || input.endsWith('.')) {
      return false
    }
    var previousDot = false
    for (id in 1 until input.length) {
      if (input[id] == '.') {
        previousDot = true
      } else {
        if ((previousDot && !Character.isJavaIdentifierStart(input[id])) || !Character.isJavaIdentifierPart(input[id])) {
          return false
        }
        previousDot = false
      }
    }
    // case-insensitive check for any Java reserved word, then keep checking for Win32 reserved file/folder names.
    return !(
      !input.contains('.') || input.matches(
        Regex(
          // case-insensitive check for any Java reserved word, then keep checking for Win32 reserved file/folder names.
          "(?i).*(^|\\.)(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|double|do|else|enum|extends|false|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|true|try|void|volatile|while|_|con|prn|aux|nul|(com[1-9])|(lpt[1-9]))(\\.|$).*"
        )
      )
      )
  }

  @LmlAction("androidPluginVersion")
  fun getDefaultAndroidPluginVersion(): String = "8.1.4"

  @LmlAction("roboVMVersion")
  fun getDefaultRoboVMVersion(): String = "2.3.20"

  @LmlAction("gwtPluginVersion")
  fun getDefaultGwtPluginVersion(): String = "1.1.29"

  @LmlAction("close")
  fun noOp() {
    // Empty dialog closing utility.
  }

  @LmlAction("exit")
  fun exit() {
    Gdx.app.exit()
    exitProcess(0)
  }
}
