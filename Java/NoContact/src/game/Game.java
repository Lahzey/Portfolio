package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.ImageUtil;
import ui.Assets;
import ui.Frame;
import util.ColorUtil;

public class Game implements GameInputListener {

	private static final long INFECTION_ICON_TIME = 2000;
	private static final float INFECTION_ICON_RISE = 1f;
	private static final int STATS_PADDING = 20;
	private static final int STATS_ICON_GAP = 10;
	private static final Font FONT = Assets.FONT.deriveFont(Frame.getFontSize());
	private static Map<Integer, Image> SCALED_VIRUS_ICONS = new HashMap<Integer, Image>();
	private static Image SCALED_TIME_ICON = null;
	
	public static final float SIZE_IN_METERS = 20;
	
	private static final Comparator<? super GameObject> Z_COMPARATOR = new Comparator<GameObject>() {

		@Override
		public int compare(GameObject o1, GameObject o2) {
			return o1.getZ() - o2.getZ();
		}
	};
	
	public static int DOOR_COUNT = 1;
	public static int MOVE_SPEED = 1;
	public static long SPAWN_COOLDOWN = 5000;
	public static int CURE_SPEED = 10;
	public static int INITIAL_INFECTION_RATE = 60;
	public static int INCREASE_PER_INFECTION = 30;
	
	public boolean paused = false;
	public boolean gameOver = false;
	private Long lastUpdate = null;
	private long gameTime = 0;
	
	private Set<GameObject> gameObjects = new HashSet<>();
	
	private Person selectedPerson = null;
	
	private float infectedRatio = INITIAL_INFECTION_RATE / 100f;
	
	private Map<Point2D.Float, Long> recentInfections = new HashMap<>();
	
	public Game() {
		float gapBetweenDoors = SIZE_IN_METERS / (DOOR_COUNT + 3);

		Color[] colors = ColorUtil.randomDistinctColors(new Random(), DOOR_COUNT * 2);
		
		// spawn left and right doors
		for (int yMod = 1; yMod <= DOOR_COUNT; yMod++) {
			float y = gapBetweenDoors * (yMod + 1);

			Color color = colors[yMod - 1 + DOOR_COUNT];
			Entrance entrance = new Entrance(color, 0, y);
			Exit exit = new Exit(entrance, SIZE_IN_METERS, y);
			entrance.setExit(exit);
			gameObjects.add(entrance);
			gameObjects.add(exit);
		}
		
		// spawn top and bottom doors
		for (int xMod = 1; xMod <= DOOR_COUNT; xMod++) {
			float x = gapBetweenDoors * (xMod + 1);
			
			Color color = colors[xMod - 1];
			Entrance entrance = new Entrance(color, x, 0);
			Exit exit = new Exit(entrance, x, SIZE_IN_METERS);
			entrance.setExit(exit);
			gameObjects.add(entrance);
			gameObjects.add(exit);
		}
	}

	public float getInfectedRatio() {
		return infectedRatio;
	}

	public void onInfection(Person newlyInfected) {
		recentInfections.put(new Point2D.Float(newlyInfected.getX(), newlyInfected.getY()), gameTime);
		infectedRatio += INCREASE_PER_INFECTION / 100f;
		if (infectedRatio >= 1) {
			gameOver = true;
			selectedPerson = null;
			infectedRatio = 1;
		}
	}

	public void spawn(GameObject object) {
		gameObjects.add(object);
	}

	public void despawn(GameObject object) {
		gameObjects.remove(object);
		
		if (selectedPerson == object) {
			selectedPerson = null;
		}
	}
	
	public void update() {
		long currentTime = System.currentTimeMillis();
		if (lastUpdate != null && !paused && !gameOver) {
			update(currentTime - lastUpdate);
		}
		lastUpdate = currentTime;
	}
	
	private void update(long deltaTime) {
		gameTime += deltaTime;
		
		// cure infections
		infectedRatio -= (CURE_SPEED / 60f / 100f) * (deltaTime / 1000f);
		if (infectedRatio < INITIAL_INFECTION_RATE / 100f) {
			infectedRatio = INITIAL_INFECTION_RATE / 100f;
		}
		
		// prepare copy of list to prevent concurrent modification
		List<GameObject> gameObjects;
		synchronized (this.gameObjects) {
			gameObjects = new ArrayList<>(this.gameObjects);
		}
		
		// find collisions
		for (GameObject gameObject : gameObjects) {
			gameObject.collidingWith.clear();
		}
		for (int i = 0; i < gameObjects.size() - 1; i++) {
			GameObject objectA = gameObjects.get(i);
			for (int ii = i + 1; ii < gameObjects.size(); ii++) {
				GameObject objectB = gameObjects.get(ii);
				if (objectA.collidesWidth(objectB)) {
					objectA.collidingWith.add(objectB);
					objectB.collidingWith.add(objectA);
				}
			}
		}
		
		// update objects
		for (GameObject gameObject : gameObjects) {
			gameObject.update(this, deltaTime);
		}
	}
	
