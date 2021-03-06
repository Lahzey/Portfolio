package ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import graphics.swing.colors.Backgrounds;
import graphics.swing.colors.Borders;

public class MenuInput extends JTextField {
	private static final long serialVersionUID = 1L;

	private static Color DEFAULT = new Color(53, 35, 23, 100);
	private static Color SELECTED = new Color(110, 45, 29, 100);
	private static Color BORDER = new Color(139, 31, 5, 100);
	private static Color TEXT = new Color(179, 109, 0);
	
	public MenuInput(String text, int horizontalAlignment) {
		super(text);
		setHorizontalAlignment(horizontalAlignment);
		Backgrounds.set(this, DEFAULT, SELECTED, SELECTED, SELECTED);
		Borders.set(this, BorderFactory.createLineBorder(DEFAULT.darker()), BorderFactory.createLineBorder(BORDER), BorderFactory.createLineBorder(BORDER), BorderFactory.createLineBorder(BORDER));
		setOpaque(false);
		setForeground(TEXT);
		setFont(Assets.FONT.deriveFont(Frame.getFontSize()));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (getBackground() != null) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		super.paintComponent(g);
	}
	
}
