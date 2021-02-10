package com.creditsuisse.util.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;

import com.creditsuisse.util.FileUtil;

/**
 * An entry in a zip file.
 * <br/>Even though this extends {@link File}, most file related actions do not work (like creating a {@link FileInputStream}.
 * @author A469627
 *
 */
public class ZipEntryFile extends File{
	private static final long serialVersionUID = 1L;
	
	private ZipEntry entry;
	
	private ZipRootFile root;
	private ZipEntryFile parentEntry; //
	private Map<String, ZipEntryFile> childEntries; //lazy initialized
	
	private File tempFile;
	private boolean tempFilePopulated;

	protected ZipEntryFile(ZipEntry entry, ZipRootFile root) {
		super(root.getAbsoluteFile() + "\\" + entry.getName());
		this.entry = entry;
		this.root = root;
	}
	
	private void setParentEntry(ZipEntryFile parentEntry){
		this.parentEntry = parentEntry;
	}
	
	protected void addChildEntry(ZipEntryFile childEntry){
		if(childEntries == null) childEntries = new HashMap<String, ZipEntryFile>();
		childEntries.put(childEntry.getName(), childEntry);
		childEntry.setParentEntry(this);
	}
	
	
	/**
	 * @return a ZipEntry based on which this {@link ZipEntryFile} was created
	 */
	public ZipEntry getEntry(){
		return entry;
	}
	
	/**
	 * @return the {@link ZipRootFile} for this entry
	 */
	public ZipRootFile getRoot(){
		return root;
	}
	
	/**
	 * Similar to {@link #getParentFile()} but this will always return a {@link ZipEntryFile}
	 * or null if the parent is the root of the zip.
	 * @return the parent of this entry or null if this is a child of the zip root
	 */
	public ZipEntryFile getParentEntry(){
		return parentEntry;
	}
	
	/**
	 * Returns a temporary file in the real file system representing this file.
	 * <br/>If it has not been created yet, it will create it now.
	 * @param populate if true, the file will also be filled with data.
	 * <br/>Folders will be populated by creating all children (these will not be populated)
	 * @return the file pointing to the created temporary file (this file will always exist)
	 * @throws IOException if any I/O Error occurs
	 */
	public File getTempFile(boolean populate) throws IOException{
		if(tempFile != null && !tempFile.exists()) tempFile = null;
		if(tempFile == null){
			tempFilePopulated = false;
			File tempParent = parentEntry == null ? root.getTempFile() : parentEntry.getTempFile(false);
			tempFile = new File(tempParent.getAbsolutePath() + "\\" + getName());
			tempFile.createNewFile();
		}
		if(populate && !tempFilePopulated){
			if(isDirectory()){
				for(ZipEntryFile childEntry : childEntries.values()){
					childEntry.getTempFile(false);
				}
			}else{
				FileUtil.write(tempFile, readAllBytes());
			}
			tempFilePopulated = true;
		}
		return tempFile;
	}
	
	/**
	 * Tries to get a temporary file, but will suppress any exception.
	 * @param populate if the file should be populated.
	 * @return the temporary file or <b>itself if it failed</b> to create a temporary file
	 * @see #getTempFile(boolean)
	 */
	public File getTempFileSilent(boolean populate){
		try {
			return getTempFile(populate);
		} catch (IOException e) {
			e.printStackTrace();
			return this;
		}
	}
	

	/**
	 * Reads all bytes of the file into a byte array
	 * @return a byte array containg the file content
	 * @throws IOException if an I/O error occurs
	 */
	public byte[] readAllBytes() throws IOException{
		return IOUtils.toByteArray(root.getZipFile().getInputStream(entry));
	}
	
	
	
	
	
	//File Overrides
	
	@Override
	public File getParentFile(){
		if(parentEntry != null) return parentEntry;
		else return root;
	}
	
	@Override
	public ZipEntryFile[] listFiles(){
		if(childEntries == null) return new ZipEntryFile[0];
		else return childEntries.values().toArray(new ZipEntryFile[childEntries.size()]);
	}
	
	@Override
	public boolean isDirectory(){
		if(entry != null){
			return entry.isDirectory();
		}else return false;
	}
	
	@Override
	public boolean isFile(){
		if(entry != null){
			return !entry.isDirectory();
		}else return false;
	}

	
}
