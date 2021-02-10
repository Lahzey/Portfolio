package data;

import java.io.File;
import java.nio.charset.Charset;

import logic.Engine;

public class FileInfo {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final String DATA_START = "|%DATA%|";

	public long id;
	public String name;
	
	public int partIndex = 0;
	
	public FileInfo(){
		
	}
	
	public FileInfo(FileInfo copyOf){
		id = copyOf.id;
		name = copyOf.name;
		partIndex = copyOf.partIndex;
	}
	
	public File generateEncodeDestination(File source, boolean multiPart){
		return new File(source.getParentFile().getAbsolutePath() + "\\" + name + (multiPart ? " (" + (partIndex + 1) + ")" : "") + "." + Engine.ENCODED_FILE_EXTENSION);
	}
}
