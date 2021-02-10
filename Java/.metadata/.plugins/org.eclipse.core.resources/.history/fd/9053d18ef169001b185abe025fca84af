package com.creditsuisse.graphics.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import com.creditsuisse.util.ColorUtil;

import net.miginfocom.swing.MigLayout;

public class SearchField extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private JTextField inputField = new JTextField();
	private JImage submitButton;
	private BufferedImage searchIcon = FontIcon.of(FontAwesomeSolid.SEARCH).toImage();
	
	public SearchField(){
		super(new MigLayout("", "[grow, fill]0px[]", ""));
		
		
		submitButton = new JImage(searchIcon);
		
		add(inputField);
		add(submitButton);
		
		Color submitColor = ColorUtil.changeBrightness(UIManager.getColor("Panel.background"), 0.8f);
		
		submitButton.setBorder(inputField.getBorder());
		Backgrounds.set(submitButton, submitColor, ColorUtil.changeBrightness(submitColor, 1.25f), ColorUtil.changeBrightness(submitColor, 0.75f));
		submitButton.setOpaque(true);
		
		inputField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.DARK_GRAY), BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		submitButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.DARK_GRAY), BorderFactory.createEmptyBorder(0, 3, 0, 2)));
		
		adjustSizes();
	}
	
	private void adjustSizes(){
		int height = (int) (getFontMetrics(getFont()).getMaxAscent() * 2f);
		if(submitButton != null) submitButton.setPreferredSize(new Dimension(height, height));
		if(inputField != null) inputField.setPreferredSize(new Dimension(height, height));
	}
	
	/**
	 * Adds a listener which is called when the field is submitted by either pressing enter or clicking the search button.
	 * @param actionListener the ActionListener to call
	 */
	public void addActionListener(ActionListener actionListener){
		submitButton.addActionListener(actionListener);
		inputField.addActionListener(actionListener);
	}
	
	public void addChangeListener(final ActionListener changeListener){
		inputField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				changeListener.actionPerformed(new ActionEvent(inputField, 0, "remove"));
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				changeListener.actionPerformed(new ActionEvent(inputField, 0, "insert"));
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				changeListener.actionPerformed(new ActionEvent(inputField, 0, "change"));
			}
		});
	}

	
	//Text Field methods
	
	public Document getDocument() {
		if(inputField != null) return inputField.getDocument();
		else return null;
	}

	public Color getSelectionColor() {
		if(inputField != null) return inputField.getSelectionColor();
		else return null;
	}

	public void setSelectionColor(Color c) {
		if(inputField != null) inputField.setSelectionColor(c);
	}

	public Color getSelectedTextColor() {
		if(inputField != null) return inputField.getSelectedTextColor();
		else return null;
	}

	public void setSelectedTextColor(Color c) {
		if(inputField != null) inputField.setSelectedTextColor(c);
	}

	public String getText(int offs, int len) throws BadLocationException {
		if(inputField != null) return inputField.getText(offs, len);
		else return "";
	}

	public Color getForeground() {
		if(inputField != null) return inputField.getForeground();
		else return super.getForeground();
	}

	public void setText(String t) {
		if(inputField != null) inputField.setText(t);
	}

	public boolean isForegroundSet() {
		if(inputField != null) return inputField.isForegroundSet();
		else return super.isForegroundSet();
	}

	public FontMetrics getFontMetrics(Font font) {
		if(inputField != null) return inputField.getFontMetrics(font);
		else return super.getFontMetrics(font);
	}

	public String getText() {
		if(inputField != null) return inputField.getText();
		else return "";
	}

	public String getSelectedText() {
		if(inputField != null) return inputField.getSelectedText();
		else return "";
	}

	public boolean isEditable() {
		if(inputField != null) return inputField.isEditable();
		else return true;
	}

	public void setEditable(boolean b) {
		if(inputField != null) inputField.setEditable(b);
	}

	public Font getFont() {
		if(inputField != null) return inputField.getFont();
		else return super.getFont();
	}
	
	public void setFont(Font f) {
		if(inputField != null) inputField.setFont(f);
		super.setFont(f);
		adjustSizes();
	}

	public int getSelectionStart() {
		if(inputField != null) return inputField.getSelectionStart();
		else return 0;
	}

	public void setSelectionStart(int selectionStart) {
		if(inputField != null) inputField.setSelectionStart(selectionStart);
	}

	public int getSelectionEnd() {
		if(inputField != null) return inputField.getSelectionEnd();
		else return 0;
	}

	public void setSelectionEnd(int selectionEnd) {
		if(inputField != null) inputField.setSelectionEnd(selectionEnd);
	}

	public boolean isFontSet() {
		if(inputField != null) return inputField.isFontSet();
		else return super.isFontSet();
	}

	public void setInputVerifier(InputVerifier inputVerifier) {
		if(inputField != null) inputField.setInputVerifier(inputVerifier);
	}

	public InputVerifier getInputVerifier() {
		if(inputField != null) return inputField.getInputVerifier();
		else return super.getInputVerifier();
	}

	public void setForeground(Color fg) {
		if(inputField != null) inputField.setForeground(fg);
	}
	
	
}
