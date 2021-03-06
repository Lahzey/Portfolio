package ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.math.NumberUtils;

import game.Difficulty;
import game.Game;
import graphics.swing.RoundedMatteBorder;
import net.miginfocom.swing.MigLayout;

public class MainMenu extends MenuPanel {
	private static final long serialVersionUID = 1L;
	
	private MenuInput doorCount = new MenuInput("", SwingConstants.RIGHT);
	private MenuInput moveSpeed = new MenuInput("", SwingConstants.RIGHT);
	private MenuInput spawnCooldown = new MenuInput("", SwingConstants.RIGHT);
	private MenuInput cureSpeed = new MenuInput("", SwingConstants.RIGHT);
	private MenuInput initialInfectionRate = new MenuInput("", SwingConstants.RIGHT);
	private MenuInput increasePerInfection = new MenuInput("", SwingConstants.RIGHT);

	
	public MainMenu() {
		super(new MigLayout("fill, wrap 1", "50px[grow, fill]50px", "20px[top]20px[top]50px[grow, bottom]20px[]50px[grow, top]"));
		setBackground(Color.BLACK);
		
		add(new MenuLabel("No Contact", 2.5f, SwingConstants.CENTER));
		add(new MenuLabel("Fight back against those Plague Inc. Players", 1.3f, SwingConstants.CENTER));
		
		JPanel settingsPanel = new JPanel(new MigLayout("wrap 2", "[]20px[grow, fill]", ""));
		settingsPanel.setOpaque(false);
		settingsPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 50, 0, 100), 5));
		
		settingsPanel.add(new MenuLabel("SETTINGS", 1.5f, SwingConstants.CENTER), "span 2, grow");
		
		JPanel difficultiesPanel = new JPanel(new MigLayout("fill", "[grow, fill]", ""));
		difficultiesPanel.setOpaque(false);
		for (Difficulty difficulty : Difficulty.values()) {
			MenuButton button = new MenuButton(difficulty.name(), e -> {
				difficulty.set();
				loadInputValues();
			});
			button.setBorderPainted(true);
			button.setBorder(new RoundedMatteBorder(2, button.getFontMetrics(button.getFont()).getHeight() / 2, difficulty.getBorderColor()));
			button.setForeground(difficulty.getForegroundColor());
			difficultiesPanel.add(button);
		}
		settingsPanel.add(difficultiesPanel, "span 2, grow");
		
		settingsPanel.add(new MenuLabel("Amount of Doors"));
		settingsPanel.add(doorCount);
		
		settingsPanel.add(new MenuLabel("Speed of People"));
		settingsPanel.add(moveSpeed);
		
		settingsPanel.add(new MenuLabel("Spawn Cooldown in Milliseconds"));
		settingsPanel.add(spawnCooldown);
		
		settingsPanel.add(new MenuLabel("Cured % per minute"));
		settingsPanel.add(cureSpeed);
		
		settingsPanel.add(new MenuLabel("Initial infection rate in %"));
		settingsPanel.add(initialInfectionRate);
		
		settingsPanel.add(new MenuLabel("Rate increase per infection in %"));
		settingsPanel.add(increasePerInfection);
		
		add(settingsPanel);
		
		add(new MenuButton("START", e -> start()));
		add(new MenuButton("EXIT", e -> System.exit(0)));
		
		add(new GuidePanel(), "south");
		
		Difficulty.EASY.set();
		loadInputValues();
	}
	
	private void loadInputValues() {
		doorCount.setText(Game.DOOR_COUNT + "");;
		moveSpeed.setText(Game.MOVE_SPEED + "");;
		spawnCooldown.setText(Game.SPAWN_COOLDOWN + "");;
		cureSpeed.setText(Game.CURE_SPEED + "");;
		initialInfectionRate.setText(Game.INITIAL_INFECTION_RATE + "");;
		increasePerInfection.setText(Game.INCREASE_PER_INFECTION + "");;
	}

	private void start() {
		String doorCountInput = doorCount.getText();
		String peopleSpeedInput = moveSpeed.getText();
		String spawnCooldownInput = spawnCooldown.getText();
		String cureSpeedInput = cureSpeed.getText();
		String initialInfectionRateInput = initialInfectionRate.getText();
		String increasePerInfectionInput = increasePerInfection.getText();
		
		if (!NumberUtils.isDigits(doorCountInput)) {
			JOptionPane.showMessageDialog(this, doorCountInput + " is not a valid number!", "Invalid Input for Amount of Doors", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!NumberUtils.isDigits(peopleSpeedInput)) {
			JOptionPane.showMessageDialog(this, peopleSpeedInput + " is not a valid number!", "Invalid Input for Speed of People", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!NumberUtils.isDigits(spawnCooldownInput)) {
			JOptionPane.showMessageDialog(this, spawnCooldownInput + " is not a valid number!", "Invalid Input for Spawn Cooldown", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!NumberUtils.isDigits(cureSpeedInput)) {
			JOptionPane.showMessageDialog(this, cureSpeedInput + " is not a valid number!", "Invalid Input for Cure Speed", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!NumberUtils.isDigits(initialInfectionRateInput)) {
			JOptionPane.showMessageDialog(this, initialInfectionRateInput + " is not a valid number!", "Invalid Input for Initial Infection Rate", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!NumberUtils.isDigits(increasePerInfectionInput)) {
			JOptionPane.showMessageDialog(this, increasePerInfectionInput + " is not a valid number!", "Invalid Input for Increase per Infection", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Game.DOOR_COUNT = Integer.parseInt(doorCountInput);
		Game.MOVE_SPEED = Integer.parseInt(peopleSpeedInput);
		Game.SPAWN_COOLDOWN = Integer.parseInt(spawnCooldownInput);
		Game.CURE_SPEED = Integer.parseInt(cureSpeedInput);
		Game.INITIAL_INFECTION_RATE = Integer.parseInt(initialInfectionRateInput);
		Game.INCREASE_PER_INFECTION = Integer.parseInt(increasePerInfectionInput);
		
		Frame.show(Frame.GAME_CONTAINER);
	}
	
}
