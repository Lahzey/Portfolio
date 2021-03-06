package graphics.swing.colors;

import java.awt.Color;

import javax.swing.JComponent;

import util.GeneralListener;

public class Backgrounds {

	public static void set(JComponent c, Color normalColor, Color hoveredColor, Color clickedColor) {
		set(c, normalColor, hoveredColor, clickedColor, null, null);
	}

	public static void set(JComponent c, Color normalColor, Color hoveredColor, Color clickedColor, GeneralListener changeListener) {
		set(c, normalColor, hoveredColor, clickedColor, null, changeListener);
	}

	public static void set(JComponent c, Color normalColor, Color hoveredColor, Color clickedColor, Color selectedColor) {
		set(c, normalColor, hoveredColor, clickedColor, selectedColor, null);
	}

	public static void set(JComponent c, Color normalColor, Color hoveredColor, Color clickedColor, Color selectedColor, GeneralListener changeListener) {
		new StyleSetter<Color>(c, normalColor != null ? normalColor : c.getBackground(), hoveredColor, clickedColor, selectedColor, changeListener) {

			@Override
			protected void setStyle(JComponent component, Color color) {
				component.setBackground(color);
			}
			
		};
		if (normalColor != null) {
			c.setBackground(normalColor);
		}
	}
}
