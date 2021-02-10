package poopgame.ui;

import javax.swing.JLabel;

import com.badlogic.ashley.core.Entity;

import poopgame.gamelogic.components.PlayerComponent;
import poopgame.server.GameServer;

public class WinScreen extends MenuPanel {
	private static final long serialVersionUID = 1L;
	
	private GameServer server;
	private Long activePlayerId;

	public WinScreen(Entity winner, GameServer server, Long activePlayerId) {
		super("wrap 2, fill, insets 50", "[grow]50px[right]", "[grow, center][]");
		this.server = server;
		this.activePlayerId = activePlayerId;
		
		PlayerComponent winnerComp = winner != null ? winner.getComponent(PlayerComponent.class) : null;
		JLabel winnerLabel = new JLabel(winner != null ?  winnerComp.name + " (" + winnerComp.champ.getName() + ") WON" : "TIE");
		winnerLabel.setFont(winnerLabel.getFont().deriveFont(100f));
		add(winnerLabel, "span 2, center");
		
		MenuButton exitButton = new MenuButton("EXIT");
		exitButton.addActionListener(e -> showMainMenu());
		add(exitButton);
		MenuButton startButton = new MenuButton("RETURN TO LOBBY");
		startButton.addActionListener(e -> returnToLobby());
		add(startButton);
	}

	private void returnToLobby() {
		for (PlayerComponent player : server.getPlayers()) {
			if (player.id == activePlayerId) {
				SwingFrame.goTo(new Lobby(server, player));
				return;
			}
		}
		
		// in case the player cannot be found again
		showMainMenu();
	}

	private void showMainMenu() {
		server.dispose();
		SwingFrame.goTo(new MainMenu());
	}

}