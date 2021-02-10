package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.kordamp.ikonli.swing.IkonResolver;

import gui.CopyPanel;
import logic.Task;
import logic.Task.TaskType;

public class FileCopier {

	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// does not matter
			System.err.println("Failed to set look and feel, application will look ugly.");
		}
		
		TaskType taskType = null;
		List<File> files = new ArrayList<>();
		boolean start = false;
		for(String arg : args){
			boolean processed = false;
			for(TaskType typeOpt : TaskType.values()){
				if(arg.equals("-" + typeOpt.name().toLowerCase())){
					if(taskType == null){
						taskType = typeOpt;
						processed = true;
					}
					else throw new IllegalArgumentException("May only use one task type at a time.");
				}
			}
			if(!processed){
				if(arg.equals("-start")){
					start = true;
					processed = true;
				}
			}
			if(!processed){
				files.add(new File(arg));
			}
		}
		if(taskType == null) taskType = TaskType.COPY;
		
		Task task = new Task(taskType, files, start);
		
		
		JFrame frame = new JFrame("File Copier");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new CopyPanel(task));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		new Thread() {
			
			@Override
			public void run() {
				// init ikon resolver
				IkonResolver.getInstance();
			}
		}.start();
	}
	
	
}
