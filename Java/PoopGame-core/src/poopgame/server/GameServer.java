package poopgame.server;

import poopgame.gamelogic.Arena;
import poopgame.gamelogic.components.PlayerComponent;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.gamelogic.engine.actions.Action;
import util.GeneralListener;

public interface GameServer {

	public static final int TCP_PORT = 29187;
	public static final int UDP_PORT = TCP_PORT + 1;
	
	void setEngine(TimeEngine engine);

	public Arena getArena();
	
	public PlayerComponent[] getPlayers();

	long getStartTime();

	public void addPlayer(PlayerComponent player);

	public boolean hasPlayer(PlayerComponent player);

	public void dispatchAction(ActionMessage actionRequest);
	
	public long estimateDelay();

	public void addReceiver(ActionReceiver receiver);

	void removeReceiver(ActionReceiver receiver);
	
	public void addLobbyUpdateListener(GeneralListener updateListener);
	
	public void removeLobbyUpdateListener(GeneralListener updateListener);

	public void resetStartTime();

	public void dispose();

	public static interface ActionReceiver {
		public void receive(Action action);
	}


}
