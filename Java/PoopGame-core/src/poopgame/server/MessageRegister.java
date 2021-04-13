package poopgame.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

import poopgame.gamelogic.Arena;
import poopgame.gamelogic.Champion;
import poopgame.gamelogic.Stats;
import poopgame.gamelogic.engine.actions.Action;
import poopgame.gamelogic.engine.actions.ActionType;

public class MessageRegister {

	public static Class<?>[] getRequestTypes() {
		return new Class<?>[] { JoinMessage.class, JoinMessage[].class, LeaveMessage.class, LobbyMessage.class, StartMessage.class, ActionMessage.class, StateUpdateMessage.class,
			Champion.class, Arena.class, Action.class, ActionType.class, HashMap.class, ArrayList.class, Stats.class, Object[].class, float[].class };
	}

	public static void registerAll(Kryo kryo) {
		for (Class<?> type : getRequestTypes()) {
			kryo.register(type);
		}
	}

}
