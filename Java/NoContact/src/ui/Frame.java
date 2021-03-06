package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import graphics.swing.Inspector;
import util.LoopThread;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static Frame INSTANCE;
	
	public static final MainMenu MAIN_MENU = new MainMenu();
	public static final GameContainer GAME_CONTAINER = new GameContainer();
	
	private static JPanel currentlyShowing = null;
	
	public Frame() {
		INSTANCE = this;
		
		JPanel focusDummy = new JPanel();
		focusDummy.setPreferredSize(new Dimension());
		focusDummy.setMaximumSize(new Dimension());
		focusDummy.setOpaque(false);
		focusDummy.setFocusable(true);
		add(focusDummy, BorderLayout.NORTH);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int squareSize = Math.min(1200, Math.min(screenSize.width, screenSize.height) - 100);
		
		getContentPane().setPreferredSize(new Dimension(squareSize, squareSize));
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		new LoopThread(60) {
			
			@Override
			public void loopedRun() {
				if (isVisible()) {
					repaint();
				}
			}
		}.start();
		
		show(MAIN_MENU);
	}
	
	public static void show(JPanel panel) {
		if (currentlyShowing != null) INSTANCE.remove(currentlyShowing);
		currentlyShowing = panel;
		INSTANCE.add(currentlyShowing, BorderLayout.CENTER);
		INSTANCE.revalidate();
	}
	
	public static float getFontSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.height >= 1200) {
			return 30f;
		} else if (screenSize.height >= 900) {
			return 25f;
		} else {
			return 20f;
		}
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new Frame().setVisible(true);
		
		Inspector.setActive(true);
	}
}
