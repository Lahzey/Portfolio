package poopgame.gamelogic.abilities.special;

import com.badlogic.ashley.core.Entity;

import poopgame.gamelogic.engine.TimeEngine;

public abstract class SpecialAbility {

	public abstract void cast(TimeEngine engine, Entity player);
	
	public void onShoot(TimeEngine engine, Entity player, Entity poop) { }

}
