package poopgame.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import com.badlogic.gdx.Gdx;

import graphics.swing.colors.Borders;
import graphics.swing.components.JImage;
import graphics.swing.components.SelectionPanel.SelectionDialog;
import net.miginfocom.swing.MigLayout;
import poopgame.gamelogic.Arena;
import poopgame.gamelogic.Champion;
import poopgame.gamelogic.PoopGame;
import poopgame.gamelogic.components.PlayerComponent;
import poopgame.server.GameServer;
import poopgame.server.LocalServer;
import poopgame.server.RemoteServer;
import util.ColorUtil;
import util.GeneralListener;

public class Lobby extends MenuPanel {
	private static final long serialVersionUID = 1L;

	private static final Random RANDOM = new Random();
	
	private GameServer server;
	private PlayerComponent activePlayer;
	private boolean joined;
	
	private JLabel arenaNameLabel;
	private JImage arenaImage;
	
	private JPanel playerList;
	
	private GeneralListener lobbyUpdateListener = () -> updateLobby();
	private GeneralListener startListener = () -> start();
	
	public Lobby(String host, String name, Champion champion) throws Exception  {
		this(host != null ? new RemoteServer(host) : new LocalServer(), new PlayerComponent(RANDOM.nextLong(), name, champion));
	}

	public Lobby(GameServer server, PlayerComponent activePlayer)  {
		super("wrap 2, fill, insets 50", "[grow]50px[right]", "");
		this.server = server;
		this.activePlayer = activePlayer;
		
		arenaNameLabel = new JLabel(server.getArena().getName());
		arenaNameLabel.setFont(arenaNameLabel.getFont().deriveFont(100f));
		add(arenaNameLabel);
		arenaImage = new JImage(server.getArena().getIcon());
		arenaImage.setFont(arenaImage.getFont().deriveFont(100f));
		if (server instanceof LocalServer) arenaImage.addActionListener(e -> changeArena());
		add(arenaImage);
		
		playerList = new JPanel(new MigLayout("wrap 3, fill, insets 50", "[]20px[grow, fill]20px[]", ""));
		playerList.setOpaque(false);
		add(playerList, "span 2, grow");
		
		MenuButton exitButton = new MenuButton("EXIT");
		exitButton.addActionListener(e -> showMainMenu());
		add(exitButton);
		MenuButton startButton = new MenuButton(server instanceof LocalServer ? "START" : "WAITING FOR HOST");
		startButton.setEnabled(server instanceof LocalServer);
		startButton.addActionListener(e -> start());
		add(startButton);
		
		server.addLobbyUpdateListener(lobbyUpdateListener);
		
		if (server instanceof RemoteServer) {
			((RemoteServer) server).addStartListener(startListener);
		}

		if (!server.hasPlayer(activePlayer)) {
			System.out.println(activePlayer.name + " joining server...");
			server.addPlayer(activePlayer);
		}
	}
	
	private void updateLobby() {
		System.out.println("updating lobby [" + server.getPlayers() + "]");
		boolean containsThisPlayer = false;
		
		playerList.removeAll();
		for (PlayerComponent player : server.getPlayers()) {
			if (player.id == activePlayer.id) containsThisPlayer = true;
			
			playerList.add(new JImage(player.champ.getIcon()));
			playerList.add(new JLabel(player.name));
			JImage kickButton = new JImage(FontAwesomeSolid.TIMES, ColorUtil.ERROR_FOREGROUND_COLOR);
			kickButton.generateStateImages();
			kickButton.setVisible((server instanceof LocalServer) && player.id != activePlayer.id);
			kickButton.addActionListener(e -> {
				if (server instanceof LocalServer) ((LocalServer) server).removePlayer(player.id);
			});
			playerList.add(kickButton);
		}
		playerList.revalidate();
		playerList.repaint();

		arenaNameLabel.setText(server.getArena().getName());
		arenaImage.setImage(server.getArena().getIcon());
		
		if (containsThisPlayer) {
			joined = true;
		} else if (joined) {
			joined = false;
			JOptionPane.showMessageDialog(this, "You have been removed from the Lobby.", "Kicked from Lobby", JOptionPane.ERROR_MESSAGE);
			showMainMenu();
		}
	}
	
	private void changeArena() {
		if (server instanceof LocalServer) {
			LocalServer localServer = (LocalServer) server;
			Arena newArena = new ArenaSelectionDialg().open();
			if (newArena != null) {
				localServer.setArena(newArena);
			}
		}
	}

	private void showMainMenu() {
		server.dispose();
		SwingFrame.goTo(new MainMenu());
	}

	private void start() {
		System.out.println("Starting Game...");
		server.removeLobbyUpdateListener(lobbyUpdateListener);
		if (server instanceof LocalServer) {
			((LocalServer) server).startLobby();
		} else {
			((RemoteServer) server).removeStartListener(startListener);
		}

		SwingFrame.showGame();
		Gdx.app.postRunnable(() -> {
			System.out.println("Setting Server & Player[" + activePlayer.id + "]...");
			PoopGame.getInstance().setServer(server);
			PoopGame.getInstance().setActivePlayer(activePlayer);
		});
	}
	
	private class ArenaSelectionDialg extends SelectionDialog<Arena> {
		private static final long serialVersionUID = 1L;

		public ArenaSelectionDialg() {
			super(arenaImage, true, Arena.values());
			selectionPanel.contentContainer.setBackground(new Color(93, 105, 73));
			selectionPanel.remove(selectionPanel.searchField);
		}

		@Override
		public Component createComponent(Arena element) {
			JPanel arenaPanel = new JPanel(new BorderLayout());
			arenaPanel.setOpaque(false);
			arenaPanel.add(new JImage(element.getIcon()), BorderLayout.CENTER);
			arenaPanel.add(new JLabel(element.getName()), BorderLayout.SOUTH);
			Border hoveredBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, SwingFrame.FOREGROUND, SwingFrame.FOREGROUND.darker().darker());
			Border clickedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, SwingFrame.FOREGROUND, SwingFrame.FOREGROUND.darker().darker());
			Borders.set(arenaPanel, null, hoveredBorder, clickedBorder);
			return arenaPanel;
		}

		@Override
		public boolean matchesFilter(Arena element, String filter) {
			return element.getName().toLowerCase().contains(filter.toLowerCase());
		}
		
	}

}
