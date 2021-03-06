package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import graphics.GIF;
import graphics.ImageUtil;
import ui.Assets;

public class Entrance extends GameObject {
	
	private static final Random RANDOM = new Random();
	private static final float COOLDOWN_DEVIATION = 0.3f;
	
	private Color color;
	private Exit exit;
	
	private long spawnCooldown = (long) (RANDOM.nextFloat() * Game.SPAWN_COOLDOWN);
	
	private GIF walkAnimation;
	
	public Entrance(Color color, float x, float y) {
		this.color = color;
		setWidth(3);
		setHeight(3);
		setX(x);
		setY(y);
		
		walkAnimation = new GIF(Assets.WALK_ANIMATION);
		walkAnimation.resize(98, 98, false);
		walkAnimation.edit(image -> ImageUtil.color(image, color));
	}

	public Color getColor() {
		return color;
	}
	
	@Override
	public int getZ() {
		return 1;
	}
	
	public Exit getExit() {
		return exit;
	}

	public void setExit(Exit exit) {
		this.exit = exit;
	}

	@Override
	public void update(Game game, long deltaTime) {
		spawnCooldown -= deltaTime;
		if (spawnCooldown <= 0) {
			game.spawn(new Person(this, Game.MOVE_SPEED, game.getInfectedRatio() > RANDOM.nextFloat()));
			spawnCooldown += Game.SPAWN_COOLDOWN;
			spawnCooldown += COOLDOWN_DEVIATION * Game.SPAWN_COOLDOWN * (RANDOM.nextFloat() * 2 - 1);
		}
	}

	@Override
	public void render(Graphics2D graphics, int gameSize) {
		graphics.setColor(color);
		graphics.fillRect(translateToPixels(getMinX(), gameSize), translateToPixels(getMinY(), gameSize), translateToPixels(getWidth(), gameSize), translateToPixels(getHeight(), gameSize));
	}

	public GIF getWalkAnimation() {
		return walkAnimation;
	}

}
