package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CancellationException;

import com.google.gson.Gson;

import data.FileInfo;
import gui.OverwritePrompt;

public class Engine {

	public static final String ENCODED_FILE_EXTENSION = "encoded";
	private static final int BUFFER_SIZE = 1024 * 1024;
	
	public static void process(List<File> files, Options options, ProgressCallback callback){
		Map<File, ProgressCallback> subCallbacks = new HashMap<>();
		Map<File, Long> currentByteCounts = new HashMap<>();
		Map<File, Long> totalByteCounts = new HashMap<>();
		
		// prepared data for encoding
		List<File> encodeQueue = new ArrayList<>(); // <source>

		// prepared data for decoding
		List<File> toPrompt = new ArrayList<>(); // <destination>
		Map<File, Map<Integer, File>> decodeQueue = new HashMap<>(); // <destination, <index, source>>
		
		for(File file : files){
			if(callback != null){
				subCallbacks.put(file, new ProgressCallback() {
					
					@Override
					public void onProgress(long currentByteCount, long totalByteCount) {
						if(currentByteCount > -1) currentByteCounts.put(file, currentByteCount);
						if(totalByteCount > -1) totalByteCounts.put(file, totalByteCount);
						
						long currentByteCountSum = 0;
						for(Long val : currentByteCounts.values()){
							currentByteCountSum += val;
						}
						long totalByteCountSum = 0;
						for(Long val : totalByteCounts.values()){
							totalByteCountSum += val;
						}
						callback.onProgress(currentByteCountSum, totalByteCountSum);
					}
					
					@Override
					public void onError(File file, Exception error) {
						callback.onError(file, error);
					}
					
					@Override
					public void onCancel() {
						callback.onCancel();
					}
				});
			}
			
			if(isEncoded(file)){
				FileInfo fileInfo;
				try {
					fileInfo = loadFileInfo(file);
					if(subCallbacks.containsKey(file)){
						subCallbacks.get(file).onProgress(0, file.length());
					}
					File destination = new File(file.getParentFile().getAbsolutePath() + "\\" + fileInfo.name);
					
					// handle overwrite prompt
					if(destination.exists() && !toPrompt.contains(destination)) toPrompt.add(destination);
					
					// add to decode queue
					Map<Integer, File> sources = decodeQueue.get(destination);
					if(sources == null){
						sources = new HashMap<>();
						decodeQueue.put(destination, sources);
					}
					sources.put(fileInfo.partIndex, file);
				} catch (IOException e) {
					e.printStackTrace();
					if(subCallbacks.containsKey(file)){
						subCallbacks.get(file).onError(file, e);
					}
				}
			}else{
				encodeQueue.add(file);
			}
		}
		
		// prompt for overwrite and remove any elements not confirmed (or cancel if user chooses to do so)
		try{
			List<File> confirmedOverwrites = promptForOverwrite(toPrompt);
			for(File toOverwrite : toPrompt){
				if(!confirmedOverwrites.contains(toOverwrite)){
					for(File source : decodeQueue.get(toOverwrite).values()){
						if(subCallbacks.containsKey(source)){
							subCallbacks.get(source).onProgress(0, 0);
						}
					}
					decodeQueue.remove(toOverwrite);
					
				}
			}
		} catch(CancellationException e) {
			callback.onCancel();
			return;
		}
		
		// process encode queue
		for(File source : encodeQueue){
			encode(source, options, subCallbacks.get(source));
		}
		
		// process decode queue
		for(File destination : decodeQueue.keySet()){
			decode(destination, decodeQueue.get(destination), subCallbacks);
		}
	}
	
	private static List<File> promptForOverwrite(List<File> toPrompt) throws CancellationException {
		List<File> confirmed = new ArrayList<File>();
		for(int i = 0; i < toPrompt.size(); i++){
			File toOverwrite = toPrompt.get(i);
			int userSelection = OverwritePrompt.show(toOverwrite, toPrompt.size() - i, null);
			
			switch (userSelection) {
			case OverwritePrompt.CANCEL:
				throw new CancellationException();
			case OverwritePrompt.YES_ALL:
				confirmed.addAll(toPrompt.subList(i, toPrompt.size()));
				return confirmed;
			case OverwritePrompt.YES:
				confirmed.add(toOverwrite);
			case OverwritePrompt.NO_ALL:
				return confirmed;
			case OverwritePrompt.NO:
			default:
				continue;
			}
		}
		
		return confirmed;
	}
	
