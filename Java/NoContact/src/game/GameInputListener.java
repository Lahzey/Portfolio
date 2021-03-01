package game;

import java.awt.event.MouseEvent;

public interface GameInputListener {
	
	public void mousePressed(MouseEvent e, float x, float y, int button);

	public void mouseMoved(MouseEvent e, float x, float y);

}
