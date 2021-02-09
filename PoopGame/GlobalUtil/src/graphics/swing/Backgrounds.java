package graphics.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.UIManager;

import util.GeneralListener;

public class Backgrounds {

	public static void set(final Component c, final Color normalColor, final Color hoveredColor, final Color clickedColor){
		set(c, normalColor, hoveredColor, clickedColor, null);
	}
	
	public static void set(final Component c, final Color normalColor, final Color hoveredColor, final Color clickedColor, final GeneralListener changeListener){
		final Color defaultColor = UIManager.getColor("Panel.background");
		SwingUtil.addRecursiveMouseListener(c, new MouseAdapter() {
			
			private boolean isHovered;
			private boolean isClicked;
			
			@Override
			public void mouseReleased(MouseEvent e) {
				isClicked = false;
				setColor();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				isClicked = true;
				setColor();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				isHovered = false;
				setColor();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				isHovered = true;
				setColor();
			}
			
			private void setColor(){
				if(isClicked && clickedColor != null){
					c.setBackground(clickedColor);
				}else if(isHovered && hoveredColor != null){
					c.setBackground(hoveredColor);
				}else if(normalColor != null){
					c.setBackground(normalColor);
				}else{
					c.setBackground(defaultColor);
				}
				if(changeListener != null) changeListener.actionPerformed();
			}
		});
		c.setBackground(normalColor != null ? normalColor : defaultColor);
	}
}
