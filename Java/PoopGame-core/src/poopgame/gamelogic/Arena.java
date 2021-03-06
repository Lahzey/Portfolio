package poopgame.gamelogic;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;

public enum Arena {

	SEWER("SEWER");
	
	private final String name;
	private Image icon;
	
	private Arena(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Image getIcon() {
		if (icon == null) {
			try {
				icon = ImageIO.read(Gdx.files.internal("maps/" + toString().toLowerCase() + "/icon.png").read());
			} catch (IOException e) {
				throw new RuntimeException("Failed to load icon for " + getName() + ".", e);
			}
		}
		return icon;
	}
	
	public String getMapPath() {
		return "maps/" + name().toLowerCase() + "/" + name().toLowerCase() + ".tmx";
	}
	
}
