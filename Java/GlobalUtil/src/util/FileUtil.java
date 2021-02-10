package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	/**
	 * Goes up in the hierarchy from the given file and returns the first file (folder) that actually exists.
	 * @param file the file who's real parent is to be found
	 * @return the first parent file that exists
	 */
	public static File getRealParentFile(File file){
		do{
			file = file.getParentFile();
		}while(file != null && !file.exists());
		return file;
	}

	/**
	 * Returns all files in the given directory and its sub directories.
	 * @param directory the directory to return its sub files
	 * @return a list of files inside the given directory or its sub directoies
	 */
	public static List<File> getFilesInDirectory(File directory){
		return getFilesInDirectory(directory, null);
	}

	/**
	 * Returns all files in the given directory and its sub directories.
	 * @param directory the directory to return its sub files
	 * @param changeListener a listener that is called when ever a new file is found and added to the list
	 * @return a list of files inside the given directory or its sub directories
	 */
	public static List<File> getFilesInDirectory(File directory, ListChangeListener<List<File>> changeListener){
		List<File> result = new ArrayList<File>();
		getFilesInDirectory(directory, changeListener, result);
		return result;
		
	}
	
	/**
	 * Gets all files in the given directory and its sub directories and appends them to the given list.
	 * @param directory the directory to return its sub files
	 * @param changeListener a listener that is called when ever a new file is found and added to the list
	 * @param addTO the list of files to append the result of this method to
	 */
	public static void getFilesInDirectory(File directory, ListChangeListener<List<File>> changeListener, List<File> addTo){
		if(directory.isFile()){
			addTo.add(directory);
			if (changeListener != null) changeListener.onChange(addTo);
		}else if (directory.isDirectory()){
			File[] subFiles = directory.listFiles();
			if(subFiles == null) return;
			for(File subFile : subFiles){
				getFilesInDirectory(subFile, changeListener, addTo);
			}
		}
	}
	
	/**
	 * Creates new File Objects similar (same parent folder, new name contains old name) to the given one (not new files on the system!) and returns it when it does not exist yet.
	 * <br/>Example: C:\test.txt (exists on file system) -> C:\test - Copy.txt (does not exist on the file system)
	 * <br/>Names tried:
	 * <ul><li>filename</li>
	 * <li>filename - Copy</li>
	 * <li>filename - Copy (2)</li>
	 * <li>filename - Copy (3)</li>
	 * <li>...</li></ul>
	 * @param toRename the file to be "renamed"
	 * @return a new file that does not exist but is in the same folder and contains the name of the given file
	 */
	public static File renameUntilNonExisting(File toRename){
		if(!toRename.exists()) return toRename;
		
		String filepath = toRename.getParent() + "\\";
		String filename = toRename.getName().substring(0, toRename.getName().lastIndexOf("."));
		String filetype = toRename.getName().substring(toRename.getName().lastIndexOf(".")); //including the "." (dot), so for example ".txt"
		
		File renamed = toRename;
		
		int i = 1;
		while(renamed.exists()){
			renamed = new File(filepath + filename + " (" + i + ")" + filetype);
			i++;
		}
		
		return renamed;
	}
	
	/**
	 * Clears the given directory, deleting all files contained it this or any subfolders.
	 * <br/>If the given directory (File instance) does not exist or is actually a file, no action will be taken.
	 * @param directory the directory to be cleared.
	 * @param deleteFolders if true, folders will be deleted too (including the given top folder), if false only files will be deleted, leaving the folder system.
	 * @return true if any files have been deleted, false otherwise.
	 */
	public static boolean clearDirectory(File directory, boolean deleteFolders){
		if(!directory.isDirectory()) return false;
		boolean deletedSomething = false;
		File[] files = directory.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File file: files) {
	            if(file.isDirectory()) {
	            	clearDirectory(file, deleteFolders);
	            } else {
	                file.delete();
	                deletedSomething = true;
	            }
	        }
	    }
	    return deletedSomething;
	}
	
	
	/**
	 * Test the given root directory any files under it with the given Predicate and adds it to the returned list if the test returns true. 
	 * @param root the directory to test from. If it is a file, only that file will be tested. If it does not exist, the returned ArrayList will be empty anyways, but no exception is thrown.
	 * @param condition a predicate that returns true, if the file should be added to the result. Works like the Predicate from Java 1.8.
	 * @return an ArrayList that contains all files in the given directory or its subdirectories, that return true in the test with the given Predicate.
	 */
	public static ArrayList<File> getFilesWhere(File root, Predicate<File> condition){
		ArrayList<File> result = new ArrayList<File>();
		if(root.isDirectory()){
			for(File child : root.listFiles()){
				result.addAll(getFilesWhere(child, condition));
			}
		}else if(root.isFile()){
			if(condition.test(root)) result.add(root);
		}
		
		return result;
	}
	
	/**
	 * Creates a new temporary directory.
	 * @param name the name of the directory
	 * @return the directory.
	 * @throws IOException if the directory cannot be created or already exist and cannot be deleted.
	 */
	public static File createTempDirectory(String name) throws IOException {
		final File temp;

		temp = File.createTempFile(name, "");

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}

		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		}

		return (temp);
	}

	/**
	 * Creates a new temporary directory with the name 'temp' plus the current nano time.
	 * @return the directory.
	 * @throws IOException if the directory cannot be created or already exist and cannot be deleted.
	 */
	public static File createTempDirectory() throws IOException {
		return createTempDirectory("temp" + System.nanoTime());
	}
	
	/**
	 * Writes the given data to the given file.
	 * @param file the file to write to
	 * @param data the data to write
	 */
	public static void write(File file, byte[] data){
		FileOutputStream fileOuputStream = null;

        try {
            fileOuputStream = new FileOutputStream(file);
            fileOuputStream.write(data);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOuputStream != null) {
                try {
                    fileOuputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
}
