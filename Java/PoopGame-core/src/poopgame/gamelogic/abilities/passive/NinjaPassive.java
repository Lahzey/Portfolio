package poopgame.gamelogic.abilities.passive;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import poopgame.gamelogic.GameEntity;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.physics.BodyInfo;
import poopgame.physics.FixtureInfo;

public class NinjaPassive extends PassiveAbility {
	
	public static final String LAST_POOP_STAT_NAME = "lastPoop";

	@Override
	public void update(TimeEngine engine, Entity player, float deltaTime) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onShoot(TimeEngine engine, Entity player, Entity poop) {
		// disable damage for last poop
		Entity lastPoop = engine.getEntityById(GameEntity.getStats(player).getStat(NinjaPassive.LAST_POOP_STAT_NAME, String.class));
		if (lastPoop != null) {
			GameEntity.getStats(lastPoop).setDamage(0f);
		}
		 // set poop id to stats
		GameEntity.getStats(player).setStat(LAST_POOP_STAT_NAME, GameEntity.getId(poop));
		
		// decrease poop size
		Body body = GameEntity.getBody(poop);
		FixtureInfo fixInfo = (FixtureInfo) ((BodyInfo) body.getUserData()).mainFixture.getUserData();
		fixInfo.width *= 0.5f;
		fixInfo.height *= 0.5f;
		PolygonShape shape = (PolygonShape) fixInfo.fixture.getShape();
		shape.setAsBox(fixInfo.width / 2, fixInfo.height / 2);
		
	}

}