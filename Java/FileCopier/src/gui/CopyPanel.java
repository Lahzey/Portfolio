package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import graphics.swing.FileDrop;
import graphics.swing.FileDrop.Listener;
import graphics.swing.components.CollapsibleTextPane;
import graphics.swing.components.JImage;
import graphics.swing.components.TextFieldFileChooser;
import graphics.swing.components.TextProgressBar;
import util.ColorUtil;
import util.LoopThread;
import util.StringFormatter;
import util.StringUtil;

import logic.FileProcessor;
import logic.FileProcessor.CopyResult;
import logic.Task;
import logic.Task.TaskType;
import net.miginfocom.swing.MigLayout;

public class CopyPanel extends JPanel implements DocumentListener {
	
	private Task task;
	private List<CopyResult> copyResults = new ArrayList<>();
	
	private TextFieldFileChooser sourceChooser = new TextFieldFileChooser(true);
	private TextFieldFileChooser destinationChooser = new TextFieldFileChooser(true);
	
	private JButton actionButton = new JButton();
	
	private JPanel progressPanel = new JPanel(new MigLayout("fillx, wrap 2, insets 0, hidemode 3", "[]25px[]", ""));
	
	public CopyPanel(Task task){
		super(new MigLayout("fillx, wrap 1, hidemode 3", "[grow, fill]", "[]5px[]20px[]5px[]20px[]20px[]"));
		this.task = task;
		setPreferredSize(new Dimension(500, 300));
		
		sourceChooser.getFileChooser().setMultiSelectionEnabled(task.type.multiSource);
		sourceChooser.getFileChooser().setFileSelectionMode(task.type.sourceSelectionMode);
		sourceChooser.setVisible(task.type.hasSource);
		destinationChooser.getFileChooser().setMultiSelectionEnabled(task.type.multiDestination);
		destinationChooser.getFileChooser().setFileSelectionMode(task.type.destinationSelectionMode);
		destinationChooser.setVisible(task.type.hasDestination);
		
		String actionName = StringUtil.capitalizeAt(task.type.name().toLowerCase(), 0);
		
		add(new JLabel("To " + actionName + ":"));
		add(sourceChooser);
		add(new JLabel("Destination:"));
		add(destinationChooser);
		
		add(actionButton);
		
		add(progressPanel);
		
		new FileDrop(sourceChooser, new Listener() {
			
			@Override
			public void filesDropped(File[] files) {
				List<File> fileList = new ArrayList<>();
				for(File file : files){
					if(sourceChooser.getFileChooser().accept(file)){
						fileList.add(file);
						if(!task.type.multiSource) break;
					}
				}
				sourceChooser.setSelectedFiles(fileList );
			}
		});

		new FileDrop(destinationChooser, new Listener() {
			
			@Override
			public void filesDropped(File[] files) {
				List<File> fileList = new ArrayList<>();
				for(File file : files){
					if(destinationChooser.getFileChooser().accept(file)){
						fileList.add(file);
						if(!task.type.multiDestination) break;
					}
				}
				destinationChooser.setSelectedFiles(fileList);
			}
		});
		
		actionButton.addActionListener(e -> {
			if(task.type == TaskType.COPY) copy();
		});
		actionButton.setText(actionName);
		actionButton.setEnabled(false);
		
		sourceChooser.getDocument().addDocumentListener(this);
		destinationChooser.getDocument().addDocumentListener(this);
	}
	
	private boolean validateInputs(){
		boolean valid = true;
		
		// check sources
		if(task.type.hasSource){
			boolean sourcesValid = true;
			if(!sourceChooser.getText().isEmpty()){
				for(File source : sourceChooser.getSelectedFiles()){
					if(!source.exists()) sourcesValid = false;
				}
			} else sourcesValid = false;
			valid = valid && sourcesValid;
		}
		
		
		// check destination
		if(task.type.hasDestination){
			boolean destinationValid = true;
			if(!destinationChooser.getText().isEmpty()){
				for(File destination : destinationChooser.getSelectedFiles()){
					if(!destination.exists()) destinationValid = false;
				}
			} else destinationValid = false;
			valid = valid && destinationValid;
		}
		
		actionButton.setEnabled(valid);
		return valid;
	}
	
