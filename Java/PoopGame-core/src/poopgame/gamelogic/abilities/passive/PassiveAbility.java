package poopgame.gamelogic.abilities.passive;

import com.badlogic.ashley.core.Entity;

import poopgame.gamelogic.engine.TimeEngine;

public abstract class PassiveAbility {
	
	public abstract void update(TimeEngine engine, Entity player, float deltaTime);
	
	public void onShoot(TimeEngine engine, Entity player, Entity poop) { }

}
