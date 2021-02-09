package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Easy to access {@link Properties} that automatically loads and stores.
 * @author A469627
 *
 */
public class Config extends Properties{
	private static final long serialVersionUID = 1L;
	
	protected File configFile;

	/**
	 * Loads the configuration file.
	 * @param configFile the file where the configuration is stored
	 */
	public Config(File configFile){
		this.configFile = configFile;
		if(!configFile.exists()){
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			FileInputStream input = new FileInputStream(configFile);
			load(input);
			input.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calls <code>super.setProperty(key, value)</code> and also stores the properties to the file passed in the constructor.
	 * <br/><br/>
	 * <b>Super Implementation JavaDoc:</b><br/>
     * Calls the <tt>Hashtable</tt> method {@code put}. Provided for
     * parallelism with the <tt>getProperty</tt> method. Enforces use of
     * strings for property keys and values. The value returned is the
     * result of the <tt>Hashtable</tt> call to {@code put}.
     *
     * @param key the key to be placed into this property list.
     * @param value the value corresponding to <tt>key</tt>.
     * @return     the previous value of the specified key in this property
     *             list, or {@code null} if it did not have one.
     * @see #getProperty
     * @since    1.2
     */
	public synchronized Object setProperty(String key, String value) {
		Object returnValue =  super.setProperty(key, value);
		try {
			store(new FileOutputStream(configFile), "");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return returnValue;
	}
	
	
}
