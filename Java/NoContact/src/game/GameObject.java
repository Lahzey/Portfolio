package game;

import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

public abstract class GameObject {
	
	public final Set<GameObject> collidingWith = new HashSet<>();

	private float x = 0;
	private float y = 0;
	private float width = 0;
	private float height = 0;

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getMinX() {
		return x - width / 2;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getMinY() {
		return y - height / 2;
	}
	
	public int getZ() {
		return 0;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	public boolean containsPoint(float x, float y) {
		boolean containsX = x >= getX() - getWidth() / 2 && x <= getX() + getWidth() / 2;
		boolean containsY = y >= getY() - getHeight() / 2 && y <= getY() + getHeight() / 2;
		return containsX && containsY;
	}
	
	public boolean collidesWidth(GameObject other) {
		boolean xCollides;
		if (getX() < other.getX()) {
			xCollides =  other.getX() - getX() < getWidth();
		} else {
			xCollides = getX() - other.getX() < other.getWidth();
		}

		boolean yCollides;
		if (getY() < other.getY()) {
			yCollides =  other.getY() - getY() < getHeight();
		} else {
			yCollides = getY() - other.getY() < other.getHeight();
		}
		
		return xCollides && yCollides;
	}

	public abstract void update(Game game, long deltaTime);

	public abstract void render(Graphics2D graphics, int pixelSize);
	
	public static int translateToPixels(float meters, int gameSize) {
		return (int) ((meters / Game.SIZE_IN_METERS) * gameSize);
	}
}
