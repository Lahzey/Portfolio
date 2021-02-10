package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A quick access for storing objects to files as JSON using the GSON library.
 * @author A469627
 *
 */
public class ObjectStore<T> {

	private Class<T> clazz;
	private File location;
	private boolean prettyPrint;

	public ObjectStore(Class<T> clazz, File location, boolean prettyPrint){
		this.clazz = clazz;
		this.location = location;
		this.prettyPrint = prettyPrint;
	}

	public ObjectStore(Class<T> clazz, File location){
		this(clazz, location, true);
	}
	
	public T get(){
		if(!location.exists()) return null;
		FileReader reader = null;
		try{
			reader = new FileReader(location);
			Gson gson = new Gson();
			return gson.fromJson(reader, clazz);
		}catch(Exception e){
			throw new RuntimeException("Failed to load object from store: " + location.getAbsolutePath(), e);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public T getOrDefault(T defaultValue){
		T value = get();
		return value == null ? defaultValue : value;
	}
	
	public void store(T value){
		FileOutputStream writer = null;
		try{
			if(!location.exists()){
				location.getParentFile().mkdirs();
				location.createNewFile();
			}
			writer = new FileOutputStream(location);
			Gson gson = prettyPrint ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
			String json = gson.toJson(value);
			writer.write(json.getBytes());
		}catch(Exception e){
			throw new RuntimeException("Failed to load object from store: " + location.getAbsolutePath(), e);
		}finally{
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