	public void render(Graphics2D graphics, int size) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, size, size);
		
		List<GameObject> gameObjects;
		synchronized (this.gameObjects) {
			gameObjects = new ArrayList<>(this.gameObjects);
		}
		gameObjects.sort(Z_COMPARATOR);
		for (GameObject gameObject : gameObjects) {
			gameObject.render(graphics, size);
		}
		
		if (selectedPerson != null) {
			int x = GameObject.translateToPixels(selectedPerson.getX(), size);
			int y = GameObject.translateToPixels(selectedPerson.getY(), size);
			int width = GameObject.translateToPixels(selectedPerson.getWidth(), size);
			int height = GameObject.translateToPixels(selectedPerson.getHeight(), size);
			int targetX = GameObject.translateToPixels(selectedPerson.getTargetX(), size);
			int targetY = GameObject.translateToPixels(selectedPerson.getTargetY(), size);
			int exitX = GameObject.translateToPixels(selectedPerson.getExit().getX(), size);
			int exitY = GameObject.translateToPixels(selectedPerson.getExit().getY(), size);
			
			// draw highlight
			graphics.setColor(selectedPerson.getColor());
			graphics.drawRect(x - width / 2, y - height / 2, width, height);
			
			// draw path
			graphics.setColor(selectedPerson.getColor());
			graphics.drawLine(x, y, targetX, targetY);
			graphics.drawLine(targetX, targetY, exitX, exitY);
		}

		graphics.setFont(FONT);
		int fontHeight = graphics.getFontMetrics().getAscent() - 5;
		
		int x = STATS_PADDING;
		int y = STATS_PADDING;
		
		// draw infected ratio
		int iconWidth = fontHeight;
		int percentRatio = (int) (infectedRatio * 100);
		graphics.setColor(Assets.VIRUS_COLOR);
		graphics.drawImage(getVirusIcon(fontHeight), x, y, null);
		graphics.drawString(percentRatio + "%", x + iconWidth + STATS_ICON_GAP, y + fontHeight);
		
		y += fontHeight + STATS_PADDING;
		
		// draw time
		graphics.setColor(Color.WHITE);
		graphics.drawImage(getTimeIcon(fontHeight), x, y, null);
		graphics.drawString(getTimeString(), x + iconWidth + STATS_ICON_GAP, y + fontHeight);
		
		// draw recent infections
		Composite originalComposite = graphics.getComposite();
		Image infectionIcon = getVirusIcon(GameObject.translateToPixels(1, size));
		List<Point2D.Float> infectionLocations = new ArrayList<>(recentInfections.keySet());
		for (Point2D.Float infectionLocation : infectionLocations) {
			long infectionTime = recentInfections.get(infectionLocation);
			if (gameTime < infectionTime + INFECTION_ICON_TIME) {
				// calculate pixel location
				float riseRatio = (gameTime - infectionTime) / (float) INFECTION_ICON_TIME;
				int iconX = GameObject.translateToPixels(infectionLocation.x, size) - infectionIcon.getWidth(null) / 2;
				int iconY = GameObject.translateToPixels(infectionLocation.y - riseRatio * INFECTION_ICON_RISE, size) - infectionIcon.getWidth(null) / 2;
				
				// change alpha
				float alpha = riseRatio < 0.5f ? riseRatio * 2 : 1f - (riseRatio - 0.5f) * 2;
				graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				
				graphics.drawImage(infectionIcon, iconX, iconY, null);
			} else {
				recentInfections.remove(infectionLocation);
			}
		}
		graphics.setComposite(originalComposite);
	}
	
	public long getTime() {
		return gameTime;
	}
	
	public String getTimeString() {
		int minutes = (int) (gameTime / 1000 / 60);
		int seconds = (int) (gameTime / 1000) - minutes * 60;
		return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
	}
	
	private Image getVirusIcon(int height) {
		if (!SCALED_VIRUS_ICONS.containsKey(height)) {
			SCALED_VIRUS_ICONS.put(height, ImageUtil.getHeightScaledImage(Assets.VIRUS_ICON, height));
		}
		return SCALED_VIRUS_ICONS.get(height);
	}
	
	private Image getTimeIcon(int height) {
		// will only ever be called for the font height, so can keep only one scaled copy
		if (SCALED_TIME_ICON == null) {
			SCALED_TIME_ICON = FontIcon.of(FontAwesomeSolid.CLOCK, height, Color.WHITE).toImage();
		}
		return SCALED_TIME_ICON;
	}

	@Override
	public void mousePressed(MouseEvent e, float x, float y, int button) {
		if (paused || gameOver) {
			return;
		}
		
		if (button == MouseEvent.BUTTON1) {
			Person clickedPerson = getPersonAt(x, y);
			if (clickedPerson != null) {
				selectedPerson = clickedPerson;
			} else if (selectedPerson != null) {
				selectedPerson.setTargetX(x);
				selectedPerson.setTargetY(y);
			}
		} else if (button == MouseEvent.BUTTON3) {
			selectedPerson = null;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e, float x, float y) {
		if (paused || gameOver) {
			return;
		}
		
		Person hoveredPerson = getPersonAt(x, y);
		
		if (hoveredPerson != null) {
			e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	private Person getPersonAt(float x, float y) {
		List<GameObject> gameObjects;
		synchronized (this.gameObjects) {
			gameObjects = new ArrayList<>(this.gameObjects);
		}
		
		for (GameObject gameObject : gameObjects) {
			if (gameObject instanceof Person && gameObject.containsPoint(x, y)) {
				return (Person) gameObject;
			}
		}
		return null;
	}
	
}
