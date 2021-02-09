package graphics.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.border.AbstractBorder;

public class DashedBorder extends AbstractBorder {
	private static final long serialVersionUID = 1L;
	
	private Color color;
	private int thickness;
	private int gap;
	
	public DashedBorder(Color color, int thickness, int gap){
		this.color = color;
		this.thickness = thickness;
		this.gap = gap;
	}

	@Override
	    public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
	        Graphics2D gg = (Graphics2D) g;
	        gg.setColor(color);
	        gg.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{gap}, 0));
	        gg.drawRect(x + thickness / 2, y + thickness / 2, w - thickness, h - thickness);
	    }
}
