package graphics.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public class OverlayBorder implements Border {
	
	private final Border base;
	private final Border overlay;
	
	public OverlayBorder(Border base, Border overlay){
		this.base = base;
		this.overlay = overlay;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		base.paintBorder(c, g, x, y, width, height);
		overlay.paintBorder(c, g, x, y, width, height);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return base.getBorderInsets(c);
	}

	@Override
	public boolean isBorderOpaque() {
		return base.isBorderOpaque() || overlay.isBorderOpaque();
	}
	
	public Border getBase(){
		return base;
	}
	
	public Border getOverlay(){
		return overlay;
	}

}
