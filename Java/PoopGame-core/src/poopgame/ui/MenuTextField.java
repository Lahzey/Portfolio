package poopgame.ui;

import java.awt.Color;

import javax.swing.JTextField;

public class MenuTextField extends JTextField {
	private static final long serialVersionUID = 1L;
	
	public MenuTextField (String text) {
		super(text);
		setBackground(new Color(0, 0, 0, 0));
		setBorder(null);
		setOpaque(false);
		setFont(getFont().deriveFont(75f));
		setHorizontalAlignment(CENTER);
	}

}
