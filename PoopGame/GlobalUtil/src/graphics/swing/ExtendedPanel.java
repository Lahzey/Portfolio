package graphics.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class ExtendedPanel extends JPanel {
	
	private float opacity = 1f;
	private Color overlay = null;

	public ExtendedPanel() {
		super();
	}

	public ExtendedPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public ExtendedPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public ExtendedPanel(LayoutManager layout) {
		super(layout);
	}
	
	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = Math.max(Math.min(opacity, 1f), 0f);
		repaint();
	}

	public Color getOverlay() {
		return overlay;
	}

	public void setOverlay(Color overlay) {
		this.overlay = overlay;
		repaint();
	}
	
	@Override
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		Composite composite = g2d.getComposite();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		
		super.paint(g);
		
		if(overlay != null){
			Color oldColor = g.getColor();
			g.setColor(overlay);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(oldColor);
		}
		
		g2d.setComposite(composite);
	}

	@Override
	protected void paintComponent(Graphics g){
	}
	
}
