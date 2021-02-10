package graphics.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import net.miginfocom.swing.MigLayout;

public class CollapsiblePanel extends JPanel {

	private static final int COLLAPSE_DURATION = 200;

	private boolean collapsed;

	private JImage collapseButton;
	private JLabel titleLabel;
	private JAnimationPanel contentPanel = new JAnimationPanel(new BorderLayout());

	public CollapsiblePanel(Component content, String title, boolean collapsed) {
		super.setLayout(new MigLayout("fill, wrap 2", "[]5px[grow, fill]", "[]7px[]"));
		this.collapsed = collapsed;
		collapseButton = new JImage(collapsed ? FontAwesomeSolid.CARET_RIGHT : FontAwesomeSolid.CARET_DOWN);
		collapseButton.generateStateImages();
		collapseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCollapsed(!CollapsiblePanel.this.collapsed);
			}
		});
		super.add(collapseButton);
		titleLabel = new JLabel(title);
		titleLabel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				collapseButton.doClick();
			}
		});
		titleLabel.setCursor(collapseButton.getCursor());
		super.add(titleLabel);

		contentPanel.setOpaque(false);
		contentPanel.add(content);
		super.add(contentPanel, "span 2, grow");

		if (collapsed) {
			contentPanel.setVisible(false);
		}
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
		collapseButton.setIcon(collapsed ? FontAwesomeSolid.CARET_RIGHT : FontAwesomeSolid.CARET_DOWN);
		collapseButton.generateStateImages();
		contentPanel.finishAnimations();
		if (collapsed) {
			contentPanel.close(COLLAPSE_DURATION, JAnimationPanel.VERTICAL);
		} else {
			contentPanel.open(COLLAPSE_DURATION, JAnimationPanel.VERTICAL);
		}
	}
	
	public boolean isCollapsed() {
		return collapsed;
	}
	
	public void setTitle(String title) {
		titleLabel.setText(title);
	}
	
	public String getTitle() {
		return titleLabel.getText();
	}

}