	private static void decode(File destination, Map<Integer, File> sources, Map<File, ProgressCallback> callbacks){
		InputStream is = null;
        OutputStream os = null;
        try {
            os = new FileOutputStream(destination);
            for(int sourceIndex = 0; sourceIndex < sources.size(); sourceIndex++){
            	File source = sources.get(sourceIndex);
            	if(source == null){
            		os.close();
            		throw new IllegalArgumentException("Part " + (sourceIndex + 1) + " of " + destination.getName() + " is missing.");
            	}
            	
            	ProgressCallback callback = callbacks.get(source);
            	long sourceLength = source.length();
            	callback.onProgress(0, sourceLength);
            	

            	is = new FileInputStream(source);
        		long readByteCount = 0;

            	// read until data start
            	byte[] dataStart = FileInfo.DATA_START.getBytes(FileInfo.CHARSET);
            	int dataStartIndex = 0;
            	int nextByte;
            	while((nextByte = is.read()) != -1){
            		byte b = (byte) nextByte;
            		if(b == dataStart[dataStartIndex]){
            			dataStartIndex++;
            			if(dataStartIndex == dataStart.length){
            				break;
            			}
            		}else{
            			dataStartIndex = 0;
            		}
            	}

            	// continue read (from data start)
        		byte[] buffer = new byte[BUFFER_SIZE];
        		int bufferLength;
        		while((bufferLength = is.read(buffer)) != -1) {
        			byte[] toWrite = buffer;
                	if(bufferLength < buffer.length) toWrite = Arrays.copyOf(buffer, bufferLength);
                	os.write(toWrite);
	                readByteCount += bufferLength;
	                if(callback != null) callback.onProgress(readByteCount, -1);
	            }
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        	for(File source : sources.values()){
        		if(callbacks.containsKey(source)) callbacks.get(source).onError(source, ex);
        	}
            destination.delete();
            return;
        } finally {
            try {
                is.close();
                os.close();
            } catch (Exception ex) {
            }
        }
	}
	
	private static void encode(File file, Options options, ProgressCallback callback){
        long processedSourceLength = 0;
		InputStream is = null;
        OutputStream os = null;
        long sourceLength = file.length();
        long maxLength = options.maxFileSize > 0 ? options.maxFileSize : Long.MAX_VALUE;
        Map<FileInfo, File> destinations = new HashMap<>();

        if(callback != null) callback.onProgress(0, sourceLength);
        
        try {
        	// part iteration
        	boolean generateMultipleParts = maxLength < sourceLength;
            long fileLength = 0;
            FileInfo currentPart = new FileInfo();
            currentPart.id = new Random().nextLong();
            currentPart.name = file.getName();
            currentPart.partIndex = 0;
            destinations.put(currentPart, currentPart.generateEncodeDestination(file, generateMultipleParts));
            
            
            Gson gson = new Gson();
            os = new FileOutputStream(destinations.get(currentPart));
            byte[] header = (gson.toJson(currentPart) + "\n" + FileInfo.DATA_START).getBytes();
            os.write(header);
        	
            is = new FileInputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bufferLength;
            while ((bufferLength = is.read(buffer)) > 0) {
            	// trim array to all bytes truly read (removing trailing nulls)
            	byte[] toWrite = buffer;
            	if(bufferLength < buffer.length) toWrite = Arrays.copyOf(buffer, bufferLength);
            	
            	// switch part once file size has been reached
            	if(fileLength + toWrite.length > maxLength){
            		int remainingLength = (int) (maxLength - fileLength);
            		byte[] remainingBytes = Arrays.copyOf(toWrite, remainingLength);
            		byte[] overflowedBytes = Arrays.copyOfRange(toWrite, remainingLength, toWrite.length);

                    os.write(remainingBytes);
                    os.close();
                    fileLength = 0;
                    toWrite = overflowedBytes;
                    
                    if(toWrite.length > 0){
                    	currentPart = new FileInfo(currentPart);
                    	currentPart.partIndex++;
                    	destinations.put(currentPart, currentPart.generateEncodeDestination(file, generateMultipleParts));
                    	os = new FileOutputStream(destinations.get(currentPart));
                    	header = (gson.toJson(currentPart) + "\n" + FileInfo.DATA_START).getBytes();
                        os.write(header);
                    } else {
                    	continue;
                    }
            	}

            	
            	// write the bytes
                os.write(toWrite);
                fileLength += toWrite.length;
                processedSourceLength += bufferLength;
                if(callback != null) callback.onProgress(processedSourceLength, -1);
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
            if(callback != null) callback.onError(file, ex);
            for(File destination : destinations.values()) destination.delete();
            return;
        } finally {
            try {
            	if(is != null) is.close();
                if(os != null) os.close();
            } catch (Exception ex) {
            }
        }
	}
	
	
	public static FileInfo loadFileInfo(File file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    StringBuilder header = new StringBuilder();
		    char lastDataStartChar = FileInfo.DATA_START.charAt(FileInfo.DATA_START.length() - 1);
		    int nextChar;
		    while ((nextChar = br.read()) != -1) {
		    	char c = (char) nextChar;
		    	header.append(c);
		    	if(c == lastDataStartChar){
		    		String headerString = header.toString();
		    		if(headerString.endsWith(FileInfo.DATA_START)){
		    			headerString = headerString.substring(0, headerString.length() - FileInfo.DATA_START.length());
			    		Gson gson = new Gson();
			    		return gson.fromJson(headerString, FileInfo.class);
		    		}
		    	}
		    }
		}
		return null;
	}
	
	public static boolean isEncoded(File file){
		return file.getName().matches(".*\\." + ENCODED_FILE_EXTENSION);
	}
	
	
	
	public static class Options {
		
		public long maxFileSize = 0;
	}
	
	public static interface ProgressCallback {
		public void onProgress(long currentByteCount, long totalByteCount);
		public void onCancel();
		public void onError(File file, Exception error);
	}

}
