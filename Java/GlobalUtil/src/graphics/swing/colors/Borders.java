package graphics.swing.colors;
import javax.swing.JComponent;
import javax.swing.border.Border;

import util.GeneralListener;

public class Borders {

	public static void set(JComponent c, Border normalBorder, Border hoveredBorder, Border clickedBorder) {
		set(c, normalBorder, hoveredBorder, clickedBorder, null, null);
	}

	public static void set(JComponent c, Border normalBorder, Border hoveredBorder, Border clickedBorder, GeneralListener changeListener) {
		set(c, normalBorder, hoveredBorder, clickedBorder, null, changeListener);
	}

	public static void set(JComponent c, Border normalBorder, Border hoveredBorder, Border clickedBorder, Border selectedBorder) {
		set(c, normalBorder, hoveredBorder, clickedBorder, selectedBorder, null);
	}

	public static void set(JComponent c, Border normalBorder, Border hoveredBorder, Border clickedBorder, Border selectedBorder, GeneralListener changeListener) {
		new StyleSetter<Border>(c, normalBorder != null ? normalBorder : c.getBorder(), hoveredBorder, clickedBorder, selectedBorder, changeListener) {

			@Override
			protected void setStyle(JComponent component, Border border) {
				component.setBorder(border);
			}
			
		};
		if (normalBorder != null) {
			c.setBorder(normalBorder);
		}
	}
}
