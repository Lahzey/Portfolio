package com.creditsuisse.graphics.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.creditsuisse.util.FileUtil;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class TextFieldFileChooser extends JPanel{
	
	private static final String FILE_SPLIT = ", ";
	
	private JTextField inputField;
	private JButton openChooserButton = new JButton("...");
	private AdvancedFileChooser fileChooser = new AdvancedFileChooser();
	
	private FileFilter fileFilter;
	private int mode = JFileChooser.OPEN_DIALOG;
	
	public TextFieldFileChooser(boolean autocomplete) {
		if(autocomplete) inputField = new FileTextField();
		else inputField = new JTextField();

		fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			
			@Override
			public String getDescription() {
				return null;
			}
			
			@Override
			public boolean accept(File f) {
				return fileFilter == null ? true : fileFilter.accept(f);
			}
		});
		
		setLayout(new BorderLayout());
		add(inputField, BorderLayout.CENTER);
		add(openChooserButton, BorderLayout.EAST);
		
		openChooserButton.setToolTipText("Browse Files");
		openChooserButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File selectedFile = getSelectedFile();
				if(selectedFile != null){
					fileChooser.setCurrentDirectory(FileUtil.getRealParentFile(selectedFile));
					fileChooser.setSelectedFiles(getSelectedFiles().toArray(new File[0]));
				}
				int selection;
				switch(mode){
				case JFileChooser.OPEN_DIALOG:
					selection = fileChooser.showOpenDialog(TextFieldFileChooser.this.getTopLevelAncestor());
					break;
				case JFileChooser.SAVE_DIALOG:
					selection = fileChooser.showSaveDialog(TextFieldFileChooser.this.getTopLevelAncestor());
					break;
				default:
					return;
				}
				if(selection == JFileChooser.APPROVE_OPTION){
					if(fileChooser.isMultiSelectionEnabled()){
						setSelectedFiles(Arrays.asList(fileChooser.getSelectedFiles()));
					}else{
						setSelectedFile(fileChooser.getSelectedFile());
					}
				}
			}
		});
	}
	
	public File getSelectedFile(){
		List<File> selectedFiles = getSelectedFiles();
		if(selectedFiles.size() > 0) return selectedFiles.get(0);
		else return null;
	}
	
	public List<File> getSelectedFiles(){
		String text = getText();
		List<File> files = new ArrayList<File>();
		if(text.contains("\"")){
			// has multiple files
			int start = -1;
			int i = 0;
			for(char c : text.toCharArray()){
				if(c == '"'){
					if(start < 0) start = i + 1;
					else{
						files.add(new File(text.substring(start, i)));
						start = -1;
					}
				}
				i++;
			}
		}else{
			// has a single file
			files.add(new File(text));
		}
		return files;
	}
	
	public void setSelectedFile(File file){
		setText(file.getAbsolutePath());
	}
	
	public void setSelectedFiles(List<File> files){
		if(files == null || files.size() == 0) inputField.setText("");
		else if(files.size() == 1) setSelectedFile(files.get(0));
		else{
			String text = null;
			for(File file : files){
				if(text == null) text = "\"" + file.getAbsolutePath() + "\"" ;
				else text += FILE_SPLIT + "\"" + file.getAbsolutePath() + "\"";
			}
			inputField.setText(text);
		}
	}
	
	public void setText(String text){
		inputField.setText(text);
	}
	
	public String getText(){
		return inputField.getText();
	}

	public AdvancedFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(AdvancedFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}
	
	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
		if(inputField instanceof FileTextField){
			((FileTextField) inputField).setFileFilter(fileFilter);
		}
	}
	
	public Document getDocument(){
		return inputField.getDocument();
	}

	public int getMode() {
		return mode;
	}

	/**
	 * @param mode one of {@link JFileChooser#OPEN_DIALOG} and {@link JFileChooser#SAVE_DIALOG}
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public void setFont(Font font){
		super.setFont(font);
		if (inputField != null) inputField.setFont(font);
		if (inputField != null) openChooserButton.setFont(font);
	}
	
	@Override
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);
		inputField.setEnabled(enabled);
		openChooserButton.setEnabled(enabled);
	}
	
	public static File showDialog(String title, String input, final int mode, FileFilter fileFilter, final Component relativeTo){
		final JFrame dialog = new JFrame(title);
		final File[] result = {null}; // array so it can be final and editable
		
		final TextFieldFileChooser chooser = new TextFieldFileChooser(true);
		chooser.setText(input);
		chooser.setFileFilter(fileFilter);
		chooser.setMode(mode);
		dialog.add(chooser, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new MigLayout("fillx", "[][grow]", ""));
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		final JButton okButton = new JButton("Ok");
		buttonPanel.add(okButton, "right");
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File selection = chooser.getSelectedFile();
				if(selection != null && (selection.exists() || mode != JFileChooser.OPEN_DIALOG)){
					result[0] = selection;
					dialog.dispose();
				}
			}
		});

		if(mode == JFileChooser.OPEN_DIALOG){
			chooser.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					onChange();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					onChange();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					onChange();
				}
				
				private void onChange(){
					File selection = chooser.getSelectedFile();
					okButton.setEnabled(selection != null && selection.exists());
				}
			});
			File selection = chooser.getSelectedFile();
			okButton.setEnabled(selection != null && selection.exists());
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				dialog.pack();
				dialog.setLocationRelativeTo(relativeTo);
				dialog.setVisible(true);
			}
		});
		
		// wait until dialog is showing
		while(!dialog.isVisible()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// does not matter
			}
		}
		
		// wait until dialog is closed
		while(dialog.isVisible()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// does not matter
			}
		}
		
		return result[0];
	}
}
