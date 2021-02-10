package poopgame.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import poopgame.gamelogic.Arena;
import poopgame.gamelogic.PoopGame;
import poopgame.gamelogic.components.PlayerComponent;
import poopgame.gamelogic.engine.TimeEngine;
import util.GeneralListener;

public class RemoteServer extends Listener implements GameServer {
	
	private Client client = new Client(65536, 65536);
	private PlayerComponent activePlayer = null;

	private Arena arena = Arena.SEWER;
	private Map<Long, PlayerComponent> playerMap = new HashMap<Long, PlayerComponent>();
	private List<PlayerComponent> players = new ArrayList<>(); // just to keep the players the order they joined in
	
	private long startTime = 0;
	
	private List<GeneralListener> lobbyUpdateListeners = new ArrayList<>();
	private List<GeneralListener> startListeners = new ArrayList<>();
	private List<ActionReceiver> actionReceivers = new ArrayList<>();

	private TimeEngine engine;
	
	private Map<Long, Long> sendTimes = new HashMap<>();
	private long lastDelay = 0;
	
	public RemoteServer(String host) throws Exception {
		client.start();
		client.connect(60000, host, TCP_PORT, UDP_PORT);
		client.addListener(this);
	    RequestRegister.registerAll(client.getKryo());
	}
	
	@Override
	public void setEngine(TimeEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public Arena getArena() {
		return arena;
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
	public long getStartTime() {
		return startTime;
	}

	@Override
	public void dispatchAction(ActionRequest actionRequest) {
		sendTimes.put(actionRequest.action.getId(), System.currentTimeMillis());
		client.sendUDP(actionRequest);
	}

	@Override
	public long estimateDelay() {
		return lastDelay;
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
		if (activePlayer != null) {
			client.sendTCP(new LeaveRequest(activePlayer.id));
		}
		
		client.stop();
	}

	@Override
	public void addPlayer(PlayerComponent player) {
		if (activePlayer == null) {
			activePlayer = player;
			players.add(player);
			playerMap.put(player.id, player);
			client.sendTCP(new JoinRequest(player.id, player.name, player.champ));
		}
	}
	
	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof LobbyUpdate) {
			LobbyUpdate lobbyUpdate = (LobbyUpdate) object;
			
			players.clear();
			for (JoinRequest joinedPlayer : lobbyUpdate.joinedPlayers) {
				PlayerComponent player;
				if (playerMap.containsKey(joinedPlayer.playerId)) {
					player = playerMap.get(joinedPlayer.playerId);
					player.name = joinedPlayer.name;
					player.champ = joinedPlayer.champ;
				} else {
					player = new PlayerComponent(joinedPlayer.playerId, joinedPlayer.name, joinedPlayer.champ);
					playerMap.put(player.id, player);
				}
				players.add(player);
			}
			
			arena = lobbyUpdate.arena;

			for (GeneralListener lobbyUpdateListener : new ArrayList<>(lobbyUpdateListeners)) {
				lobbyUpdateListener.actionPerformed();
			}
		} else if (object instanceof StartSignal) {
			StartSignal startSignal = (StartSignal) object;
			startTime = startSignal.startTime;
			
			for (GeneralListener startListener : new ArrayList<>(startListeners)) {
				startListener.actionPerformed();
			}
		} else if (object instanceof ActionRequest) {
			ActionRequest actionRequest = (ActionRequest) object;
			
			PoopGame.getInstance().executeAfterNextUpdate(() -> {
				Long sendTime = sendTimes.remove(actionRequest.action.getId());
				if (sendTime != null) {
					lastDelay = System.currentTimeMillis() - sendTime;
				}
				
				for (ActionReceiver actionReceiver : new ArrayList<>(actionReceivers)) {
					actionReceiver.receive(actionRequest.action);
				}
			});
		} else if (object instanceof StateUpdate) {
			if (engine != null) {
				engine.applyStateUpdate((StateUpdate) object);
			}
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

	public void addStartListener(GeneralListener startListener) {
		startListeners.add(startListener);
	}

	public void removeStartListener(GeneralListener startListener) {
		startListeners.remove(startListener);
	}

}