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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.badlogic.gdx.Gdx;

import graphics.swing.Inspector;

public class SwingFrame extends JFrame implements AWTEventListener {
	private static final long serialVersionUID = 1L;
	
	private static Font FONT = null;
	public static final Color FOREGROUND = new Color(125, 56, 0);
	
	private static SwingFrame INSTANCE = null;
	private static final List<AWTEventListener> INPUT_PROCESSORS = new ArrayList<>();
	
	private JPanel contentPane = new JPanel(new BorderLayout());
	
	private JPanel gameContainer;

	public SwingFrame(JPanel gameContainer) {
		INSTANCE = this;
		this.gameContainer = gameContainer;
		add(contentPane, BorderLayout.CENTER);
		
		// There is a Bug in Swing / AWT where if you remove all focusable components the focus system breaks and key events stop working
		JPanel focusDummy = new JPanel();
		focusDummy.setFocusable(true);
		focusDummy.setMaximumSize(new Dimension());
		focusDummy.setPreferredSize(new Dimension());
		add(focusDummy, BorderLayout.SOUTH);

		Inspector.setActive(true);
        
		UIDefaults uiDefaults = UIManager.getDefaults();
		Font consolas = new Font("Consolas", Font.BOLD, 30);
		uiDefaults.put("Label.font", consolas);
		uiDefaults.put("Label.foreground", FOREGROUND);
		uiDefaults.put("Button.font", consolas);
		uiDefaults.put("Button.foreground", FOREGROUND);
		uiDefaults.put("TextField.foreground", FOREGROUND);
		uiDefaults.put("TextField.font", consolas);
        
		contentPane.add(new MainMenu());
		
		getToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
	}
	
	public static void showGame() {
		goTo(INSTANCE.gameContainer);
	}
	
	public static void goTo(JComponent menuComponent) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				INSTANCE.contentPane.removeAll();
				INSTANCE.contentPane.add(menuComponent); // replaces current one because of BorderLayout
				INSTANCE.revalidate();
				INSTANCE.repaint();
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
