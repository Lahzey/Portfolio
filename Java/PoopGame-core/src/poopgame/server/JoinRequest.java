package poopgame.server;

import poopgame.gamelogic.Champion;

public class JoinRequest {

	public long playerId;
	public String name;
	public Champion champ;
	
	public JoinRequest() {}

	public JoinRequest(long playerId, String name, Champion champ) {
		this.playerId = playerId;
		this.name = name;	
		this.champ = champ;
	}

}