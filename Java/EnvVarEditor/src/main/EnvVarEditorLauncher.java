package main;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ui.EnvVarEditorPanel;

public class EnvVarEditorLauncher {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame frame = new JFrame("Environment Variable Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel content = new EnvVarEditorPanel();
		frame.setContentPane(content);
		frame.setMinimumSize(new Dimension(500, 0));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	
	
}
