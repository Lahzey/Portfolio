package util;

public class ProcessUtil {

	/**
	 * Replacement of Java 1.8 <code>Process.isAlive</code>.
	 * @param process the process to check
	 * @return true if still alive (running), false otherwise
	 */
	public static boolean isAlive(Process process){
	    try {
	        process.exitValue();
	        return false;
	    } catch (Exception e) {
	        return true;
	    }
	}
}
