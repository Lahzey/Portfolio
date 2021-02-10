package poopgame.gamelogic.abilities.special;

import com.badlogic.ashley.core.Entity;

import poopgame.gamelogic.Bird;
import poopgame.gamelogic.Champion;
import poopgame.gamelogic.GameEntity;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.util.InternalAssetLoader;

public class TrumpSpecial extends SpecialAbility {

	@Override
	public void cast(TimeEngine engine, Entity player) {
		Bird bird = new Bird(player);
		bird.create(engine, GameEntity.getBody(player).getPosition());
		InternalAssetLoader.getSound(Champion.TRUMP.getFolderName() + "special/birdsound.mp3").play();
	}

}