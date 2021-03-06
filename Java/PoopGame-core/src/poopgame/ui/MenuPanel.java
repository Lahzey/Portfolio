package poopgame.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.badlogic.gdx.Gdx;

import net.miginfocom.swing.MigLayout;

public class MenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static Image BACKGROUND = null;

	public MenuPanel(String layoutConstraints, String colConstraints, String rowConstraints) {
		super(new MigLayout(layoutConstraints, colConstraints, rowConstraints));
	}

	public Image getBackgroundImage() {
		if (BACKGROUND == null) {
			try {
				BACKGROUND = ImageIO.read(Gdx.files.internal("ui/slime_background.jpg").read());
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		return BACKGROUND;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		int tileWidth = getBackgroundImage().getWidth(null);
		int tileHeight = getBackgroundImage().getHeight(null);
		for (int y = 0; y < getHeight(); y += tileHeight) {
			for (int x = 0; x < getWidth(); x += tileWidth) {
				g2d.drawImage(getBackgroundImage(), x, y, this);
			}
		}
		g2d.dispose();
	}

}
