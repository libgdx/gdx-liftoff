/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//Note, the above license and copyright applies to this file only.
package gdx.liftoff;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;

import org.lwjgl.system.JNI;
import org.lwjgl.system.linux.UNISTD;
import org.lwjgl.system.macosx.LibC;
import org.lwjgl.system.macosx.ObjCRuntime;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adds some utilities to ensure that the JVM was started with the
 * {@code -XstartOnFirstThread} argument, which is required on macOS for LWJGL 3
 * to function. Also helps on Windows when users have names with characters from
 * outside the Latin alphabet, a common cause of startup crashes.
 * <br>
 * <a href="https://jvm-gaming.org/t/starting-jvm-on-mac-with-xstartonfirstthread-programmatically/57547">Based on this java-gaming.org post by kappa</a>
 * @author damios
 */
public class StartupHelper {

    // No need to throw redundant exceptions.  Instances of this class would be useless anyway.
    private StartupHelper() {}

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    /**
     * Must only be called on Linux. Check OS first!
     * @return true if NVIDIA drivers are in use on Linux, false otherwise
     */
    public static boolean isLinuxNvidia() {
        // The 'dir' param can't be '_' because it's not supported in older Java versions.
        String[] drivers = new File("/proc/driver").list((dir, path) -> path.toUpperCase(Locale.ROOT).contains("NVIDIA"));
        if (drivers == null) return false;
        return drivers.length > 0;
    }

    /**
     * Starts a new JVM if the application was started on macOS without the
     * {@code -XstartOnFirstThread} argument. Returns whether a new JVM was
     * started and thus no code should be executed. Redirects the output of the
     * new JVM to the old one.
     * <p>
     * <u>Usage:</u>
     *
     * <pre><code>
     * public static void main(String... args) {
     * 	if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
     * 	// the actual main method code
     * }
     * </code></pre>
     *
     * @return whether a new JVM was started and thus no code should be executed
     *         in this one
     */
    public static boolean startNewJvmIfRequired() {
        return startNewJvmIfRequired(true);
    }

    /**
     * Starts a new JVM if the application was started on macOS without the
     * {@code -XstartOnFirstThread} argument. This also includes some code for
     * Windows, for the case where the user's home directory includes certain
     * non-Latin-alphabet characters (without this code, most LWJGL3 apps fail
     * immediately for those users). Returns whether a new JVM was started and
     * thus no code should be executed.
     * <p>
     * <u>Usage:</u>
     *
     * <pre><code>
     * public static void main(String... args) {
     * 	if (StartupHelper.startNewJvmIfRequired(true)) return; // This handles macOS support and helps on Windows.
     * 	// after this is the actual main method code
     * }
     * </code></pre>
     *
     * @param redirectOutput
     *            whether the output of the new JVM should be rerouted to the
     *            old JVM, so it can be accessed in the same place; keeps the
     *            old JVM running if enabled
     * @return whether a new JVM was started and thus no code should be executed
     *         in this one
     */
    public static boolean startNewJvmIfRequired(boolean redirectOutput) {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (osName.contains("mac")) return startNewJvm0(/*isMac =*/ true, redirectOutput);
        if (osName.contains("windows")) {
            // Here, we are trying to work around an issue with how LWJGL3 loads its extracted .dll files.
            // By default, LWJGL3 extracts to the directory specified by "java.io.tmpdir", which is usually the user's home.
            // If the user's name has non-ASCII (or some non-alphanumeric) characters in it, that would fail.
            // By extracting to the relevant "ProgramData" folder, which is usually "C:\ProgramData", we avoid this.
            // We also temporarily change the "user.name" property to one without any chars that would be invalid.
            // We revert our changes immediately after loading LWJGL3 natives.
            String programData = System.getenv("ProgramData");
            if (programData == null) programData = "C:\\Temp"; // if ProgramData isn't set, try some fallback.
            String prevTmpDir = System.getProperty("java.io.tmpdir", programData);
            String prevUser = System.getProperty("user.name", "libGDX_User");
            System.setProperty("java.io.tmpdir", programData + "\\libGDX-temp");
            System.setProperty(
                "user.name",
                ("User_" + prevUser.hashCode() + "_GDX" + Version.VERSION).replace('.', '_')
            );
            Lwjgl3NativesLoader.load();
            System.setProperty("java.io.tmpdir", prevTmpDir);
            System.setProperty("user.name", prevUser);
            return false;
        }
        return startNewJvm0(/*isMac =*/ false, redirectOutput);
    }

