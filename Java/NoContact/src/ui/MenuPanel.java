package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class MenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public MenuPanel() {
		super();
	}

	public MenuPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public MenuPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public MenuPanel(LayoutManager layout) {
		super(layout);
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int tileWidth = Assets.BACKGROUND.getWidth(null);
        int tileHeight = Assets.BACKGROUND.getHeight(null);
        for (int y = 0; y < getHeight(); y += tileHeight) {
            for (int x = 0; x < getWidth(); x += tileWidth) {
                g2d.drawImage(Assets.BACKGROUND, x, y, this);
            }
        }
        g2d.dispose();
    }
	
}
