package util.zip;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class ZipUtil {
	
	
	/**
	 * Copied from https://stackoverflow.com/a/47595502/5784265
	 * @param file the file to check
	 * @return true if it is an archive
	 */
	@SuppressWarnings("deprecation")
	public static boolean isArchive(File file) {
	    int fileSignature = 0;
	    RandomAccessFile raf = null;
	    try {
	        raf = new RandomAccessFile(file, "r");
	        fileSignature = raf.readInt();
	    } catch (IOException e) {
	        // handle if you like
	    } finally {
	        IOUtils.closeQuietly(raf);
	    }
	    return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
	}
	
	/**
	 * Lists all files in the given archive file.
	 * <br/>Returns an empty list if it fails to open the archive.
	 * <br/>No Exceptions are thrown if the file is not an archive, so use {@link #isArchive(File)} if you want to make that check first.
	 * @param archive the archive to list files of
	 * @param listDirectories if true, it will also include directories
	 * @return a list of {@link ZipEntryFile} containing all found files or empty if the archive could not be opened.
	 */
	public static List<ZipEntryFile> listFiles(File archive, boolean listDirectories){
		List<ZipEntryFile> files = new ArrayList<ZipEntryFile>();
		ZipRootFile root = new ZipRootFile(archive);
		try {
			root.load();
		} catch (Throwable e) {
			System.err.println("Failed to load archive " + archive.getAbsolutePath());
			e.printStackTrace();
		}
		files.addAll(root.getAllEntries());
		return files;
	}
}
