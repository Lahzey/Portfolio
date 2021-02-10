package poopgame.desktop;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import graphics.swing.Inspector;
import poopgame.ui.SwingFrame;

public class SwingLauncher {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace(); // not critical
		}
		
		// needs to be done first to initialize GDX resource management
		JPanel gameContainer = new JPanel(new BorderLayout());
		SwingWrapper wrapper = new SwingWrapper();
        gameContainer.add(wrapper.getCanvas(), BorderLayout.CENTER);
        
		SwingFrame swingFrame = new SwingFrame(gameContainer);
		swingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		swingFrame.setSize(1000, 1000); // used when exiting maximized state
		swingFrame.setExtendedState(swingFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		swingFrame.setVisible(true);
		
		Inspector.setActive(true);
	}
	
}
