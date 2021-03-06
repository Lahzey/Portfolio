package game;

import java.awt.Color;

import ui.Assets;
import util.ColorUtil;

public enum Difficulty {

	EASY(1, 1, 7000, 10, 30, ColorUtil.SUCCESS_FOREGROUND_COLOR, ColorUtil.SUCCESS_BACKGROUND_COLOR.darker(), ColorUtil.SUCCESS_BORDER_COLOR),
	MEDIUM(1, 1, 4000, 5, 30, ColorUtil.WARNING_FOREGROUND_COLOR, ColorUtil.WARNING_BACKGROUND_COLOR, ColorUtil.WARNING_BORDER_COLOR),
	HARD(2, 2, 6000, 0, 40, ColorUtil.ERROR_FOREGROUND_COLOR, ColorUtil.ERROR_BACKGROUND_COLOR, ColorUtil.ERROR_BORDER_COLOR);

	private int doorCount;
	private int moveSpeed;
	private long spawnCooldown;
	private int cureSpeed;
	private int initialInfections;

	private Color foregroundColor;
	private Color backgroundColor;
	private Color borderColor;

	private Difficulty(int doorCount, int moveSpeed, long spawnCooldown, int cureSpeed, int initialInfections, Color foregroundColor, Color backgroundColor, Color borderColor) {
		this.doorCount = doorCount;
		this.moveSpeed = moveSpeed;
		this.spawnCooldown = spawnCooldown;
		this.cureSpeed = cureSpeed;
		this.initialInfections = initialInfections;
		this.foregroundColor = ColorUtil.mix(foregroundColor, Assets.BACKGROUND_COLOR);
		this.backgroundColor = ColorUtil.mix(backgroundColor, Assets.BACKGROUND_COLOR);
		this.borderColor = ColorUtil.mix(borderColor, Assets.BACKGROUND_COLOR);
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void set() {
		Game.DOOR_COUNT = doorCount;
		Game.MOVE_SPEED = moveSpeed;
		Game.SPAWN_COOLDOWN = spawnCooldown;
		Game.CURE_SPEED = cureSpeed;
		Game.INITIAL_INFECTION_RATE = initialInfections;
	}
}
