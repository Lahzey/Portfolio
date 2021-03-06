package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import graphics.swing.colors.Backgrounds;

public class MenuButton extends JButton {
	private static final long serialVersionUID = 1L;

	private static Color DEFAULT = new Color(53, 35, 23, 100);
	private static Color HOVERED = new Color(110, 45, 29, 100);
	private static Color CLICKED = new Color(139, 31, 5, 100);
	private static Color TEXT = new Color(179, 109, 0);
	
	public MenuButton(String text, ActionListener actionListener) {
		super(text);
		setFocusPainted(false);
		setBorderPainted(false);
		Backgrounds.set(this, DEFAULT, HOVERED, CLICKED, HOVERED);
		setForeground(TEXT);
		setFont(Assets.FONT.deriveFont(Frame.getFontSize()));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setOpaque(false);
		
		addActionListener(actionListener);
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
