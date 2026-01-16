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
// Note, the above license and copyright applies to this file only.
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
 * A helper object for game startup, featuring three utilities related to LWJGL3 on various operating systems.
 * <p>
 * The utilities are as follows:
 * <ul>
 *  <li> Windows: Prevents a common crash related to LWJGL3's extraction of shared library files.</li>
 *  <li> macOS: Spawns a child JVM process with {@code -XstartOnFirstThread} in the JVM args (if it was not already).
 *  This is required for LWJGL3 to work on macOS.</li>
 *  <li> Linux (NVIDIA GPUs only): Spawns a child JVM process with the {@code __GL_THREADED_OPTIMIZATIONS}
 *  {@link System#getenv(String) Environment Variable} set to {@code 0} (if it was not already). This is required for
 *  LWJGL3 to work on Linux with NVIDIA GPUs.</li>
 * </ul>
 * <a href="https://jvm-gaming.org/t/starting-jvm-on-mac-with-xstartonfirstthread-programmatically/57547">Based on this java-gaming.org post by kappa</a>
 * @author damios
 */
public class StartupHelper {

    private StartupHelper() {}

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    /**
     * Must only be called on Linux. Check OS first (or use short-circuit evaluation)!
     * @return whether NVIDIA drivers are present on Linux.
     */
    public static boolean isLinuxNvidia() {
        String[] drivers = new File("/proc/driver").list(
            (dir, path) -> path.toUpperCase(Locale.ROOT).contains("NVIDIA"));
        if (drivers == null) return false;
        return drivers.length > 0;
    }

    /**
     * Applies the utilities as described by {@link StartupHelper}'s Javadoc.
     * <p>
     * All {@link System#getenv() Environment Variables} are copied to the child JVM process (if it is spawned), as
     * specified by {@link ProcessBuilder#environment()}; the same applies for
     * {@link System#getProperties() System Properties}.
     * <p>
     * <b>Usage:</b>
     * <pre><code>
     * public static void main(String[] args) {
     * 	 if (StartupHelper.startNewJvmIfRequired()) return;
     * 	 // ... The rest of main() goes here, as normal.
     * }
     * </code></pre>
     * @return whether a child JVM process was spawned or not.
     */
    public static boolean startNewJvmIfRequired() {
        return startNewJvmIfRequired(true);
    }

    /**
     * Applies the utilities as described by {@link StartupHelper}'s Javadoc.
     * <p>
     * All {@link System#getenv() Environment Variables} are copied to the child JVM process (if it is spawned), as
     * specified by {@link ProcessBuilder#environment()}; the same applies for
     * {@link System#getProperties() System Properties}.
     * <p>
     * <b>Usage:</b>
     * <pre><code>
     * public static void main(String[] args) {
     *   // The parameter on the next line could instead be false if you don't want to inherit IO.
     * 	 if (StartupHelper.startNewJvmIfRequired(true)) return;
     * 	 // ... The rest of main() goes here, as normal.
     * }
     * </code></pre>
     * @param inheritIO whether I/O should be inherited in the child JVM process. Please note that enabling this will
     *                  block the thread until the child JVM process stops executing.
     * @return whether a child JVM process was spawned or not.
     */
    public static boolean startNewJvmIfRequired(boolean inheritIO) {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (osName.contains("mac")) return startNewJvm0(/*isMac =*/ true, inheritIO);
        if (osName.contains("windows")) {
            // Here, we are trying to work around an issue with how LWJGL3 loads its extracted .dll files.
            // By default, LWJGL3 extracts to the directory specified by "java.io.tmpdir": usually, the user's home.
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
        return startNewJvm0(/*isMac =*/ false, inheritIO);
    }

    private static final String MAC_JRE_ERR_MSG = "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the '-XstartOnFirstThread' argument manually!";
    private static final String LINUX_JRE_ERR_MSG = "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the environment variable '__GL_THREADED_OPTIMIZATIONS' to '0'!";
    private static final String CHILD_LOOP_ERR_MSG = "The current JVM process is a spawned child JVM process, but StartupHelper has attempted to spawn another child JVM process! This is a broken state, and should not normally happen! Your game may crash or not function properly!";

    /**
     * Spawns a child JVM process if on macOS, or on Linux with NVIDIA drivers.
     * <p>
     * All {@link System#getenv() Environment Variables} are copied to the child JVM process (if it is spawned), as
     * specified by {@link ProcessBuilder#environment()}; the same applies for
     * {@link System#getProperties() System Properties}.
     * @param isMac whether the current OS is macOS. If this is `false` then the current OS is assumed to be Linux (and
     *             an immediate check for NVIDIA drivers is performed).
     * @param inheritIO whether I/O should be inherited in the child JVM process. Please note that enabling this will
     *                 block the thread until the child JVM process stops executing.
     * @return whether a child JVM process was spawned or not.
     */
    public static boolean startNewJvm0(boolean isMac, boolean inheritIO) {
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

        // Check whether this JVM process is a child JVM process already.
        // This state shouldn't usually be reachable, but this stops us from endlessly spawning new child JVM processes.
        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println(CHILD_LOOP_ERR_MSG);
            return false;
        }

        // Spawn the child JVM process with updated environment variables or JVM args
        List<String> jvmArgs = new ArrayList<>();
        // The following line is used assuming you target Java 8, the minimum for LWJGL3.
        String javaExecPath = System.getProperty("java.home") + "/bin/java";
        // If targeting Java 9 or higher, you could use the following instead of the above line:
        //String javaExecPath = ProcessHandle.current().info().command().orElseThrow()
        if (!(new File(javaExecPath).exists())) {
            System.err.println(getJreErrMsg(isMac));
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

            if (!inheritIO) processBuilder.start();
            else processBuilder.inheritIO().start().waitFor();
        } catch (Exception e) {
            System.err.println("There was a problem restarting the JVM.");
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        return true;
    }

    private static String getJreErrMsg(boolean isMac) {
        if (isMac) return MAC_JRE_ERR_MSG;
        else return LINUX_JRE_ERR_MSG;
    }

    private static long getProcessID(boolean isMac) {
        if (isMac) return LibC.getpid();
        else return UNISTD.getpid();
    }
}
