package poopgame.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import graphics.swing.components.JImage;
import graphics.swing.components.KeyChooseButton;
import poopgame.gamelogic.Champion;
import poopgame.gamelogic.PoopGame;
import poopgame.gamelogic.engine.actions.ActionType;

public class ChampSelection extends MenuPanel {
	private static final long serialVersionUID = 1L;
	
	private int selectedChampIndex = 0;
	private Champion[] champs = Champion.values();
	
	private MenuTextField ipInput = null;
	private JLabel champNameLabel;
	private JImage champImage;
	private MenuTextField nameInput;
	private JLabel errorLabel;

	public ChampSelection(boolean showIpInput) {
		super("wrap 2, fill, insets 50", "[grow, right]50px[grow, left]", (showIpInput ? "[][]20px" : "")  + "[][grow, fill][][][][][][][]");
		
		if (showIpInput) {
			add(new JLabel("IP ADRESS:"), "span 2, center");
			ipInput = new MenuTextField("localhost");
			add(ipInput, "span 2, center");
		}
		
		JPanel switchPanel = new JPanel(new BorderLayout());
		switchPanel.setOpaque(false);
		MenuButton prevButton = new MenuButton("<");
		prevButton.addActionListener(e -> changeChamp(true));
		switchPanel.add(prevButton, BorderLayout.WEST);
		champNameLabel = new JLabel(champs[selectedChampIndex].getName());
		champNameLabel.setFont(champNameLabel.getFont().deriveFont(100f));
		champNameLabel.setHorizontalAlignment(JLabel.CENTER);
		switchPanel.add(champNameLabel, BorderLayout.CENTER);
		MenuButton nextButton = new MenuButton(">");
		nextButton.addActionListener(e -> changeChamp(false));
		switchPanel.add(nextButton, BorderLayout.EAST);
		
		add(switchPanel, "span 2, grow");

		champImage = new JImage(champs[selectedChampIndex].getSplash());
		add(champImage, "span 2, grow");
		
		nameInput = new MenuTextField("Name");
		add(nameInput, "span 2, grow");
		
		add(new JLabel("MOVE LEFT"));
		add(generateKeyChooseButton(ActionType.MOVE_LEFT_START));
		
		add(new JLabel("MOVE RIGHT"));
		add(generateKeyChooseButton(ActionType.MOVE_RIGHT_START));
		
		add(new JLabel("JUMP ")); // font cuts off part of trailing P
		add(generateKeyChooseButton(ActionType.JUMP));
		
		add(new JLabel("POOP ")); // font cuts off part of trailing P
		add(generateKeyChooseButton(ActionType.POOP_START));
		
		add(new JLabel("SPECIAL"));
		add(generateKeyChooseButton(ActionType.SPECIAL));
		
		errorLabel = new JLabel();
		errorLabel.setVisible(false);
		add(errorLabel, "span 2, center");
		
		MenuButton backButton = new MenuButton("BACK");
		backButton.addActionListener(e -> SwingFrame.goTo(new MainMenu()));
		add(backButton, "left");
		MenuButton confirmButton = new MenuButton("CONFIRM");
		confirmButton.addActionListener(e -> showLobby());
		add(confirmButton, "right");
	}
	
	private void changeChamp(boolean previous) {
		selectedChampIndex += previous ? -1 : 1;
		if (selectedChampIndex >= champs.length) {
			selectedChampIndex = 0;
		} else if (selectedChampIndex < 0) {
			selectedChampIndex = champs.length - 1;
		}
		
		Champion champ = champs[selectedChampIndex];
		champNameLabel.setText(champ.getName());
		champImage.setImage(champ.getSplash());
	}
	
	private KeyChooseButton generateKeyChooseButton(ActionType actionType) {
		int code = PoopGame.INPUT_MAP.getMappedInput(actionType);
		boolean isMouse = PoopGame.INPUT_MAP.isMappedToMouse(actionType);
		
		KeyChooseButton button = new KeyChooseButton(code, true, true);
		button.setKeyCode(code, isMouse);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setOpaque(false);
		button.setHorizontalAlignment(JButton.LEFT);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setChooseText("press any key...");
		button.setForeground(new Color(60, 115, 87));
		
		button.addChooseListener(() -> PoopGame.INPUT_MAP.setMapping(button.getKeyCode(), actionType, button.isMouse()));
		
		return button;
	}

	private void showLobby() {
		String ip = ipInput == null ? null : ipInput.getText();
		try {
			SwingFrame.goTo(new Lobby(ip, nameInput.getText(), champs[selectedChampIndex]));
		} catch (Exception e) {
			e.printStackTrace();
			errorLabel.setText(ip == null ? "Failed to create local server." : "Failed to connect to remote server at " + ip + ".");
			errorLabel.setVisible(true);
		}
	}

}
