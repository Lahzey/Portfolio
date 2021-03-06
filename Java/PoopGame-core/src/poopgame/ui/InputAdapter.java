package poopgame.ui;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public abstract class InputAdapter implements AWTEventListener {

	// to prevent multiple firing of events when holding a key for too long
	private Set<Integer> currentlyPressedKeys = new HashSet<>();
	private Set<Integer> currentlyPressedMouseButtons = new HashSet<>();

	@Override
	public void eventDispatched(AWTEvent event) {
		if (event.getID() == MouseEvent.MOUSE_PRESSED) {
			MouseEvent mouseEvent = (MouseEvent) event;
			int mouseButton = mouseEvent.getButton();
			if (!currentlyPressedMouseButtons.contains(mouseButton)) {
				currentlyPressedMouseButtons.add(mouseButton);
				mousePressed(mouseButton);
			}
		} else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
			MouseEvent mouseEvent = (MouseEvent) event;
			int mouseButton = mouseEvent.getButton();
			if (currentlyPressedMouseButtons.contains(mouseButton)) {
				currentlyPressedMouseButtons.remove(mouseButton);
				mouseReleased(mouseButton);
			}
		} else if (event.getID() == KeyEvent.KEY_PRESSED) {
			KeyEvent keyEvent = (KeyEvent) event;
			int keyCode = keyEvent.getKeyCode();
			if (!currentlyPressedKeys.contains(keyCode)) {
				currentlyPressedKeys.add(keyCode);
				keyPressed(keyCode);
			}
		} else if (event.getID() == KeyEvent.KEY_RELEASED) {
			KeyEvent keyEvent = (KeyEvent) event;
			int keyCode = keyEvent.getKeyCode();
			if (currentlyPressedKeys.contains(keyCode)) {
				currentlyPressedKeys.remove(keyCode);
				keyReleased(keyCode);
			}
		}
	}

	public abstract void mousePressed(int mouseButton);

	public abstract void mouseReleased(int mouseButton);

	public abstract void keyPressed(int keyCode);

	public abstract void keyReleased(int keyCode);

}
