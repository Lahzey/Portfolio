package poopgame.server;

import poopgame.gamelogic.engine.actions.Action;

public class ActionMessage {

	public Action action;
	
	public ActionMessage() {}
	
	public ActionMessage(Action action) {
		this.action = action;
	}
	
}
