package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import graphics.GIF;

public class Person extends GameObject {
	
	private Entrance entrance;
	private float metersPerSec;
	
	private float targetX;
	private float targetY;
	
	private boolean contagious;
	private boolean infected;
	
	private GIF walkAnimation;
	
	public Person(Entrance entrance, int metersPerSec, boolean contagious) {
		this.entrance = entrance;
		this.metersPerSec = metersPerSec;
		this.contagious = contagious;
		setWidth(1);
		setHeight(1);
		setX(entrance.getX());
		setY(entrance.getY());
		
		targetX = entrance.getExit().getX();
		targetY = entrance.getExit().getY();
		
		walkAnimation = new GIF(entrance.getWalkAnimation());
	}

	public float getTargetX() {
		return targetX;
	}

	public void setTargetX(float targetX) {
		this.targetX = targetX;
	}

	public float getTargetY() {
		return targetY;
	}

	public void setTargetY(float targetY) {
		this.targetY = targetY;
	}

	public void infect() {
		this.infected = true;
	}

	public Entrance getEntrance() {
		return entrance;
	}

	public Exit getExit() {
		return entrance.getExit();
	}

	public Color getColor() {
		return entrance.getColor();
	}

	@Override
	public void update(Game game, long deltaTime) {
		walkAnimation.update(deltaTime);
		
		// infection
		if (contagious) {
			for (GameObject object : collidingWith) {
				if (object instanceof Person) {
					Person person = (Person) object;
					if (!person.infected) {
						person.infect();
						game.onInfection(person);
						break;
					}
				}
			}
		}
		
		// movement
		float targetXDif = targetX - getX();
		float targetYDif = targetY - getY();
		
		float targetDist = (float) Math.sqrt(targetXDif * targetXDif + targetYDif * targetYDif);
		float travelDist = metersPerSec * (deltaTime / 1000f);
		float travelRatio = travelDist / targetDist;
		
		if (travelRatio > 1) {
			travelRatio = 1;
			targetX = getExit().getX();
			targetY = getExit().getY();
		}
		
		setX(getX() + targetXDif * travelRatio);
		setY(getY() + targetYDif * travelRatio);
		
		if (Math.abs(getX() - getExit().getX()) < 0.1f && Math.abs(getY() - getExit().getY()) < 0.1f) {
			game.despawn(this);
		}
	}

	@Override
	public void render(Graphics2D graphics, int gameSize) {
		// size and position
		int x = translateToPixels(getMinX(), gameSize);
		int y = translateToPixels(getMinY(), gameSize);
		int width = translateToPixels(getWidth(), gameSize);
		int height = translateToPixels(getHeight(), gameSize);
		
		// distance to target
		float targetDistX = targetX - getX();
		float targetDistY = targetY - getY();
		
		BufferedImage image = walkAnimation.currentFrame();
		
		// rotation
		double degrees = Math.toDegrees(Math.atan(targetDistX / targetDistY)) * -1;
		degrees += (targetDistY < 0 ? 180 : 0);
//		degrees = 360 - degrees;
//		System.out.println(targetDistX + "/" + targetDistY + ": " + degrees);
		double locationX = image.getWidth() / 2;
		double locationY = image.getHeight() / 2;
		AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(degrees), locationX, locationY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		
		graphics.drawImage(op.filter(image, null), x, y, width, height, null);
	}

}
