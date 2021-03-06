package poopgame.gamelogic.abilities.special;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import poopgame.gamelogic.GameEntity;
import poopgame.gamelogic.abilities.passive.NinjaPassive;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.physics.components.BodyComponent;

public class NinjaSpecial extends SpecialAbility {

	@Override
	public void cast(TimeEngine engine, Entity player) {
		Entity lastPoop = engine.getEntityById(GameEntity.getStats(player).getStat(NinjaPassive.LAST_POOP_STAT_NAME, String.class));
		if (lastPoop != null) {
			Body playerBody = GameEntity.getBody(player);
			BodyComponent poopBodyComp = lastPoop.getComponent(BodyComponent.class);
			Body poopBody = poopBodyComp != null ? poopBodyComp.body : null;
			if (playerBody != null && poopBody != null) {
				Vector2 playerPosition = playerBody.getTransform().getPosition();
				float playerX = playerPosition.x;
				float playerY = playerPosition.y;
				float playerAngle = playerBody.getAngle();

				Vector2 poopPosition = poopBody.getTransform().getPosition();
				float pooprX = poopPosition.x;
				float poopY = poopPosition.y;
				float poopAngle = poopBody.getAngle();
				
				playerBody.setTransform(pooprX, poopY, poopAngle);
				poopBody.setTransform(playerX, playerY, playerAngle);
			}
		} else {
			GameEntity.getStats(player).setEnergy(100f);
		}
	}

}