	private void copy(){
		if(!validateInputs()) return;
		
		for(File file : sourceChooser.getSelectedFiles()){
			copyResults.add(FileProcessor.copy(file, destinationChooser.getSelectedFile(), false));
		}
		
		sourceChooser.setEnabled(false);
		destinationChooser.setEnabled(false);
		actionButton.setEnabled(false);
		progressPanel.removeAll();

		JImage cancelButton = new JImage(FontAwesomeSolid.TIMES, Color.DARK_GRAY);
		cancelButton.generateStateImages();
		progressPanel.add(cancelButton, "span 2, right");
		
		TextProgressBar progressBar = new TextProgressBar();
		progressBar.setVisible(false);
		progressPanel.add(progressBar, "span 2, grow");
		
		JLabel timeLabel = new JLabel("Time:");
		progressPanel.add(timeLabel);
		JLabel timeValue = new JLabel();
		progressPanel.add(timeValue);
		
		JLabel calcLabel = new JLabel("Calculating Size...");
		progressPanel.add(calcLabel);
		JLabel calcValue = new JLabel();
		progressPanel.add(calcValue);
		
		JLabel speedLabel = new JLabel("Speed:");
		progressPanel.add(speedLabel);
		
		JLabel speedValue = new JLabel();
		progressPanel.add(speedValue);
		
		JLabel copiedLabel = new JLabel();
		progressPanel.add(copiedLabel);
		
		JLabel failedLabel = new JLabel();
		progressPanel.add(failedLabel);

		JLabel threadsLabel = new JLabel("Open Threads:");
		progressPanel.add(threadsLabel);
		
		JLabel threadsValue = new JLabel();
		progressPanel.add(threadsValue);
		
		CollapsibleTextPane exceptionsPanel = new CollapsibleTextPane("Exceptions (0)", "");
		exceptionsPanel.setVisible(false);
		exceptionsPanel.setCollapsed(true);
		progressPanel.add(exceptionsPanel, "span 2, grow");
		
		progressPanel.revalidate();
		progressPanel.repaint();
		

		cancelButton.addActionListener(e -> {
			for(CopyResult result : copyResults) result.cancelRequested = true;
			progressBar.setColor(ColorUtil.WARNING_FOREGROUND_COLOR.brighter());
		});
		
		new LoopThread(3) {
			
			private long lastByteCount = 0;
			private boolean finishedListing = false;
			private Set<Exception> exceptions = new HashSet<>();
			private int lastExceptionsSize = 0;
			
			@Override
			public void loopedRun() {
				int totalByteCount = 0;
				int successCount = 0;
				int failedCount = 0;
				int fileCount = 0;
				boolean finishedListing = true;
				boolean finished = true;
				for(CopyResult result : copyResults){
					totalByteCount += result.totalByteCount;
					successCount += result.successCount;
					failedCount += result.failedCount;
					fileCount += result.files.size();
					finishedListing = finishedListing && result.finishedListing;
					finished = finished && result.isFinished();
					
					synchronized (result.exceptions) {
						exceptions.addAll(result.exceptions);
					}
				}
				int processedCount = successCount + failedCount;
				long time = System.currentTimeMillis() - startTime;
				double seconds = time / 1000d;
				
				if(exceptions.size() > lastExceptionsSize){
					exceptionsPanel.setVisible(true);
					exceptionsPanel.setTitle("Exceptions (" + exceptions.size() + ")");
					StringBuilder exceptionTexts = null;
					for(Exception exception : exceptions){
						if(exceptionTexts == null){
							exceptionTexts = new StringBuilder(exception.getMessage());
						}else{
							exceptionTexts.append("\n\n" + exception.getMessage());
						}
					}
					exceptionsPanel.setText(exceptionTexts.toString());
					lastExceptionsSize = exceptions.size();
				}
				
				if(!finished){
					long bytesPerSec = (long) ((totalByteCount - lastByteCount) * getActualLoopsPerSec());
					lastByteCount = totalByteCount;
					
					if(finishedListing){
						if(!this.finishedListing){
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									calcLabel.setVisible(false);
									calcValue.setVisible(false);
									progressBar.setVisible(true);
									progressPanel.revalidate();
								}
							});
							this.finishedListing = true;
						}
					}
					progressBar.setMaximum(fileCount);
					progressBar.setValue(processedCount);
					timeValue.setText(StringFormatter.formatNumber(seconds, 1) + " seconds");
					calcValue.setText("Processed: " + processedCount + "  |  Listed: " + fileCount);
					speedValue.setText(StringFormatter.formatByteCount(bytesPerSec) + "/s");
					copiedLabel.setText("Copied: " + successCount);
					failedLabel.setText("Failed: " + failedCount);
					threadsValue.setText(FileProcessor.threadCount + " / " + FileProcessor.MAX_THREAD_COUNT);
				}else{
					progressPanel.remove(cancelButton);
					
					timeLabel.setText("Copy " + (copyResults.get(0).cancelRequested ? "cancelled" : "completed") + " after:");
					timeValue.setText(StringFormatter.formatNumber(seconds, 1) + " seconds");
					
					progressBar.setValue(fileCount);
					progressBar.setMaximum(fileCount);
					speedLabel.setText("Average Speed:");
					speedValue.setText(StringFormatter.formatByteCount((long) (totalByteCount / seconds)) + "/s");

					progressPanel.add(new JLabel("Processed " + fileCount + " files"), 3);
					progressPanel.add(new JLabel("Total Size: " + StringFormatter.formatByteCount(totalByteCount)), 4);

					copiedLabel.setText("Copied: " + successCount);
					failedLabel.setText("Failed: " + failedCount);

					progressBar.setVisible(true);
					calcLabel.setVisible(false);
					calcValue.setVisible(false);
					threadsLabel.setVisible(false);
					threadsValue.setVisible(false);
					
					sourceChooser.setEnabled(true);
					destinationChooser.setEnabled(true);
					actionButton.setEnabled(true);
					
					progressPanel.revalidate();
					progressPanel.repaint();
					terminate();
				}
				
				progressPanel.repaint();
			}
		}.start();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		validateInputs();
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		validateInputs();
	}
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		validateInputs();
	}

}
