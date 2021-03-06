package poopgame.gamelogic.engine.actions;

import java.util.Random;

import com.badlogic.ashley.core.Entity;

import poopgame.gamelogic.Player;
import poopgame.gamelogic.engine.TimeEngine;

public class Action {
	
	private static final Random RANDOM = new Random();
	
	private long id;
	private ActionType type;
	private long playerId;
	
	private long actionTime;
	
	public Action() {}
	
	public Action(ActionType type, long playerId, long actionTime) {
		this(RANDOM.nextLong(), type, playerId, actionTime);
	}
	
	public Action(long id, ActionType type, long playerId, long actionTime) {
		this.id = id;
		this.type = type;
		this.playerId = playerId;
		this.actionTime = actionTime;
	}
	
	public void execute(TimeEngine engine, Entity player) {
		Player.executeAction(engine, player, this);
	}
	
	public long getId() {
		return id;
	}
	
	public ActionType getType() {
		return type;
	}

	public long getPlayerId() {
		return playerId;
	}
	
	public long getActionTime() {
		return actionTime;
	}

	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}

}
