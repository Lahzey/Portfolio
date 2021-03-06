package ui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MenuLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	
	private static Color TEXT = new Color(179, 109, 0);
	
	public MenuLabel(String text) {
		this(text, 1, SwingConstants.LEFT);
	}
	
	public MenuLabel(String text, int horizontalAlignment) {
		this(text, 1, horizontalAlignment);
	}
	
	public MenuLabel(String text, float textSize) {
		this(text, textSize, SwingConstants.LEFT);
	}
	
	public MenuLabel(String text, float textSize, int horizontalAlignment) {
		super(text);
		setForeground(TEXT);
		setFont(Assets.FONT.deriveFont(Frame.getFontSize() * textSize));
		setHorizontalAlignment(horizontalAlignment);
	}
	
}
