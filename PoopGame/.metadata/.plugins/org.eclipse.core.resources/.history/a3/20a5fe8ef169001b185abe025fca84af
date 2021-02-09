package com.creditsuisse.graphics.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.creditsuisse.util.ColorUtil;
import com.creditsuisse.util.LoopThread;

public class TextProgressBar extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public static final String MINIMUM_NUMBER = "$MINIMUM";
	public static final String MAXIMUM_NUMBER = "$MAXIMUM";
	public static final String VALUE_NUMBER = "$VALUE";
	public static final String COMMENT = "$COMMENT";
	
	private static final int MINIMIZED_HEIGHT = 5;
	
	private long minimum = 0;
	private long maximum = 0;
	private long value = 0;
	private String comment = "";

	private static final Color DEFAULT_COLOR = ColorUtil.SUCCESS_FOREGROUND_COLOR.brighter();
	private Color color = DEFAULT_COLOR;
	
	private String text = VALUE_NUMBER + " / " + MAXIMUM_NUMBER + " " + COMMENT;
	private JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
	
	private boolean minimized = false;
	private int currentHeight = getTargetHeight();
	private MinimizeAnimationThread animationThread;

	public TextProgressBar() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.WHITE);
		add(textLabel);
	}
	
	public TextProgressBar(String text) {
		this();
		setText(text);
	}

	public long getMinimum() {
		return minimum;
	}

	public void setMinimum(long minimum) {
		this.minimum = minimum;
		repaint();
	}

	public long getMaximum() {
		return maximum;
	}

	public void setMaximum(long maximum) {
		this.maximum = maximum;
		repaint();
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
		repaint();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		if(comment == null) comment = "";
		this.comment = comment;
		repaint();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		repaint();
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if(color == null) color = DEFAULT_COLOR;
		this.color = color;
		repaint();
	}
	
	public boolean isMinimized(){
		return minimized;
	}
	
	public void setMinimized(boolean minimized){
		this.minimized = minimized;
		setBorder(minimized ? null : BorderFactory.createLineBorder(Color.BLACK));
		
		if(animationThread == null || !animationThread.isRunning()){
			animationThread = new MinimizeAnimationThread();
			animationThread.start();
		}
	}
	
	@Override
	public Dimension getMinimumSize(){
		return new Dimension(0, currentHeight);
	}

	@Override
	public Dimension getMaximumSize(){
		Dimension max = super.getMaximumSize();
		return new Dimension(max != null ? max.width : 1, currentHeight);
	}

	@Override
	public Dimension getPreferredSize(){
		Dimension pref = super.getPreferredSize();
		return new Dimension(pref != null ? pref.width : 1, currentHeight);
	}
	
	@SuppressWarnings("serial")
	private int getTargetHeight(){
		if(minimized){
			return MINIMIZED_HEIGHT;
		}else{
			return new FontMetrics(textLabel.getFont()){}.getHeight() * 2;
		}
	}
	
	@Override
	public void setFont(Font font){
		super.setFont(font);
		if(textLabel != null) textLabel.setFont(font);
	}
	
	@Override
	public Font getFont() {
		if(textLabel != null){
			return textLabel.getFont();
		}else{
			return super.getFont();
		}
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if(textLabel != null) textLabel.setForeground(fg);
	}
	
	@Override
	public Color getForeground() {
		if(textLabel != null){
			return textLabel.getForeground();
		}else{
			return super.getForeground();
		}
	}

	@Override
	protected void paintComponent(Graphics g){
		Dimension size = getSize();
		
		if(minimized){
			textLabel.setText("");
		}else{
			setLabelText();
		}
		super.paintComponent(g);
		
		Color oldColor = g.getColor();
		g.setColor(color);
		if(maximum - minimum > 0) g.fillRect(0, 0, (int)(size.width * ((double)value / (maximum - minimum))), size.height);
		
		g.setColor(oldColor);
	}
	
	private void setLabelText(){
		String text = this.text;
		text = text.replace(MINIMUM_NUMBER, minimum + "");
		text = text.replace(MAXIMUM_NUMBER, maximum + "");
		text = text.replace(VALUE_NUMBER, value + "");
		text = text.replace(COMMENT, comment);
		text.trim();
		textLabel.setText(text);
	}
	
	
	private class MinimizeAnimationThread extends LoopThread{
		
		private static final float PIXEL_PER_MILLI = 0.1f;
		
		private long lastIteration;
		
		private float accumulatedPixels;
		
		public MinimizeAnimationThread(){
			super(60);
		}

		@Override
		public void onStart(){
			lastIteration = System.currentTimeMillis();
		}

		@Override
		public void loopedRun() {
			int target = getTargetHeight();
			if(target != currentHeight){
				long current = System.currentTimeMillis();
				long passed = current - lastIteration;
				
				accumulatedPixels += passed * PIXEL_PER_MILLI;
				int change = (int) accumulatedPixels;
				accumulatedPixels -= change;
				
				if(currentHeight < target) {
					currentHeight += change;
					if(currentHeight > target) currentHeight = target;
				}
				else{
					currentHeight -= change;
					if(currentHeight < target) currentHeight = target;
				}
				revalidate();
				repaint();
				
				lastIteration = current;
			}else{
				terminate();
			}
		}
		
	}
	
}
