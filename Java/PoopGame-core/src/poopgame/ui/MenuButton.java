package poopgame.ui;

import java.awt.Cursor;

import javax.swing.JButton;

import graphics.swing.colors.Foregrounds;

public class MenuButton extends JButton {
	private static final long serialVersionUID = 1L;
	
	public MenuButton (String text) {
		super(text);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
		setOpaque(false);
		setFont(getFont().deriveFont(75f));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Foregrounds.set(this, SwingFrame.FOREGROUND, SwingFrame.FOREGROUND.brighter(), SwingFrame.FOREGROUND.darker());
	}

}
