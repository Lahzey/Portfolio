package ui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import game.Game;
import graphics.swing.components.JImage;
import net.miginfocom.swing.MigLayout;

public class GameContainer extends MenuPanel implements AWTEventListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	
	private Game game = null;
	
	private JPanel pauseMenu;
	private JPanel gameOverMenu;
	
	private MenuLabel timeLabel = new MenuLabel("", SwingConstants.CENTER);
	
	public GameContainer() {
		super(new MigLayout("fill", "[grow, center]", "[grow, center]"));
		
		// initialise pause menu
		pauseMenu = new JPanel(new MigLayout("wrap 1, gap 50px, insets 50px", "[grow, fill]", ""));
		pauseMenu.setOpaque(false);
		pauseMenu.add(new MenuButton("RESUME", e -> togglePause()));
		pauseMenu.add(new MenuButton("RESTART", e -> {
			removeAll();
			disposeGame();
		}));
		pauseMenu.add(new MenuButton("GIVE UP", e -> {
			removeAll();
			game.gameOver = true;
		}));
		pauseMenu.setBorder(BorderFactory.createLineBorder(new Color(53, 35, 23, 100), 5));
		
		// initialise game over menu
		gameOverMenu = new JPanel(new MigLayout("wrap 1, gap 50px, insets 50px", "[grow, fill]", ""));
		gameOverMenu.setOpaque(false);
		JImage virusImage = new JImage(Assets.VIRUS_ICON);
		virusImage.setPreferredSize(new Dimension(100, 100));
		gameOverMenu.add(virusImage);
		gameOverMenu.add(new MenuLabel("GAME OVER", 2f, SwingConstants.CENTER));
		gameOverMenu.add(timeLabel);
		gameOverMenu.add(new MenuButton("RESTART", e -> {
			removeAll();
			disposeGame();
		}));
		gameOverMenu.add(new MenuButton("EXIT", e -> {
			removeAll();
			disposeGame();
			Frame.show(Frame.MAIN_MENU);
		}));
		gameOverMenu.setBorder(BorderFactory.createLineBorder(new Color(53, 35, 23, 100), 5));
		
		getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	private void disposeGame() {
		game = null;
	}
	
	private void createGame() {
		game = new Game();
	}
	
	private void togglePause() {
		if (game != null && !game.gameOver) {
			game.paused = !game.paused;
			removeAll();
			if (game.paused) add(pauseMenu);
			revalidate();
		}
	}

	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (game == null) {
			createGame();
		}
		
		game.update();
		
		if (game.gameOver && !gameOverMenu.isDisplayable()) {
			removeAll();
			timeLabel.setText("You survived for " + game.getTimeString() + " " + (game.getTime() >= 60000 ? "minutes" : "seconds"));
			add(gameOverMenu);
			revalidate();
		}
		
		Rectangle gameArea = getGameArea();
		Graphics2D gameGraphics = (Graphics2D) g.create(gameArea.x, gameArea.y, gameArea.width, gameArea.height);
		game.render(gameGraphics, gameArea.width);
		gameGraphics.dispose();
		
		if (getComponentCount() > 0) {
			g.setColor(new Color(0, 0, 0, 100));
			g.fillRect((getWidth() - gameArea.width) / 2, (getHeight() - gameArea.height) / 2, gameArea.width, gameArea.height);
		}
	}
	
	private Rectangle getGameArea() {
		int size = Math.min(getWidth(), getHeight());
		return new Rectangle((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		if (event instanceof KeyEvent) {
			KeyEvent keyEvent = (KeyEvent) event;
			if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE && isDisplayable()) {
					togglePause();
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point2D.Float mouseLocation = translateMouseLocation(e);
		if (game == null || mouseLocation == null) {
			return;
		}
		
		game.mousePressed(e, mouseLocation.x, mouseLocation.y, e.getButton());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point2D.Float mouseLocation = translateMouseLocation(e);
		if (game == null || mouseLocation == null) {
			return;
		}
		
		game.mouseMoved(e, mouseLocation.x, mouseLocation.y);
	}
	
	private Point2D.Float translateMouseLocation(MouseEvent e) {
		Rectangle gameArea = getGameArea();
		if (gameArea.contains(e.getX(), e.getY())) {
			float x = ((e.getX() - gameArea.x) / (float) gameArea.width) * Game.SIZE_IN_METERS;
			float y = ((e.getY() - gameArea.y) / (float) gameArea.height) * Game.SIZE_IN_METERS;
			return new Point2D.Float(x, y);
		} else {
			return null;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
}
