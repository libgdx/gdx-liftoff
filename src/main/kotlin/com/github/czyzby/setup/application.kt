package com.github.czyzby.setup

import com.kotcrab.vis.ui.util.OsUtils
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecuteResultHandler
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.management.ManagementFactory

/**
 * Application related utils.
 * @author Kotcrab
 */
object Application {
    /**
     * Starts new instance of application. This method is very flexible and supports starting new instance when
     * original application instance was launched via IDE (Intellij IDEA, Eclipse untested), using classpath or via jar.
     * After calling this original instance may exit safely. Output of new instance is NOT redirected to original instance
     * output streams.
     */
    fun startNewInstance() {
        try {
            val cmdLine = CommandLine.parse(getRestartCommand())
            val executor = DefaultExecutor()
            executor.streamHandler = PumpStreamHandler(null, null, null)
            executor.execute(cmdLine, DefaultExecuteResultHandler())
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun getJavaBinPath(): String {
        if (OsUtils.isWindows()) {
            val javaBin = File(System.getProperty("java.home") + "/bin/java.exe")
            if (javaBin.exists()) {
                return "\"" + javaBin.absolutePath + "\""
            }
        }

        return "java"
    }

    private fun getRestartCommand(): String {
        val vmArguments = ManagementFactory.getRuntimeMXBean().inputArguments
        val vmArgsOneLine = StringBuilder()

        if (OsUtils.isMac()) {
            vmArgsOneLine.append("-XstartOnFirstThread ")
        }

        for (arg in vmArguments) {
            if (arg.contains("-agentlib") == false) {
                vmArgsOneLine.append(arg).append(" ")
            }
        }

        val cmd = StringBuilder(getJavaBinPath() + " " + vmArgsOneLine)

        val mainCommand = System.getProperty("sun.java.command").split(" ")

        if (mainCommand[0].endsWith(".jar"))
            cmd.append("-jar " + File(mainCommand[0]).path)
        else
            cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0])

        for (i in 1..mainCommand.size - 1) {
            cmd.append(" ")
            cmd.append(mainCommand[i])
        }

        //if launching from idea, not in debug mode
        val ideaLauncher = "-Didea.launcher.bin.path="
        val ideaLauncherStart = cmd.indexOf(ideaLauncher)
        if (ideaLauncherStart != -1) {
            cmd.insert(ideaLauncherStart + ideaLauncher.length, "\"")
            cmd.insert(cmd.indexOf("-cp ", ideaLauncherStart) - 1, "\"")
        }

        return cmd.toString()
    }
}

fun Throwable.stackTraceToString(): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw, true)
    this.printStackTrace(pw)
    return sw.buffer.toString()
}
