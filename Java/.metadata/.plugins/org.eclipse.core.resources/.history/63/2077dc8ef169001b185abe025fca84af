package com.creditsuisse.graphics.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CommandLine extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private OutputStream outputStream;
	
	private JTextArea outputArea = new JTextArea();
	private JTextField inputField = new JTextField();
	
	private boolean autoPrintInput = true;
	private boolean autoClearInput = false;
	
	public CommandLine() {
		setLayout(new BorderLayout());
		
		outputArea.setEditable(false);
		inputField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(autoPrintInput) println(getInput());
				if(autoClearInput) clearInput();
			}
		});
		
		add(new JScrollPane(outputArea), BorderLayout.CENTER);
		add(inputField, BorderLayout.SOUTH);
		
		outputStream = new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
			     int[] bytes = {b};
			     write(bytes, 0, bytes.length);
			}
			
			 public void write(int[] bytes, int offset, int length) {
					print(new String(bytes, offset, length));
			 }
		};
	}
	
	@Override
	public void setFont(Font font){
		super.setFont(font);
		if(outputArea != null) outputArea.setFont(font);
		if(inputField != null) inputField.setFont(font);
	}
	
	public void print(String text){
		outputArea.setText(outputArea.getText() + text);
	}
	
	public void println(String text){
		print(text + System.getProperty("line.separator"));
	}
	
	public String readNextLine(){
		final StringBuilder input = new StringBuilder();
		addInputListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				input.append(getInput());
			}
		});
		while(input.length() == 0){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return input.toString();
	}
	
	public void clearOutput(){
		outputArea.setText("");
	}
	
	public void clearInput(){
		inputField.setText("");
	}
	
	public void setInput(String input){
		inputField.setText(input);
	}
	
	public String getInput(){
		return inputField.getText();
	}
	
	public void addInputListener(ActionListener listener){
		inputField.addActionListener(listener);
	}
	
	public void removeInputListener(ActionListener listener){
 		inputField.removeActionListener(listener);
	}
	
	public void addInputDocumentListener(DocumentListener listener){
		inputField.getDocument().addDocumentListener(listener);
	}
	
	public void removeInputDocumentListener(DocumentListener listener){
		inputField.getDocument().removeDocumentListener(listener);
	}
	
	public boolean isAutoPrintInput() {
		return autoPrintInput;
	}

	public void setAutoPrintInput(boolean autoPrintInput) {
		this.autoPrintInput = autoPrintInput;
	}

	public boolean isAutoClearInput() {
		return autoClearInput;
	}

	public void setAutoClearInput(boolean autoClearInput) {
		this.autoClearInput = autoClearInput;
	}
	
	public OutputStream getOutputStream(){
		return outputStream;
	}
	
	public void openInNewFrame(int width, int height){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		this.setPreferredSize(new Dimension(width, height));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		CommandLine cmd = new CommandLine();
		cmd.addInputListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Input Listener");
			}
		});
		cmd.addInputDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				System.out.println("Doc Listener Remove");
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				System.out.println("Doc Listener Insert");
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				System.out.println("Doc Listener Change");
			}
		});
		
		cmd.openInNewFrame(600, 400);
		
		cmd.print("Enter Anything: ");
		cmd.println("You entered: " + cmd.readNextLine());
	}
}
