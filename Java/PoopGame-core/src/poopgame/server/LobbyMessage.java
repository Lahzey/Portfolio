package poopgame.server;

import poopgame.gamelogic.Arena;

public class LobbyMessage {

	public Arena arena;
	public JoinMessage[] joinedPlayers;
	
	public LobbyMessage() {}

	public LobbyMessage(Arena arena, JoinMessage... joinedPlayers) {
		this.arena = arena;
		this.joinedPlayers = joinedPlayers;
	}

}
