package logic;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

public class Task {
	
	public final TaskType type;
	public final List<File> params;
	public final boolean start;
	
	public Task(TaskType type, List<File> params, boolean start){
		this.type = type;
		this.params = params;
		this.start = start;
	}
	
	
	
	public static enum TaskType {
		
		COPY(true, true, JFileChooser.FILES_AND_DIRECTORIES, true, false, JFileChooser.DIRECTORIES_ONLY),
		DELETE(true, true, JFileChooser.FILES_AND_DIRECTORIES, false, false, 0);
		
		
		public final boolean hasSource;
		public final boolean multiSource;
		public final int sourceSelectionMode;
		public final boolean hasDestination;
		public final boolean multiDestination;
		public final int destinationSelectionMode;
		
		private TaskType(boolean hasSource, boolean multiSource, int sourceSelectionMode, boolean hasDestination, boolean multiDestination, int destinationSelectionMode){
			this.hasSource = hasSource;
			this.multiSource = multiSource;
			this.sourceSelectionMode = sourceSelectionMode;
			this.hasDestination = hasDestination;
			this.multiDestination = multiDestination;
			this.destinationSelectionMode = destinationSelectionMode;
		}
		
	}
	
	
}
