package com.creditsuisse.graphics.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A JPanel drawing a color gradient on the background.
 * @author A469627
 *
 */
public class GradientPanel extends JPanel{

	public static final Direction DEFAULT_DIRECTION = Direction.VERTICAL;
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	
	private Color[] colors;
	private Direction direction = DEFAULT_DIRECTION;
	private int transitionSize = -1;
	private float opacity = 1f;
	

	public GradientPanel() {
		super();
		setBackground(TRANSPARENT);
	}
	
	public GradientPanel(Color... colors) {
		this(DEFAULT_DIRECTION, colors);
	}
	
	public GradientPanel(Direction direction, Color... colors) {
		//Not using setters to avoid repaints
		this.colors = colors;
		this.direction = direction;
		setBackground(TRANSPARENT);
	}

	public GradientPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setBackground(TRANSPARENT);
	}

	public GradientPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		setBackground(TRANSPARENT);
	}

	public GradientPanel(LayoutManager layout) {
		super(layout);
		setBackground(TRANSPARENT);
	}
	
	public Color[] getColors(){
		return colors;
	}
	
	public void setColors(Color... colors){
		this.colors = colors;
		repaint();
	}
	
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
		repaint();
	}
	
	public int getTransitionSize(){
		return transitionSize;
	}
	
	public void setTransitionSize(int transitionSize){
		this.transitionSize = transitionSize;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		if(opacity < 0 || opacity > 1) throw new IllegalArgumentException("Opacity may not be smaller than 0.0 or bigger than 1.0, but was " + opacity + ".");
		this.opacity = opacity;
	}

	@Override
    protected void paintComponent(Graphics g) {
		boolean opaque = isOpaque();
        if(colors.length >= 1 && opaque){
    		BufferedImage background = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    		Graphics2D g2d = background.createGraphics();
        	Paint paint = g2d.getPaint();
    		int[] transitionPoints = new int[colors.length - 1];
    		int sizePerColor = (direction == Direction.HORIZONTAL ? getWidth() : getHeight()) / colors.length;
        	int transitionSize = this.transitionSize >= 0 ? this.transitionSize : sizePerColor;
        	int transitionRadius = transitionSize / 2;
        	
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    		
        	for(int i = 0; i < colors.length; i++){
        		if(i > 0){
            		transitionPoints[i - 1] = sizePerColor * i;
        		}
        		g2d.setColor(colors[i]);
        		if(direction == Direction.HORIZONTAL){
        			int x = sizePerColor * i;
        			int width = sizePerColor;
        			if(i > 0){
        				x += transitionRadius;
        				width -= transitionRadius;
        			}
        			if(i < colors.length - 1){
        				width -= transitionRadius;
        			}
            		g2d.fillRect(x, 0, width, getHeight());
        		}else{
        			int y = sizePerColor * i;
        			int height = sizePerColor;
        			if(i > 0){
        				y += transitionRadius;
        				height -= transitionRadius;
        			}
        			if(i < colors.length - 1){
        				height -= transitionRadius;
        			}
        			g2d.fillRect(0, y, getWidth(), height);
        			g2d.drawLine(0, (sizePerColor * i), getWidth(), (sizePerColor * i) + sizePerColor);
        		}
        	}
        	
        	for(int i = 0; i < transitionPoints.length; i++){
        		int transitionPoint = transitionPoints[i];
        		if(direction == Direction.HORIZONTAL){
        			GradientPaint gp = new GradientPaint(transitionPoint - transitionRadius, 0, colors[i], transitionPoint + transitionRadius, 0, colors[i + 1]);
        			g2d.setPaint(gp);
        			g2d.fillRect(transitionPoint - transitionRadius, 0, transitionPoint + transitionRadius, getHeight());
        		}else{
        			GradientPaint gp = new GradientPaint(0, transitionPoint - transitionRadius, colors[i], 0, transitionPoint + transitionRadius, colors[i + 1]);
        			g2d.setPaint(gp);
        			g2d.fillRect(0, transitionPoint - transitionRadius, getWidth(), transitionPoint + transitionRadius);
        		}
        	}
        	
        	Composite composite = ((Graphics2D) g).getComposite();
        	((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getOpacity()));
        	g.drawImage(background, 0, 0, null);
        	((Graphics2D) g).setComposite(composite);
        }
        setOpaque(false);
        super.paintComponent(g);
        setOpaque(opaque);
    }
	
	public static enum Direction{
		/** Draws top (color1) to bottom (color2) */
		VERTICAL,

		/** Draws left (color1) to right (color2) */
		HORIZONTAL;
	}

}
