package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ui.Images;
import ui.SearchInputPanel;

public class FileSearchLauncher {
	
	private static final String TITLE = "Java File Search";
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JFrame frame = new JFrame();
				frame.setIconImage(Images.APPLICATION_ICON);
				frame.setTitle(TITLE);
				frame.add(new SearchInputPanel(SearchConfig.getSearch()));
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
		    }
		});
	}
//	
}
