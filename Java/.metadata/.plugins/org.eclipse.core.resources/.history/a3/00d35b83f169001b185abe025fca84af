package com.creditsuisse.graphics.swing;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import com.creditsuisse.graphics.ImageUtil;

public class ScaledMenuItem extends JMenuItem{
	private static final long serialVersionUID = 1L;
	
	private int iconHeight = 30;
	private Image original;

	public ScaledMenuItem(String text, Image image) {
		super(text);
		setIcon(image);
	}
	
	public void setIcon(Image image){
		original = image;
		super.setIcon(new ImageIcon(ImageUtil.getHeightScaledImage(image, iconHeight)));
	}
	
	@Override
	protected void paintComponent(Graphics g){
		int textHeight = g.getFontMetrics().getHeight();
		if(textHeight != iconHeight){
			iconHeight = textHeight;
			super.setIcon(new ImageIcon(ImageUtil.getHeightScaledImage(original, iconHeight)));
		}
		super.paintComponent(g);
	}
}
