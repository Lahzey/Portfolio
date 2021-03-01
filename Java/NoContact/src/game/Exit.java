package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class Exit extends GameObject {
	
	private Entrance entrance;
	
	public Exit(Entrance entrance, float x, float y) {
		this.entrance = entrance;
		setWidth(3);
		setHeight(3);
		setX(x);
		setY(y);
	}
	
	public Color getColor() {
		return entrance.getColor();
	}
	
	@Override
	public int getZ() {
		return 1;
	}

	@Override
	public void update(Game game, long deltaTime) {}

	@Override
	public void render(Graphics2D graphics, int gameSize) {
		graphics.setColor(getColor());
		graphics.fillRect(translateToPixels(getMinX(), gameSize), translateToPixels(getMinY(), gameSize), translateToPixels(getWidth(), gameSize), translateToPixels(getHeight(), gameSize));
	}

}
