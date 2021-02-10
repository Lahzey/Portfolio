package search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * An Index capable of remembering all files present at a given location.
 * <br/>Useful to speed up file searches (cause windows is really slow).
 * @author A469627
 *
 */
public class Index {
	
	public static final String INDEX_FILE_NAME = "java.searchindex";
	private static final String CREATED_AT_PREFIX = "#C:";
	private static final String FILE_PREFIX = "#F:";
	
	/**
	 * Used while indexing to store folder contents to be indexed after all files in current folder are indexed
	 */
	private final List<File> indexChildList = new ArrayList<>();

	public final File location;
	public final File indexFile;
	public final List<File> files = new ArrayList<>();
	public long createdAt;
	
	private boolean cancelRequested;
	
	/**
	 * Creates an Index object, but does not perform any indexing yet.
	 * @param location the location that should be indexed (must be a directory)
	 */
	public Index(File location){
		if(!location.isDirectory()) throw new IllegalArgumentException("Given file " + location.getAbsolutePath() + " does not exist or is not a directory.");
		this.location = location;
		indexFile = new File(location.getAbsolutePath() + "/" + INDEX_FILE_NAME);
	}
	
	/**
	 * Scans through the index location and lists all folders and files found.
	 * @param callback will be called every time a file is indexed
	 */
	public void generate(IndexingCallback callback){
		files.clear();
		index(callback, location.listFiles());
		if(cancelRequested) cancelRequested = false;
		else createdAt = System.currentTimeMillis();
	}
	
	/**
	 * Cancels the current action (loading or generating), but cannot cancel two actions at once.
	 * <br/>If loading, the file will be loaded completely, but will cancel as soon as parsing begins.
	 */
	public void cancel(){
		cancelRequested = true;
	}
	
	/**
	 * Checks whether the index already exists on the file system.
	 * @return
	 */
	public boolean exists(){
		return indexFile.isFile();
	}
	
	/**
	 * Loads the index from a file in the file system.
	 * <br/>Only works if an index file with the correct name is present in the indexes location.
	 * @param callback called when a file is loaded from the index
	 * @return true if an index could be loaded, false otherwise
	 */
	public boolean load(IndexingCallback callback){
		try {
			List<File> newFiles = new ArrayList<>();
			List<String> lines = Files.readAllLines(indexFile.toPath());
			createdAt = Long.parseLong(lines.get(0).substring(CREATED_AT_PREFIX.length()));
			for(int i = 1; i < lines.size(); i++){
				String line = lines.get(i);
				if(line.startsWith(FILE_PREFIX)){ //Files check first because files are more common so normally only 1 check is made
					File file = new File(line.substring(FILE_PREFIX.length()));
					newFiles.add(file);
					if(!cancelRequested) callback.onIndex(file);
					else{
						cancelRequested = false;
						return false;
					}
				}
			}
			files.clear();
			files.addAll(newFiles);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Gets the time this index was created at.
	 * <br/>If the index is not loaded yet, it will try to load the creation time only.
	 * @return the creation time or 0 if it could not be loaded.
	 */
	public long getCreatedAt(){
		if(createdAt == 0){
			if(exists()){
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(indexFile));
					createdAt = Long.parseLong(reader.readLine().substring(CREATED_AT_PREFIX.length()));
				} catch (Exception e) {
					System.err.println("Failed to load createdAt from " + indexFile.getAbsolutePath());
				} finally {
					try {
						if(reader != null) reader.close();
					} catch (IOException e) {}
				}
			}
		}
		return createdAt;
	}
	
	/**
	 * Saves the index to a file in the index location.
	 */
	public void save(){
		try {
			List<String> lines = new ArrayList<>();
			lines.add(CREATED_AT_PREFIX + createdAt);
			for(File file : files){
				lines.add(FILE_PREFIX + file.getAbsolutePath());
			}
			Files.write(indexFile.toPath(), lines);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save index to file", e);
		}
		
	}
	
	/**
	 * Indexes the given files.
	 * <br/>If there are any folders, it will also index their children (recursive).
	 * <br/>This will index all current files before indexing children.
	 * @param callback will be called every time a file is indexed
	 * @param toIndex the file to index
	 */
	private void index(IndexingCallback callback, File... toIndex){
		if(cancelRequested) return;
		synchronized(indexChildList){
			indexChildList.clear();
			for(File file : toIndex){
				if(file.isDirectory()){
					for(File child : file.listFiles()){
						indexChildList.add(child);
					}
				}else{
					if(!file.getName().equals(INDEX_FILE_NAME)){
						files.add(file);
						if(callback != null && !cancelRequested) callback.onIndex(file);
					}
				}
			}
			if(indexChildList.size() > 0) index(callback, indexChildList.toArray(new File[indexChildList.size()]));
		}
	}
	
	
	public static interface IndexingCallback{
		public void onIndex(File file);
	}
	
}
