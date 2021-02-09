package util;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Shell32;

/**
 * Elevates a Java process to administrator rights if requested.
 */
public class JavaElevator {

    /**
     * Elevates the Java process started with the given arguments to administrator level.
     * 
     * @param additionalArgs The Java program arguments. Arguments to start this process will already be included.
     */
    public static void startAsAdmin(String... additionalArgs) {
            // Get the command and remove the elevation marker.
            String command = System.getProperty("sun.java.command");
            for(String arg : additionalArgs) command += " \"" + arg + "\"";

            // Get class path and default java home.
            String classPath = System.getProperty("java.class.path");
            String javaHome = System.getProperty("java.home");
            String vm = javaHome + "\\bin\\java.exe";

            // Check for alternate VM for elevation. Full path to the VM may be passed with: -Delevation.vm=...
            if (System.getProperties().contains("elevation.vm")) {
                vm = System.getProperty("elevation.vm");
            }
            String parameters = "-cp " + classPath;
            parameters += " " + command;
            System.out.println(parameters);
                Shell32.INSTANCE.ShellExecute(null, "runas", vm, parameters, null, 0);

            int lastError = Kernel32.INSTANCE.GetLastError();
            if (lastError != 0) {
                String errorMessage = Kernel32Util.formatMessageFromLastErrorCode(lastError);
                errorMessage += "\n  vm: " + vm;
                errorMessage += "\n  parameters: " + parameters;
                throw new IllegalStateException("Error performing elevation: " + lastError + ": " + errorMessage);
            }
            System.exit(0);
    }
}