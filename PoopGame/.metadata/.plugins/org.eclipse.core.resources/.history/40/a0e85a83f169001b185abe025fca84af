package com.creditsuisse.graphics.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.creditsuisse.graphics.GraphicsUtil;
import com.creditsuisse.util.ColorUtil;


public class RoundedMatteBorder extends PartialMatteBorder {

	private int top;
	private int right;
	private int bottom;
	private int left;
	private int topRightRadius;
	private int topLeftRadius;
	private int bottomRightRadius;
	private int bottomLeftRadius;
	private Color color;
	
	private boolean straightTopRightCorner;
	private boolean straightTopLeftCorner;
	private boolean straightBottomRightCorner;
	private boolean straightBottomLeftCorner;

	// used to track shape drawn and paint the background inside the rounded border only
	private Path2D.Float currentBorderPath = new Path2D.Float();
	private Color currentBackground = null;
	private Map<Shape, Stroke> shapesToDraw = new HashMap<Shape, Stroke>();
	
	
	public RoundedMatteBorder(int top, int right, int bottom, int left, int topRightRadius, int topLeftRadius, int bottomRightRadius, int bottomLeftRadius, Color color) {
		super(top, right, bottom, left, topRightRadius + topLeftRadius, topRightRadius + bottomRightRadius, bottomRightRadius + bottomLeftRadius, topLeftRadius + bottomLeftRadius, SUBTRACTED_PIXEL, color);
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
		this.topRightRadius = topRightRadius;
		this.topLeftRadius = topLeftRadius;
		this.bottomRightRadius = bottomRightRadius;
		this.bottomLeftRadius = bottomLeftRadius;
		this.color = color;
	}
	
	public RoundedMatteBorder(int top, int right, int bottom, int left, int radius, Color color){
		this(top, right, bottom, left, radius, radius, radius, radius, color);
	}


	public RoundedMatteBorder(int width, int radius, Color color){
		this(width, width, width, width, radius, radius, radius, radius, color);
	}
	

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Color oldColor = g.getColor();
        g.translate(x, y);
        
		Graphics2D g2d = (Graphics2D) g;
		Stroke stroke;
		float strokeWidth, halfStrokeWidth, startX, startY, endX, endY, cornerX, cornerY;
		
		//*************************************
		// Important: Border is painted clockwise so the path can be tracked and connected correctly
		//*************************************
		
		// top left
		strokeWidth = (left + top) / (left > 0 && top > 0 ? 2f : 1f);
		halfStrokeWidth = strokeWidth / 2;
		stroke = new BasicStroke(strokeWidth);
		startX = halfStrokeWidth;
		startY = topLeftRadius;
		endX = topLeftRadius;
		endY = halfStrokeWidth;
		cornerX = halfStrokeWidth;
		cornerY = halfStrokeWidth;
		
		if(left > 0 && top > 0){
			if(straightTopLeftCorner){
				draw(new Line2D.Float(startX, startY, endX, endY), stroke);
			}else{
				draw(GraphicsUtil.createRoundedCorner(startX, startY, endX, endY, cornerX, cornerY), stroke);
			}
		}else{
			if(left > 0){
				draw(new Line2D.Float(startX, startY, cornerX, cornerY), stroke);
			}else if(top > 0){
				draw(new Line2D.Float(cornerX, cornerY, endX, endY), stroke);
			}
		}
		
		// top right
		strokeWidth = (top + right) / (top > 0 && right > 0 ? 2f : 1f);
		halfStrokeWidth = strokeWidth / 2;
		stroke = new BasicStroke(strokeWidth);
		startX = width - topRightRadius;
		startY = halfStrokeWidth;
		endX = width - halfStrokeWidth;
		endY = topRightRadius;
		cornerX = width - halfStrokeWidth;
		cornerY = halfStrokeWidth;
		
		if(top > 0 && right > 0){
			if(straightTopRightCorner){
				draw(new Line2D.Float(startX, startY, endX, endY), stroke);
			}else{
				draw(GraphicsUtil.createRoundedCorner(startX, startY, endX, endY, cornerX, cornerY), stroke);
			}
		}else{
			if(top > 0){
				draw(new Line2D.Float(startX, startY, cornerX, cornerY), stroke);
			}else if(right > 0){
				draw(new Line2D.Float(cornerX, cornerY, endX, endY), stroke);
			}
		}

