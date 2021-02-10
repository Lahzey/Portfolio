package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for the windows command line
 * @author A469627
 *
 */
public class CommandLineUtil {

	/**
	 * Executes the given command and returns its result.
	 * @param command the command to execute
	 * @return the result
	 * @throws IOException if an error occurs while executing the command
	 */
	public static List<String> exec(String command) throws IOException{
		Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", command});
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		List<String> lines = new ArrayList<String>();
		String line;
		while((line = stdInput.readLine()) != null){
			lines.add(line);
		}
		return lines;
	}
}
