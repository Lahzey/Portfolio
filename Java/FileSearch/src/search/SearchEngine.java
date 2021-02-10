package search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.LoopThread;
import util.RegexUtil;
import util.zip.ZipEntryFile;
import util.zip.ZipUtil;

import search.Index.IndexingCallback;
import search.SearchEngine.SearchCallback.Status;
import search.data.Search;
import search.data.SearchResult;

/**
 * This class can perform a {@link Search} and will put it into a {@link #history}.
 * @author A469627
 *
 */
public class SearchEngine {
	
	public static final String IGNORED_CHARS_REGEX = "[\\s-_]";

	private static List<Search> history = new ArrayList<>();
	
	private static Index currentIndex;
	private static QueuedFileFilter currentFilter;

	
	
	/**
	 * Performs the given search.
	 * <br/>This task will take some time so it is recommended to call this method within a separate thread.
	 * <br/>The search will be put into a {@link #history}.
	 * @param search the search to perform
	 * @param callback called to prompt the user
	 */
	public static void performSearch(Search search, SearchCallback callback){
		//Add search to history
		history.add(search);
		
		callback.onStatusChange(Status.SCAN_FOR_INDEX);
		
		//Check if index available
		boolean indexGenerated = false;
		currentIndex = new Index(search.scope);
		if(currentIndex.exists()){
			Date createdAt = new Date(currentIndex.getCreatedAt());
			if(callback != null){
				//Prompt user for index usage
				if(callback.prompt("Stored index available from " + createdAt + ".\nDo you want to use it?")){
					indexGenerated = true;
				}
			}
		}
		
		callback.onStatusChange(Status.INITIALIZING);
		
		//Create filter to check all files in the correct order
		currentFilter = new QueuedFileFilter(search, callback);
		
		//Create callback for indexing to be called whenever a file is found
		IndexingCallback indexingCallback = new IndexingCallback() {
			
			@Override
			public void onIndex(File file) {
				currentFilter.add(file);
				if(callback != null && (search.namePattern.isEmpty() || search.containingText.isEmpty())) callback.onIndex(file);
			}
		};
		
		//Start the file filter
		currentFilter.start();
		
		//Generate or load index and filter it (will return as soon as all files have been indexed)
		if(indexGenerated){
			callback.onStatusChange(Status.LOADING_INDEX);
			currentIndex.load(indexingCallback);
		}else{
			callback.onStatusChange(Status.GENERATING_INDEX);
			currentIndex.generate(indexingCallback);
			if(search.storeIndex) currentIndex.save();
		}
		currentIndex = null;
		
		callback.onStatusChange(Status.FILTERING_FILES);
		
		//Wait for file filter to finish
		currentFilter.finish();
		currentFilter = null;
		
		callback.onStatusChange(Status.DONE);
	}
	
	public static void cancelSearch(){
		if(currentIndex != null) currentIndex.cancel();
		if(currentFilter != null) currentFilter.cancel();
	}
	
	public static abstract class SearchCallback{
		
		private int indexedCount = 0;
		private int processedCount = 0;

		private final void onProcess(File file){
			processedCount++;
			onProgressChange(processedCount);
			
		}
		
		private final void onIndex(File file){
			indexedCount++;
			onMaxProgressChange(indexedCount);
		}
		
		public abstract boolean prompt(String question);
		public abstract void onFind(File file, int matchIndex);
		public abstract void onStatusChange(Status status);
		public abstract void onProgressChange(int progress);
		public abstract void onMaxProgressChange(int maxProgress);
		
		public enum Status {
			SCAN_FOR_INDEX("Scanning for existing index..."),
			INITIALIZING("Initializing search..."),
			LOADING_INDEX("Loading index and filtering files..."),
			GENERATING_INDEX("Generating index and filtering files..."),
			FILTERING_FILES("Filtering files..."),
			DONE("Search finished"),
			CANCELLED("Search cancelled");
			
			private String message;

			private Status(String message){
				this.message = message;
			}
			
			public String toString(){
				return message;
			}
		}
	}
	
	
	/**
	 * An object of this class can serve as a queue for files found by indexing and later filter those files with the criteria of a given search.
	 * <br/>As soon as {@link #start()} is called, a new thread will constantly take files from the queue,
	 * check them with the search criteria and if it is met, add them to the search results.
	 * <br/>By calling {@link #finish()} the thread will continue until both queues are empty and then terminate.
	 * <br/>This Filter may be started and stopped multiple times.
	 * @author A469627
	 *
	 */
	private static class QueuedFileFilter{
	
		private LoopThread thread;
		private Search search;
		private SearchResult result;
		private SearchCallback callback;
		
		//Saved values for performance
		private boolean checkContent;
		private Pattern namePattern;
		private Pattern contentPattern;
		
		
		//Queues
		private final List<File> nameQueue = Collections.synchronizedList(new ArrayList<>());
		private final List<File> contentQueue = Collections.synchronizedList(new ArrayList<>());
		private final List<File> archiveQueue = Collections.synchronizedList(new ArrayList<>());
		
		private boolean isScanningArchive = false;
		
