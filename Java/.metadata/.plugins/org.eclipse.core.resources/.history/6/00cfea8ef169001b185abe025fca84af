package com.creditsuisse.graphics.swing;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;


/**
 * A Frame to quickly display content in a one-liner
 * <br/>Example:
 * <br/>new TestFrame(new JLabel("test"));
 * <br/>setVisible is called in the constructor, preferred size is 500x500 and default close operation is EXIT_ON_CLOSE.
 * @author A469627
 *
 */
public class TestFrame extends JFrame{

	private static final long serialVersionUID = 1L;

	public TestFrame(Component... children){
		if(children.length > 1) setLayout(new MigLayout("fill, wrap 1", "[grow, fill]", ""));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setPreferredSize(new Dimension(1500, 500));
		addAll(children);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		Inspector.setActive(true);
	}
	
	public void addAll(Component... comps){
		for(Component comp : comps) add(comp);
	}
	
	@Override
	public Component add(Component comp) {
		super.add(comp);
		revalidate();
		repaint();
		return comp;
	}

	@Override
	public void add(Component comp, Object constraints) {
		super.add(comp, constraints);
		revalidate();
		repaint();
	}

	//Because Java 1.6 does not have this method
	public void revalidate() {
		invalidate();
		validate();
	}
}
