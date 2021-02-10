package ui;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import graphics.swing.DashedBorder;
import graphics.swing.FileDrop;
import graphics.swing.JAnimationPanel;
import util.ColorUtil;

import net.miginfocom.swing.MigLayout;

public class AddServerPanel extends JAnimationPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel label = new JLabel("Drop a server or click here.");
	private FileDrop.Listener listener;
	private FileDrop fileDrop;
	
	public AddServerPanel(final FileDrop.Listener listener){
		super(new MigLayout("fill", "[center]", "[center]"));
		setListener(listener);
		label.setForeground(ColorUtil.INFO_FOREGROUND_COLOR);
		setBackground(ColorUtil.INFO_BACKGROUND_COLOR);
		setBorder(new DashedBorder(ColorUtil.INFO_BORDER_COLOR, 2, 5));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(label);
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(AddServerPanel.this.listener != null) AddServerPanel.this.listener.filesDropped(null);
			}
		});
	}
	
	public void setListener(FileDrop.Listener listener){
		if(fileDrop != null){
			FileDrop.remove(this);
		}
		this.listener = listener;
		fileDrop = new FileDrop(this, listener);
	}
}
