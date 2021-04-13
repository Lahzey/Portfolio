package graphics.swing.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import graphics.Icon;
import graphics.ImageUtil;

public class JLamp extends JComponent{
	private static final long serialVersionUID = 1L;
	
	public static final Color DEFAULT_COLOR = new Color(0, 0, 0, 0);
	
	public static final Image LAMP_IMAGE = Icon.LED.toImage();
	private static final Map<Color, Image> TINTED_IMAGES = new HashMap<Color, Image>();
	
	public JLamp(){
		this(DEFAULT_COLOR);
	}
	
	public JLamp(Color color){
		setColor(color);
	}
	
	public Dimension getPreferredSize(){
		int fontHeight = getFontMetrics(getFont()).getHeight();
		return new Dimension(fontHeight, fontHeight);
	}
	
	/**
	 * Sets the color of the lamp.
	 * <br/>same as {@link #setForeground(Color)}
	 * @param color the color to set (may have transparency)
	 */
	public void setColor(Color color){
		setForeground(color);
	}
	
	/**
	 * Gets the color of the lamp.
	 * <br/>same as {@link #getForeground()}
	 * @return the color of the lamp
	 */
	public Color getColor(){
		return getForeground();
	}
	
	public Image getTintedImage(){
		Color color = getForeground();
		Image tinted = TINTED_IMAGES.get(color);
		if(tinted == null) {
			tinted = ImageUtil.color(LAMP_IMAGE, color);
			TINTED_IMAGES.put(color, tinted);
		}
		return tinted;
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		int width = getWidth();
		int height = getHeight();
		int x = 0;
		int y = 0;
		if(width > height){
			x = (width - height) / 2;
			width = height;
		}else if(height > width){
			y = (height - width) / 2;
			height = width;
		}
		
		g.drawImage(getTintedImage(), x, y, width, height, null);
	}
	
	

}
