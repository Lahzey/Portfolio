package com.creditsuisse.graphics.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public class PartialMatteBorder implements Border  {

	/**
	 * 5 means the border length is 5 pixels
	 */
	public static final int PIXEL = 0;
	
	/**
	 * 5 means the border length the full length - 5 pixels
	 */
	public static final int SUBTRACTED_PIXEL = 1;
	
	/**
	 * 5 means the border length is 5% of the full length
	 */
	public static final int PERCENTAGE = 2;
	
	// alignments
	public static final int CENTER = 10;
	public static final int LEFT = 11;
	public static final int RIGHT = 12;
	public static final int TOP = 13;
	public static final int BOTTOM = 14;

	
	
	private int topThickness;
	private int rightThickness;
	private int bottomThickness;
	private int leftThickness;
	private int topLength;
	private int rightLength;
	private int bottomLength;
	private int leftLength;
	private int topAlignment;
	private int rightAlignment;
	private int bottomAlignment;
	private int leftAlignment;
	
	private int lengthUnit;
	
	private Color color;

	public PartialMatteBorder(int topThickness, int rightThickness, int bottomThickness, int leftThickness, int topLength, int rightLength, int bottomLength, int leftLength, int topAlignment, int rightAlignment, int bottomAlignment, int leftAlignment, int lengthUnit, Color color) {
		super();
		this.topThickness = topThickness;
		this.rightThickness = rightThickness;
		this.bottomThickness = bottomThickness;
		this.leftThickness = leftThickness;
		this.topLength = topLength;
		this.rightLength = rightLength;
		this.bottomLength = bottomLength;
		this.leftLength = leftLength;
		this.topAlignment = topAlignment;
		this.rightAlignment = rightAlignment;
		this.bottomAlignment = bottomAlignment;
		this.leftAlignment = leftAlignment;
		this.lengthUnit = lengthUnit;
		this.color = color;
	}
	
	public PartialMatteBorder(int topThickness, int rightThickness, int bottomThickness, int leftThickness, int topLength, int rightLength, int bottomLength, int leftLength, int lengthUnit, Color color) {
		this(topThickness, rightThickness, bottomThickness, leftThickness, topLength, rightLength, bottomLength, leftLength, CENTER, CENTER, CENTER, CENTER, lengthUnit, color);
	}
	
	public PartialMatteBorder(int topThickness, int rightThickness, int bottomThickness, int leftThickness, int length, int lengthUnit, Color color){
		this(topThickness, rightThickness, bottomThickness, leftThickness, length, length, length, length, lengthUnit, color);
	}
	
	public PartialMatteBorder(int thickness, int length, int lengthUnit, Color color){
		this(thickness, thickness, thickness, thickness, length, lengthUnit, color);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		if(color == null) return;
		
		Insets insets = getBorderInsets(c);
        Color oldColor = g.getColor();
        g.translate(x, y);
        
        // calculate length of sides
        int topLength = 0;
        int rightLength = 0;
        int bottomLength = 0;
        int leftLength = 0;
        switch(lengthUnit){
        case PIXEL:
        	topLength = this.topLength;
        	rightLength = this.rightLength;
        	bottomLength = this.bottomLength;
        	leftLength = this.leftLength;
        	break;
        case SUBTRACTED_PIXEL:
        	topLength = width - this.topLength;
        	rightLength = height - this.rightLength;
        	bottomLength = width - this.bottomLength;
        	leftLength = height - this.leftLength;
        	break;
        case PERCENTAGE:
        	topLength = width * this.topLength / 100;
        	rightLength = height * this.rightLength / 100;
        	bottomLength = width * this.bottomLength / 100;
        	leftLength = height * this.leftLength / 100;
        	break;
        }
        
        int topStartX = topAlignment == LEFT ? 0 : (topAlignment == RIGHT ? width - topLength : width / 2 - topLength / 2);
        int topStartY = 0;
        int bottomStartX = bottomAlignment == LEFT ? 0 : (bottomAlignment == RIGHT ? width - bottomLength : width / 2 - bottomLength / 2);
        int bottomStartY = height - insets.bottom;

        int leftStartY = leftAlignment == TOP ? 0 : (leftAlignment == BOTTOM ? height - leftLength : height / 2 - leftLength / 2);
        int leftStartX = 0;
        int rightStartY = rightAlignment == TOP ? 0 : (rightAlignment == BOTTOM ? height - rightLength : height / 2 - rightLength / 2);
        int rightStartX = width - insets.right;

        g.setColor(color);
        g.fillRect(topStartX, topStartY, topLength, insets.top);
        g.fillRect(bottomStartX, bottomStartY, bottomLength, insets.bottom);
        g.fillRect(leftStartX, leftStartY, insets.left, leftLength);
        g.fillRect(rightStartX, rightStartY, insets.right, rightLength);
        
        
        g.translate(-x, -y);
        g.setColor(oldColor);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(topThickness, leftThickness, bottomThickness, rightThickness);
	}

	@Override
	public boolean isBorderOpaque() {
		return color != null;
	}
	

}
