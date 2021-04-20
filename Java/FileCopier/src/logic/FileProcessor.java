package logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.FileUtil;
import util.LoopThread;
import util.StringFormatter;

import logic.Task.TaskType;

public class FileProcessor {
	

	public static int threadCount = 0;
	public static final int MAX_THREAD_COUNT = 50;
	
	public static final char[] FORBIDDEN_CHARS = {'/', ':', '*', '?', '"', '<', '>', '|'};
	
	
	public static CopyResult copy(File fileToCopy, File destination, boolean printProgress) {
		final CopyResult result = new CopyResult();
		result.startTime = System.currentTimeMillis();
		result.toCopy = fileToCopy;
		result.destination = destination;
		result.files = new ArrayList<>();
		File source = fileToCopy.getParentFile();
		String destinationPath = destination.getAbsolutePath();
		String destinationPrefix = destinationPath.endsWith("\\") ? destinationPath : destinationPath + "\\";
		
		new Thread() {
			
			@Override
			public void run() {
				// Setup the progress printer (purely cosmetic, not necessary)
				LoopThread progressPrinter = null;
				if(printProgress){
					progressPrinter = new LoopThread(1, 1000){
						
						long lastByteCount = 0;

						@Override
						public void loopedRun() {
							System.out.println("\n--------------------------------");
							long bytesPerSec = result.totalByteCount - lastByteCount;
							lastByteCount = result.totalByteCount;
							
							System.out.println("Copied: " + result.successCount);
							System.out.println("Failed: " + result.failedCount);
							System.out.println("Open Threads: " + threadCount);
							System.out.println(StringFormatter.formatByteCount(bytesPerSec) + "/s");
							if(result.finishedListing){
								System.out.println(StringFormatter.formatNumber((result.successCount + result.failedCount) / (float) result.files.size() * 100, "0.1") + "%");
							} else {
								System.out.println("Calculating size...");
								System.out.println("(Processed: " + (result.successCount + result.failedCount) + " | Listed: " + result.files.size() + ")");
							}
							System.out.println("--------------------------------\n");
						}
						
					};
					progressPrinter.start();
				}
				
				
				// Setup a thread to manage all threads that are processing files
				LoopThread processingThreadManager = new LoopThread(10){

					@Override
					public void loopedRun() {
						while(result.files.size() > result.currentIndex && threadCount < MAX_THREAD_COUNT){
							if(result.cancelRequested){
								List<File> cancelResult = new ArrayList<>(result.currentIndex);
								for(int i = 0; i < result.currentIndex; i++){
									cancelResult.add(result.files.get(i));
								}
								result.files = cancelResult;
								result.finishedListing = true;
								terminate();
								return;
							}else{
								new ProcessingThread(result, source, destinationPrefix, printProgress).start();
							}
						}
					}
				};
				processingThreadManager.start();
				
				// Find all files in the given directory
				FileUtil.getFilesInDirectory(fileToCopy, null, result.files);
				
				result.finishedListing = true;
				
				while(result.currentIndex < result.files.size()){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// does not matter
					}
				}
				
				if(progressPrinter != null) progressPrinter.terminate();
				processingThreadManager.terminate();
				
				if(printProgress){
					long time = System.currentTimeMillis() - result.startTime;
					double seconds = time / 1000d;
					System.out.println("Copy complete after " + StringFormatter.formatNumber(seconds, "0.1") + " seconds");
					System.out.println("with an average speed of " + StringFormatter.formatByteCount((long) (result.totalByteCount / seconds)) + "/s");
					System.out.println();
					System.out.println("Processed " + result.files.size() +" files (" + StringFormatter.formatByteCount(result.totalByteCount) + ")");
					System.out.println("Sucessful: " + result.successCount + " | Failed: " + result.failedCount);
				}
			}
		}.start();
		return result;
	}
	

	private static void copyFileUsingStream(File source, File dest, CopyResult result, boolean printExceptions) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
                result.totalByteCount += length;
            }
            result.successCount++;
        } catch (Exception ex) {
        	if(printExceptions) ex.printStackTrace();
        	result.exceptions.add(ex);
        	result.failedCount++;
        } finally {
            try {
                is.close();
                os.close();
            } catch (Exception ex) {
            }
        }
    }
	
	
	private static class ProcessingThread extends LoopThread {
		
		private CopyResult result;
		private File source;
		private String destinationPrefix;
		private boolean printProgress;
		
		public ProcessingThread(CopyResult result, File source, String destinationPrefix, boolean printProgress){
			this.result = result;
			this.source = source;
			this.destinationPrefix = destinationPrefix;
			this.printProgress = printProgress;
		}
		
		@Override
		public void start(){
			threadCount++;
			super.start();
		}
		
		@Override
		public void run(){
			super.run();
			threadCount--;
		}

		@Override
		public void loopedRun() {
			final int currentIndex;
			synchronized(result){
				if(result.files.size() > result.currentIndex && !result.cancelRequested){
					currentIndex = result.currentIndex;
					result.currentIndex++;
				}else{
					terminate();
					return;
				}
			}
			File file = result.files.get(currentIndex);
			File newFile;
			if(source != null){
				newFile = new File(destinationPrefix + source.toURI().relativize(file.toURI()).getPath());
			}else{
				newFile = new File(destinationPrefix + removeForbiddenChars(file.getAbsolutePath()));
			}
			newFile.getParentFile().mkdirs();
			copyFileUsingStream(file, newFile, result, printProgress);
			
		}
		
	}
	
	
	
	public static int getThreadCount(){
		return threadCount;
	}
	
	public static class CopyResult extends TaskResult {
		public File toCopy;
		public File destination;
		
		public List<File> files;
		
		public int successCount = 0;
		public int failedCount = 0;
		
		public int currentIndex = 0;
		
		public long totalByteCount = 0;
		
		public long startTime;
		public boolean finishedListing = false;
		
		public boolean cancelRequested = false;
		
		public List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
		
		public boolean isFinished(){
			return finishedListing && files.size() == currentIndex;
		}
	}
	
	
	private static String removeForbiddenChars(String string){
		for(char c : FORBIDDEN_CHARS){
			string = string.replace(c + "", "");
		}
		return string;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static TaskResult performAsync(TaskType taskType, File source, File destination) {
		final TaskResult result;
		final TaskPerformer taskPerformer;
		switch(taskType){
		case COPY:
			result = new CopyResult();
			File sourceParent = source.getParentFile();
			String destinationPath = destination.getAbsolutePath();
			String destinationPrefix = destinationPath.endsWith("\\") ? destinationPath : destinationPath + "\\";
			taskPerformer = new TaskPerformer() {
				
				@Override
				public void perform(File file) {
					File newFile;
					if(sourceParent != null){
						newFile = new File(destinationPrefix + sourceParent.toURI().relativize(file.toURI()).getPath());
					}else{
						newFile = new File(destinationPrefix + removeForbiddenChars(file.getAbsolutePath()));
					}
					newFile.getParentFile().mkdirs();
					copyFileUsingStream(file, newFile, (CopyResult) result, false);
				}
			};
			break;
		case DELETE:
			result = new TaskResult();
			taskPerformer = new TaskPerformer() {
				
				@Override
				public void perform(File file) {
					file.delete();
				}
			};
			break;
		default:
			return null;
		}
		result.taskType = taskType;
		result.source = source;
		result.destination = destination;
		result.startTime = System.currentTimeMillis();
		result.listedFiles = new ArrayList<>();
		
		new Thread() {
			
			@Override
			public void run() {
				// Setup the processor which will take the discovered files and process them
				LoopThread processor = new LoopThread(10){

					@Override
					public void loopedRun() {
						while(result.listedFiles.size() > result.currentIndex && threadCount < MAX_THREAD_COUNT){
							if(result.cancelRequested){
								List<File> cancelResult = new ArrayList<>(result.currentIndex);
								for(int i = 0; i < result.currentIndex; i++){
									cancelResult.add(result.listedFiles.get(i));
								}
								result.listedFiles = cancelResult;
								result.finishedListing = true;
								return;
							}
							threadCount++;
							final int index = result.currentIndex;
							result.currentIndex++;
							new Thread() {
								
								@Override
								public void run() {
									File file = result.listedFiles.get(index);
									taskPerformer.perform(file);
									threadCount--;
								}
							}.start();
						}
					}
				};
				processor.start();
				
				// Find all files in the given directory
				FileUtil.getFilesInDirectory(source, null, result.listedFiles);
				
				result.finishedListing = true;
				
				while(result.currentIndex < result.listedFiles.size()){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// does not matter
					}
				}
				
				if(taskType == TaskType.DELETE){
					try {
						Runtime runtime = Runtime.getRuntime();
						runtime.exec("RMDIR /Q/S \"" + source.getAbsolutePath() + "\"");
					} catch (IOException e) {
						result.exceptions.add(e);
					}
				}
				
				processor.terminate();
			}
		}.start();
		
		return result;
	}
	
	public static class TaskResult {
		public TaskType taskType;
		public File source;
		public File destination;
		
		public List<File> listedFiles;
		
		public int successCount = 0;
		public int failedCount = 0;
		
		public int currentIndex = 0;
		
		public long startTime;
		public boolean finishedListing = false;
		
		public boolean cancelRequested = false;
		
		public List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
		
		public boolean isFinished(){
			return finishedListing && listedFiles.size() == currentIndex;
		}
	}
	
	private static interface TaskPerformer {
		public void perform(File file);
	}
}
