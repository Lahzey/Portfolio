package poopgame.server;

import poopgame.gamelogic.Arena;

public class LobbyUpdate {

	public Arena arena;
	public JoinRequest[] joinedPlayers;
	
	public LobbyUpdate() {}

	public LobbyUpdate(Arena arena, JoinRequest... joinedPlayers) {
		this.arena = arena;
		this.joinedPlayers = joinedPlayers;
	}

}