		// bottom right
		strokeWidth = (right + bottom) / (right > 0 && bottom > 0 ? 2f : 1f);
		halfStrokeWidth = strokeWidth / 2;
		stroke = new BasicStroke(strokeWidth);
		startX = width - halfStrokeWidth;
		startY = height - bottomRightRadius;
		endX = width - bottomRightRadius;
		endY = height - halfStrokeWidth;
		cornerX = width - halfStrokeWidth;
		cornerY = height - halfStrokeWidth;
		
		if(right > 0 && bottom > 0){
			if(straightBottomRightCorner){
				draw(new Line2D.Float(startX, startY, endX, endY), stroke);
			}else{
				draw(GraphicsUtil.createRoundedCorner(startX, startY, endX, endY, cornerX, cornerY), stroke);
			}
		}else{
			if(right > 0){
				draw(new Line2D.Float(startX, startY, cornerX, cornerY), stroke);
			}else if(bottom > 0){
				draw(new Line2D.Float(cornerX, cornerY, endX, endY), stroke);
			}
		}

		// bottom left
		strokeWidth = (bottom + left) / (bottom > 0 && left > 0 ? 2f : 1f);
		halfStrokeWidth = strokeWidth / 2;
		stroke = new BasicStroke(strokeWidth);
		startX = bottomLeftRadius;
		startY = height - halfStrokeWidth;
		endX = halfStrokeWidth;
		endY = height - bottomLeftRadius;
		cornerX = halfStrokeWidth;
		cornerY = height - halfStrokeWidth;
		
		if(bottom > 0 && left > 0){
			if(straightBottomLeftCorner){
				draw(new Line2D.Float(startX, startY, endX, endY), stroke);
			}else{
				draw(GraphicsUtil.createRoundedCorner(startX, startY, endX, endY, cornerX, cornerY), stroke);
			}
		}else{
			if(bottom > 0){
				draw(new Line2D.Float(startX, startY, cornerX, cornerY), stroke);
			}else if(left > 0){
				draw(new Line2D.Float(cornerX, cornerY, endX, endY), stroke);
			}
		}

		// evaluate background and set alpha to 0 to prevent background drawing outside rounded corner
		Color background = c.getBackground();
		boolean changed = currentBackground == null || background.getRed() != currentBackground.getRed() || background.getGreen() != currentBackground.getGreen() || background.getBlue() != currentBackground.getBlue() || background.getAlpha() != 0;
		if(changed){
			if(background.getAlpha() == 0){
				background = null;
			}else{
				currentBackground = background;
				c.setBackground(new Color(background.getRed(), background.getGreen(), background.getBlue(), 0));
			}
		}
        
		
		flush(c, g2d, x, y, width, height);
		
        g.translate(-x, -y);
        g.setColor(oldColor);
	}
	
	private void draw(Shape shape, Stroke stroke){
		if(currentBorderPath != null){
			currentBorderPath.append(shape, true);
		}
		shapesToDraw.put(shape, stroke);
	}
	
	private void flush(Component c, Graphics g, int x, int y, int width, int height){
		Graphics2D g2d = (Graphics2D) g;
		
		// paint background
		if(currentBorderPath != null && currentBackground != null){
			g2d.setColor(currentBackground);
			g2d.fill(currentBorderPath);
		}
		currentBorderPath = new Path2D.Float();
		
		// paint edges
		super.paintBorder(c, g2d, x, y, width, height);
		
		// paint corners
		g2d.setColor(color);
		for(Shape shape : shapesToDraw.keySet()){
			g2d.setStroke(shapesToDraw.get(shape));
			g2d.draw(shape);
		}
		shapesToDraw.clear();
	}
	
	
	public static void main(String[] args) {
		JPanel panel = new JPanel();
		panel.setBackground(ColorUtil.INFO_BACKGROUND_COLOR);
		panel.setBorder(new RoundedMatteBorder(5, 5, 0, 5, 50, ColorUtil.ERROR_FOREGROUND_COLOR));
		new TestFrame(panel);
	}

}
