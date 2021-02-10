package com.creditsuisse.graphics.swing;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;
import javax.swing.border.Border;

public class SelectablePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private Border oldBorder;
	
	public SelectablePanel() {
		super();
		init();
	}

	public SelectablePanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		init();
	}

	public SelectablePanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		init();
	}

	public SelectablePanel(LayoutManager layout) {
		super(layout);
		init();
	}

	public void init(){
		setFocusTraversalKeysEnabled(true);
		setFocusable(true);
		addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				setBorder(oldBorder);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				oldBorder = getBorder();
				setBorder(new DashedBorder(Color.BLUE, 1, 5));
			}
		});
	}

}
