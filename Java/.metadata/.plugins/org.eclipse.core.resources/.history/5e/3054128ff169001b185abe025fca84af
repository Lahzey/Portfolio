package com.creditsuisse.graphics.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class ColorChangeMouseListener extends MouseAdapter{

	public Color defaultColor;
	public Color hoverColor;
	public Color pressedColor;
	
	private boolean pressed;
	private boolean hover;
	
	public ColorChangeMouseListener(Color defaultColor, Color hoverColor, Color pressedColor){
		this.defaultColor = defaultColor;
		this.hoverColor = hoverColor;
		this.pressedColor = pressedColor;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		pressed = true;
		setColor(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
		setColor(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		hover = true;
		setColor(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		hover = false;
		setColor(e);
	}
	
	private void setColor(MouseEvent e){
		if(pressed){
			setBackground(e.getComponent(), pressedColor);
		}else if(hover){
			setBackground(e.getComponent(), hoverColor);
		}else{
			setBackground(e.getComponent(), defaultColor);
		}
	}
	
	private void setBackground(Component component, Color color){
		if(component instanceof JComponent){
			JComponent jcomp = (JComponent) component;
			jcomp.setOpaque(color != null);
		}
		component.setBackground(color);
	}

}
