package poopgame.server;

import poopgame.gamelogic.Champion;

public class JoinMessage {

	public long playerId;
	public String name;
	public Champion champ;
	
	public JoinMessage() {}

	public JoinMessage(long playerId, String name, Champion champ) {
		this.playerId = playerId;
		this.name = name;	
		this.champ = champ;
	}

}
