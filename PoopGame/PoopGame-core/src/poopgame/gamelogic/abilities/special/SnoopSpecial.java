package poopgame.gamelogic.abilities.special;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.audio.Sound;

import poopgame.gamelogic.AtomicPoop;
import poopgame.gamelogic.Champion;
import poopgame.gamelogic.GameEntity;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.util.InternalAssetLoader;

public class SnoopSpecial extends SpecialAbility {

	private static final Sound SOUND = InternalAssetLoader.getSound(Champion.KIM.getFolderName() + "special/nuke.mp3");
	private static final String ATOMIC_POOP_ID = "AtomicPoopId";

	@Override
	public void cast(TimeEngine engine, Entity player) {
		String poopId = GameEntity.getStats(player).getStat(ATOMIC_POOP_ID, String.class);
		Entity poop = engine.getEntityById(poopId);
		if (poop == null) {
			poop = new AtomicPoop(player).create(engine, GameEntity.getBody(player).getPosition());
			GameEntity.getStats(player).setEnergy(100);
			SOUND.play();

		} else {
			AtomicPoop.detonate(engine, poop);
		}
	}

}