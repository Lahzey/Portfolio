package com.creditsuisse.graphics.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

public class ScaledImageButton extends JComponent{
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_GAP = 5;
	
	private JButton button;
	private JLabel text;
	private JImage image;
	private Position imagePosition;
	private int gap = DEFAULT_GAP;
	
	private boolean initialized;
	
	public ScaledImageButton(String text, Image image, Position imagePosition){
		super();
		setLayout(new BorderLayout());
		this.button = new JButton();
		add(button);
		
		this.text = new JLabel(text);
		this.image = new JImage(image);
		this.imagePosition = imagePosition;
		initComponents();
		initialized = true;
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	public ScaledImageButton(String text, Image image){
		this(text, image, Position.LEFT);
	}
	
	private void initComponents(){
		button.setLayout(new MigLayout("insets 0, gap 0"));
		button.removeAll();
		switch(imagePosition){
		case BOTTOM:
			button.add(this.text, "grow, wrap");
			button.add(this.image, "alignx center, gapy" + gap + "px 0px");
		case RIGHT:
			button.add(this.text, "grow");
			button.add(this.image, "gapx " + gap + "px 0px");
		case TOP:
			button.add(this.image, "alignx center, wrap, gapy 0px " + gap + "px");
			button.add(this.text, "grow");
		case LEFT:
		default:
			button.add(this.image, "gapx 0px " + gap + "px");
			button.add(this.text, "grow");
		}
	}

	public void addActionListener(ActionListener l) {
		button.addActionListener(l);
	}

	public ActionListener[] getActionListeners() {
		return button.getActionListeners();
	}

	public Image getImage() {
		if(initialized) return image.getImage();
		else return null;
	}

	public void setImage(Image image) {
		if(initialized) this.image.setImage(image);
	}
	
	public String getText() {
		if(initialized) return text.getText();
		else return "";
	}

	public void setText(String text) {
		if(initialized) this.text.setText(text);
	}
	
	public int getIconTextGap() {
		return gap;
	}
	
	public void setIconTextGap(int iconTextGap) {
		gap = iconTextGap;
		if(initialized) initComponents();
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if(initialized){
			text.setFont(font);
			image.setFont(font);
		}
	}
	
	@Override
	public void setFocusable(boolean focusable) {
		button.setFocusable(focusable);
	}
	
	@Override
	public boolean isFocusable(){
		return button.isFocusable();
	}
	
	public Position getImagePosition(){
		return imagePosition;
	}
	
	public void setImagePosition(Position imagePosition){
		this.imagePosition = imagePosition;
		if(initialized) initComponents();
	}

	public static enum Position{
		LEFT, RIGHT, TOP, BOTTOM;
	}
}