		/**
		 * Creates a new QueuedFileFilter with given search and callback.
		 * @param search the search to filter with (using its criteria and adding to its results) <b>MAY NOT BE NULL</b>
		 * @param callback the callback to call whenever a file matching the criteria is found (may be null)
		 */
		public QueuedFileFilter(Search search, SearchCallback callback){
			if(search == null) throw new IllegalArgumentException("Search may not be null");
			this.callback = callback;
			this.search = search;
			result = search.result;
			
			checkContent = !search.containingText.isEmpty();
			
			int namePatternFlags = Pattern.DOTALL;
			int contentPatternFlags = Pattern.DOTALL;
			String nameRegex = null;
			String contentRegex = null;
			
			if(!search.nameCase) namePatternFlags += Pattern.CASE_INSENSITIVE;
			if(!search.containingCase) contentPatternFlags += Pattern.CASE_INSENSITIVE;
			
			if(search.nameRegex) nameRegex = search.namePattern;
			else{
				for(String patternPart : search.namePattern.split(",")){
					patternPart = RegexUtil.wildcardToRegex(patternPart.trim());
					if(nameRegex != null) nameRegex += "|" + patternPart;
					else nameRegex = "(" + patternPart;
				}
				nameRegex += ")";
			}
			
			if(search.containingRegex) contentRegex = search.containingText;
			else contentRegex = RegexUtil.wildcardToRegex(search.containingText);
			
			namePattern = Pattern.compile(nameRegex, namePatternFlags);
			contentPattern = Pattern.compile(contentRegex, contentPatternFlags);
		}
		
		/**
		 * Adds a file to the name queue
		 * <br/>Once it passes the name queue, it will be added to the content queue
		 * @param file the file to add
		 */
		public void add(File file){
			if(search.searchArchives && ZipUtil.isArchive(file)) archiveQueue.add(file);
			nameQueue.add(file);
		}
		
		/**
		 * Starts a new thread to constantly filter all files in the queues.
		 * <br/>If a thread is already running, it will be terminated first.
		 */
		public void start(){
			if(thread != null && thread.isRunning()) thread.terminate();
			thread = new LoopThread() {
				
				@Override
				public void loopedRun() {
					if(!archiveQueue.isEmpty() && !isScanningArchive){
						File toRead = archiveQueue.remove(0);
						readArchive(toRead);
					}else if(!nameQueue.isEmpty()){
						File toCheck = nameQueue.remove(0);
						checkName(toCheck);
					}else if(!contentQueue.isEmpty()){
						File toCheck = contentQueue.remove(0);
						checkContent(toCheck);
						if(callback != null) callback.onProcess(toCheck);
					}else sleepSilent(100);
				}
			};
			thread.start();
		}
		
		/**
		 * Checks if the name of the given file matches the search.
		 * @param file the file to check
		 */
		private void checkName(File file){
			String fileName = file.getName();
			Matcher matcher = namePattern.matcher(fileName);
			while(matcher.find()){
				if(checkContent){
					contentQueue.add(file);
					if(!search.namePattern.isEmpty() && !search.containingText.isEmpty()) callback.onIndex(file);
					return;
				}else{
					String match = matcher.group();
					int matchIndex = fileName.indexOf(match);
					onFind(file, fileName, matchIndex, matchIndex + match.length());
				}
			}
			if(callback != null && (search.namePattern.isEmpty() || search.containingText.isEmpty())) callback.onProcess(file);
		}
		
		/**
		 * Checks if the content of the given file matches the search.
		 * @param file the file to check
		 */
		private void checkContent(File file){
			if(file.isFile() || file instanceof ZipEntryFile){
				try {
					String fileContent;
					if(file instanceof ZipEntryFile) fileContent = new String(((ZipEntryFile) file).readAllBytes());
					else fileContent = new String(Files.readAllBytes(file.toPath()));
					Matcher matcher = contentPattern.matcher(fileContent);
					while(matcher.find()){
						String match = matcher.group();
						int matchIndex = fileContent.indexOf(match);
						onFind(file, fileContent, matchIndex, matchIndex + match.length());
					}
				} catch (IOException e) {
					System.out.println("Failed to read contents of " + file.getAbsolutePath() + ":");
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Reads out the given archive and adds contained files it to the name queue
		 * @param file the archive to read
		 */
		private void readArchive(File file){
			isScanningArchive = true;
			new Thread(){
				public void run(){
					for(File zipEntry : ZipUtil.listFiles(file, false)){
						nameQueue.add(zipEntry);
						if(callback != null) callback.onIndex(zipEntry);
					}
					isScanningArchive = false;
				}
			}.start();
		}
		
		/**
		 * Waits until both queues are empty, then terminates the thread and returns.
		 * <br/>If no thread is running, this will return immediately.
		 */
		public void finish(){
			if(thread != null && thread.isRunning()){
				while(!archiveQueue.isEmpty() || isScanningArchive || !nameQueue.isEmpty() || !contentQueue.isEmpty()){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				if(thread != null){
					thread.terminate();
					thread = null;
				}
			}
		}
		
		/**
		 * Clears all queues and terminates the thread.
		 */
		public void cancel(){
			archiveQueue.clear();
			nameQueue.clear();
			contentQueue.clear();
			finish();
		}
		
		/**
		 * Adds the file to the search results and calls the callback
		 * @param file the file that has been found (that matches the search criteria)
		 * @param fileContent the content of the file scanned or null if only the name has been checked
		 * @param index the index at which the content was found
		 */
		private void onFind(File file, String content, int startIndex, int endIndex){
			int matchIndex = result.addMatch(file, content, startIndex, endIndex);
			if(callback != null){
				callback.onFind(file, matchIndex);
			}
		}
	}
}