    private static final String MAC_ERR_MSG = "There was a problem evaluating whether the JVM was started with the -XstartOnFirstThread argument.";
    private static final String LINUX_ERR_MSG = "There was a problem evaluating whether the JVM was restarted with __GL_THREADED_OPTIMIZATIONS disabled.";
    private static final String MAC_ERR_MSG_2 = "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the -XstartOnFirstThread argument manually!";
    private static final String LINUX_ERR_MSG_2 = "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the environment variable __GL_THREADED_OPTIMIZATIONS=0";

    public static boolean startNewJvm0(boolean isMac, boolean redirectOutput) {
        long processID = getProcessID(isMac);
        if (!isMac) {
            // No need to restart non-NVIDIA Linux
            if (!isLinuxNvidia()) return false;
            // check whether __GL_THREADED_OPTIMIZATIONS is already disabled
            if ("0".equals(System.getenv("__GL_THREADED_OPTIMIZATIONS"))) return false;
        } else {
            // There is no need for -XstartOnFirstThread on Graal native image
            if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) return false;

            // Checks if we are already on the main thread, such as from running via Construo.
            long objcMsgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
            long nsThread = ObjCRuntime.objc_getClass("NSThread");
            long currentThread = JNI.invokePPP(nsThread, ObjCRuntime.sel_getUid("currentThread"), objcMsgSend);
            boolean isMainThread = JNI.invokePPZ(currentThread, ObjCRuntime.sel_getUid("isMainThread"), objcMsgSend);
            if (isMainThread) return false;

            if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + processID))) return false;
        }

        // check whether the JVM was previously restarted
        // avoids looping, but most certainly leads to a crash
        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println(/*x =*/ getErrMsg(isMac));
            return false;
        }

        // Restart the JVM with updated (env || jvmArgs)
        List<String> jvmArgs = new ArrayList<>();
        // The following line is used assuming you target Java 8, the minimum for LWJGL3.
        String javaExecPath = System.getProperty("java.home") + "/bin/java";
        // If targeting Java 9 or higher, you could use the following instead of the above line:
        //String javaExecPath = ProcessHandle.current().info().command().orElseThrow()
        if (!(new File(javaExecPath).exists())) {
            System.err.println(/*x =*/ getErrMsg2(isMac));
            return false;
        }

        jvmArgs.add(javaExecPath);
        if (isMac) jvmArgs.add("-XstartOnFirstThread");
        jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        jvmArgs.add("-cp");
        jvmArgs.add(System.getProperty("java.class.path"));
        String mainClass = System.getenv("JAVA_MAIN_CLASS_" + processID);
        if (mainClass == null) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (trace.length > 0) mainClass = trace[trace.length - 1].getClassName();
            else {
                System.err.println("The main class could not be determined.");
                return false;
            }
        }
        jvmArgs.add(mainClass);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
            if (!isMac) processBuilder.environment().put("__GL_THREADED_OPTIMIZATIONS", "0");

            if (!redirectOutput) processBuilder.start();
            else processBuilder.inheritIO().start().waitFor();
        } catch (Exception e) {
            System.err.println("There was a problem restarting the JVM");
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        return true;
    }

    private static String getErrMsg(boolean isMac) {
        if (isMac) return MAC_ERR_MSG;
        else return LINUX_ERR_MSG;
    }

    private static String getErrMsg2(boolean isMac) {
        if (isMac) return MAC_ERR_MSG_2;
        else return LINUX_ERR_MSG_2;
    }

    private static long getProcessID(boolean isMac) {
        if (isMac) return LibC.getpid();
        else return UNISTD.getpid();
    }
}
