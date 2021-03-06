package poopgame.gamelogic;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import com.sun.glass.events.KeyEvent;

import poopgame.gamelogic.engine.actions.ActionType;

public class InputMap {

	private final Map<Integer, ActionType> keyMapping = new HashMap<>();
	private final Map<Integer, ActionType> mouseMapping = new HashMap<>();
	
	public InputMap() {
		// Initialise default values;
		setKeyMapping(KeyEvent.VK_A, ActionType.MOVE_LEFT_START);
		setKeyMapping(KeyEvent.VK_D, ActionType.MOVE_RIGHT_START);
		setKeyMapping(KeyEvent.VK_SPACE, ActionType.JUMP);
		setMouseMapping(MouseEvent.BUTTON1, ActionType.POOP_START);
		setKeyMapping(KeyEvent.VK_Q, ActionType.SPECIAL);
	}

	public ActionType getMouseMapping(int button) {
		return mouseMapping.get(button);
	}
	
	public void setMouseMapping(int button, ActionType actionType) {
		mouseMapping.put(button, actionType);
	}

	public ActionType getKeyMapping(int keyCode) {
		return keyMapping.get(keyCode);
	}
	
	public void setKeyMapping(int keyCode, ActionType actionType) {
		keyMapping.put(keyCode, actionType);
	}
	
	public void setMapping(int code, ActionType actionType, boolean isMouse) {
		if (isMouse) {
			mouseMapping.put(code, actionType);
		} else {
			keyMapping.put(code, actionType);
		}
	}
	
	public int getMappedInput(ActionType actionType) {
		for(int key : keyMapping.keySet()) {
			if (keyMapping.get(key) == actionType) {
				return key;
			}
		}

		for(int button : mouseMapping.keySet()) {
			if (mouseMapping.get(button) == actionType) {
				return button;
			}
		}
		
		return -1;
	}
	
	public boolean isMappedToMouse(ActionType actionType) {
		for(int button : mouseMapping.keySet()) {
			if (mouseMapping.get(button) == actionType) {
				return true;
			}
		}
		return false;
	}

}
