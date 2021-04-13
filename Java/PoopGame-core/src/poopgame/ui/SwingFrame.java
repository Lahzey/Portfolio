package poopgame.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.AWTEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.badlogic.gdx.Gdx;

import graphics.swing.Inspector;
import graphics.swing.layouts.SwapLayout;
import poopgame.util.InternalAssetLoader;
import util.LoopThread;

public class SwingFrame extends JFrame implements AWTEventListener {
	private static final long serialVersionUID = 1L;
	
	private static Font FONT = null;
	public static final Color FOREGROUND = new Color(125, 56, 0);
	
	private static SwingFrame INSTANCE = null;
	private static final List<AWTEventListener> INPUT_PROCESSORS = new ArrayList<>();
	
//	private MigLayout contentLayout = new MigLayout("fill, wrap 1, insets 0", "[grow, fill]", "");
	private SwapLayout contentLayout = new SwapLayout(200);
	private JPanel contentPane = new JPanel(contentLayout);

	private JPanel uiContainer;
	private JPanel gameContainer;

	public SwingFrame(JPanel gameWrapper) {
		INSTANCE = this;
		setUIDefaults();
		
		// There is a Bug in Swing / AWT where if you remove all focusable components the focus system breaks and key events stop working
		JPanel focusDummy = new JPanel();
		focusDummy.setFocusable(true);
		focusDummy.setMaximumSize(new Dimension());
		focusDummy.setPreferredSize(new Dimension());
		add(focusDummy, BorderLayout.SOUTH);
		
		MenuPanel loadingPanel = new MenuPanel("fill", "[grow, center]", "[grow, center]");
		loadingPanel.add(new JLabel("Loading..."));
		contentPane.add(loadingPanel);
		
		uiContainer = new JPanel(new BorderLayout());
		uiContainer.add(new MainMenu());
		contentPane.add(uiContainer);
		
		gameContainer = new JPanel(new BorderLayout());
		gameContainer.add(gameWrapper);
		contentPane.add(gameContainer);

		add(contentPane, BorderLayout.CENTER);

		getToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
		Inspector.setActive(true);
		
		new LoopThread(10) {
			
			@Override
			public void loopedRun() {
				if (InternalAssetLoader.isInitialised()) {
					contentPane.remove(loadingPanel);
					contentPane.revalidate();
					contentPane.repaint();
					terminate();
				}
			}
		}.start();
	}
	
	private void setShowGame(boolean showGame) {
		int componentIndex = showGame ? 1 : 0;
		if (contentLayout.getCurrentComponentIndex() != componentIndex) {
			contentLayout.swap(contentPane, showGame ? 1 : 0);
		}
	}
	
	private static void setUIDefaults() {
		UIDefaults uiDefaults = UIManager.getDefaults();
		Font consolas = new Font("Consolas", Font.BOLD, 30);
		uiDefaults.put("Label.font", consolas);
		uiDefaults.put("Label.foreground", FOREGROUND);
		uiDefaults.put("Button.font", consolas);
		uiDefaults.put("Button.foreground", FOREGROUND);
		uiDefaults.put("TextField.foreground", FOREGROUND);
		uiDefaults.put("TextField.font", consolas);
	}
	
	public static void showGame() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				INSTANCE.setShowGame(true);
			}
		});
	}
	
	public static void goTo(JComponent menuComponent) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				INSTANCE.setShowGame(false);
				INSTANCE.uiContainer.removeAll();
				INSTANCE.uiContainer.add(menuComponent); // replaces current one because of BorderLayout
				INSTANCE.uiContainer.revalidate();
				INSTANCE.uiContainer.repaint();
			}
		});
	}

	public static Font getGraffitiFont(float size) {
		if (FONT == null) {
			try {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				FONT = Font.createFont(Font.TRUETYPE_FONT, Gdx.files.internal("ui/graffiti.ttf").read());
				ge.registerFont(FONT);
			} catch (IOException | FontFormatException e) {
				e.printStackTrace();
			}
		}
		return FONT.deriveFont(Font.BOLD, size);
	}
	
	public static void addInputProcessor(AWTEventListener inputProcessor) {
		INPUT_PROCESSORS.add(inputProcessor);
	}

	public static void removeInputProcessor(AWTEventListener inputProcessor) {
		INPUT_PROCESSORS.remove(inputProcessor);
	}
	
	public static SwingFrame getInstance() {
		return INSTANCE;
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		for (AWTEventListener inputProcessor : INPUT_PROCESSORS) {
			inputProcessor.eventDispatched(event);
		}
	}
	
}
