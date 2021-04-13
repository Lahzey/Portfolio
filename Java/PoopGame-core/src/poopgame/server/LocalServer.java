package poopgame.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import poopgame.gamelogic.Arena;
import poopgame.gamelogic.PoopGame;
import poopgame.gamelogic.components.PlayerComponent;
import poopgame.gamelogic.engine.TimeEngine;
import util.GeneralListener;
import util.LoopThread;

public class LocalServer extends Listener implements GameServer {
	
	private Server server = new Server(65536, 65536);
	
	private Arena arena = Arena.SEWER;
	private Map<Long, PlayerComponent> playerMap = new HashMap<Long, PlayerComponent>();
	private List<PlayerComponent> players = new ArrayList<>(); // just to keep the players the order they joined in

	private long startTime = 0;
	
	private List<GeneralListener> lobbyUpdateListeners = new ArrayList<>();
	private List<ActionReceiver> actionReceivers = new ArrayList<>();
	
	private LoopThread stateUpdateSender;
	private TimeEngine engine;
	
	public LocalServer() {
		try {
			server.start();
			server.bind(TCP_PORT, UDP_PORT);
			server.addListener(this);
		    MessageRegister.registerAll(server.getKryo());
		} catch (IOException e) {
			throw new RuntimeException("Failed to start local server.", e);
		}
	}
	
	@Override
	public void setEngine(TimeEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public Arena getArena() {
		return arena;
	}
	
	public void setArena(Arena arena) {
		this.arena = arena;
		sendLobbyUpdate();
	}

	@Override
	public PlayerComponent[] getPlayers() {
		return players.toArray(new PlayerComponent[players.size()]);
	}

	@Override
	public boolean hasPlayer(PlayerComponent player) {
		return players.contains(player);
	}

	@Override
	public void addPlayer(PlayerComponent player) {
		players.add(player);
		playerMap.put(player.id, player);
		sendLobbyUpdate();
	}

	public void removePlayer(long playerId) {
		PlayerComponent removedPlayer = playerMap.remove(playerId);
		if (removedPlayer != null) {
			players.remove(removedPlayer);
		}
		sendLobbyUpdate();
	}

	public void removeAllPlayers() {
		playerMap.clear();
		players.clear();
		sendLobbyUpdate();
	}
	
	private void sendLobbyUpdate() {
		JoinMessage[] joinedPlayers = new JoinMessage[players.size()];
		for (int i = 0; i < players.size(); i++) {
			PlayerComponent joinedPlayer = players.get(i);
			joinedPlayers[i] = new JoinMessage(joinedPlayer.id, joinedPlayer.name, joinedPlayer.champ);
		}
		LobbyMessage lobbyUpdate = new LobbyMessage(arena, joinedPlayers);
		
		for (Connection connection : server.getConnections()) {
			connection.sendTCP(lobbyUpdate);
		}
		
		for (GeneralListener lobbyUpdateListener : new ArrayList<>(lobbyUpdateListeners)) {
			lobbyUpdateListener.actionPerformed();
		}
	}
	
	@Override
	public long getStartTime() {
		return startTime;
	}
	
	@Override
	public void resetStartTime() {
		startTime = 0;
	}

	public void startLobby() {
		startTime = System.currentTimeMillis() + 1000;
		
		StartMessage startSignal = new StartMessage(startTime);
		for (Connection connection : server.getConnections()) {
			connection.sendTCP(startSignal);
		}
		
		stateUpdateSender = new LoopThread(2, 2000) {
			
			@Override
			public void loopedRun() {
				if (startTime > 0 && engine != null) {
					StateUpdateMessage stateUpdate = engine.createStateUpdate();
					if (stateUpdate == null) {
						return;
					}
					
					for (Connection connection : server.getConnections()) {
						connection.sendUDP(stateUpdate);
					}
				}
			}
		};
		stateUpdateSender.start();
	}

	@Override
	public void dispatchAction(ActionMessage actionRequest) {
		PoopGame.getInstance().executeAfterNextUpdate(() -> {
			for (ActionReceiver actionReceiver : new ArrayList<>(actionReceivers)) {
				actionReceiver.receive(actionRequest.action);
			}
			
			for (Connection connection : server.getConnections()) {
				connection.sendUDP(actionRequest);
			}
		});
	}

	@Override
	public long estimateDelay() {
		return 0;
	}

	@Override
	public void addReceiver(ActionReceiver receiver) {
		actionReceivers.add(receiver);
	}

	@Override
	public void removeReceiver(ActionReceiver receiver) {
		actionReceivers.remove(receiver);
	}

	@Override
	public void dispose() {
		if (stateUpdateSender != null) {
			stateUpdateSender.terminate();
			stateUpdateSender = null;
		}
		server.stop();
	}
	
	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof JoinMessage) {
			JoinMessage joinRequest = (JoinMessage) object;
			PlayerComponent player = new PlayerComponent(joinRequest.playerId, joinRequest.name, joinRequest.champ); // input map not relevant for remote players
			addPlayer(player);
		} else if (object instanceof LeaveMessage) {
			LeaveMessage leaveRequest = (LeaveMessage) object;
			removePlayer(leaveRequest.playerId);
		} else if (object instanceof ActionMessage) {
			ActionMessage actionRequest = (ActionMessage) object;
			dispatchAction(actionRequest);
		}
	}

	@Override
	public void addLobbyUpdateListener(GeneralListener updateListener) {
		lobbyUpdateListeners.add(updateListener);
	}
	
	@Override
	public void removeLobbyUpdateListener(GeneralListener updateListener) {
		lobbyUpdateListeners.remove(updateListener);
	}

}
