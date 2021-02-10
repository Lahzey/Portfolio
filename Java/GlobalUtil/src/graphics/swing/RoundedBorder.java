package graphics.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

public class RoundedBorder extends AbstractBorder {

	private Color color;
	private int thickness;
	private int radius;
	
	public RoundedBorder(Color color, int thickness, int radius) {
		this.color = color;
		this.thickness = thickness;
        this.radius = radius;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if ((this.thickness > 0) && (g instanceof Graphics2D)) {
            Graphics2D g2d = (Graphics2D) g;

            Color oldColor = g2d.getColor();
            g2d.setColor(this.color);

            Shape outer;
            Shape inner;

            int offs = this.thickness;
            int size = offs + offs;
            if (this.radius > 0) {
                outer = new RoundRectangle2D.Float(x, y, width, height, radius, radius);
                inner = new RoundRectangle2D.Float(x + offs, y + offs, width - size, height - size, radius, radius);
            }
            else {
                outer = new Rectangle2D.Float(x, y, width, height);
                inner = new Rectangle2D.Float(x + offs, y + offs, width - size, height - size);
            }
            Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
            path.append(outer, false);
            path.append(inner, false);
            g2d.fill(path);
            g2d.setColor(oldColor);
        }
	}

    /**
     * Reinitialize the insets parameter with this Border's current Insets.
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
	@Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(thickness, thickness, thickness, thickness);
        return insets;
    }

	@Override
	public boolean isBorderOpaque() {
        return true;
	}

}
