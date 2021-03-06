package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;

import graphics.swing.components.JAnimationPanel;
import graphics.swing.components.JImage;
import net.miginfocom.swing.MigLayout;

public class GuidePanel extends JAnimationPanel {
	private static final long serialVersionUID = 1L;

	private static Color TEXT_COLOR = new Color(179, 109, 0);
	
	private JAnimationPanel content;
	
	public GuidePanel() {
		super(new MigLayout("insets 50px, fill, wrap 2, hidemode 3", "[]20px[grow, fill]", "[][grow, fill]"));
		setOpaque(false);
		
		generateContent();
		
		JImage infoButton = new JImage(FontAwesomeRegular.QUESTION_CIRCLE, TEXT_COLOR);
		infoButton.setFont(Assets.FONT.deriveFont(Frame.getFontSize()));
		infoButton.addActionListener(e -> toggleContent());
		add(infoButton);
		add(new MenuLabel("GUIDE", 1.3f));
		
		add(content, "span 2, grow");
		content.setVisible(false);
	}
	
	private void toggleContent() {
		if (content.isVisible()) {
			content.close(200, JAnimationPanel.VERTICAL);
		} else {
			content.open(200, JAnimationPanel.VERTICAL);
		}
	}
	
	private void generateContent() {
		content = new JAnimationPanel(new MigLayout("insets 0, fill, wrap 1", "[grow, fill]", "")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(255, 255, 255, 100));
				g.fillRect(5, 0, getWidth() - 5, getHeight());
				super.paintComponent(g);
			}
		};
		content.setOpaque(false);
		content.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(100, 50, 0, 100)), BorderFactory.createEmptyBorder(10, 10, 10, 0)));
		
		String text = "The goal of this game is to survive as long as possible by preventing people from touching each other.";
		
		text += "\n\nPeople will spawn from the left and top boxes and walk to the counterparts with the same color.";
		text += "\nYou can make them take detours to avoid other people. To do this, click on them and then click anywhere to make them walk there before continuing to their destination.";
		
		text += "\n\nIf a person is contagious and touches another, it will infect that person.";
		text += "\nThe number on the top left tells you how high the chance for a person to be contagious is.";
		text += "\nIt will go up with every infection and increases over time, back to the initial amout.";
		text += "\nOnce it reaches 100% it's Game Over.";

		text += "\n\nYou may pick one of the predefined difficulties or customize it yourself through the settings.";
		
		JTextArea contentLabel = new JTextArea(text);
		contentLabel.setFont(new Font("Arial", Font.BOLD, (int) (Frame.getFontSize() * 0.6f)));
		contentLabel.setForeground(Color.BLACK);
		contentLabel.setWrapStyleWord(true);
		contentLabel.setLineWrap(true);
        contentLabel.setEditable(false);
        contentLabel.setFocusable(false);
        contentLabel.setOpaque(false);
		
		JScrollPane scroll = new JScrollPane(contentLabel);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		content.add(scroll);
	}

}
