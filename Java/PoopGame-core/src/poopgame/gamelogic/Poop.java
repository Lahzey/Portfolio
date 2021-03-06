package poopgame.gamelogic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import poopgame.gamelogic.components.IdComponent;
import poopgame.gamelogic.components.PlayerComponent;
import poopgame.gamelogic.components.UpdateComponent;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.physics.BodyInfo;
import poopgame.physics.FixtureInfo;
import poopgame.util.InternalAssetLoader;

public class Poop extends GameEntity {

	public static final float WIDTH = 0.5f;
	public static final float HEIGHT = 0.5f;

	protected static final String DAMAGE_AMP = "damageAmp";
	protected static final String SECONDS_IN_AIR = "secondsInAir";

	private static final Sound SOUND = InternalAssetLoader.getSound("poopsound.mp3");

	protected Entity pooper;

	public Poop(Entity pooper) {
		super(WIDTH, HEIGHT, true);
		this.pooper = pooper;
	}

	@Override
	public String generateId() {
		return generateChildId(pooper, getClass().getSimpleName());
	}

	@Override
	public Entity create(TimeEngine engine, Vector2 position) {
		return create(engine, position, true, SOUND);
	}

	public Entity create(TimeEngine engine, Vector2 position, boolean physics, Sound sound) {
		Entity poop = super.create(engine, position);
		poop.add(new UpdateComponent(Poop.class));
		
		if (physics) getBody(poop).setLinearVelocity(getBody(pooper).getLinearVelocity().cpy().scl(1.5f));

		getStats(poop).setOwnerId(getId(pooper));
		getStats(poop).setDamage(getStats(pooper).getDamage());
		getStats(poop).setStat(DAMAGE_AMP, 5f);
		getStats(poop).setStat(SECONDS_IN_AIR, 0f);

		// Play poop sound
		if (sound != null) sound.play();

		return poop;
	}

	// Called by UpdateComponent
	public static void update(TimeEngine engine, Entity entity, float delta) {
		float secondsInAir = getStats(entity).getStat(SECONDS_IN_AIR, Float.class) + delta;
		getStats(entity).setStat(SECONDS_IN_AIR, secondsInAir);
		
		Body body = getBody(entity);
		FixtureInfo fixInfo = (FixtureInfo) ((BodyInfo) body.getUserData()).mainFixture.getUserData();
		String ownerId = getStats(entity).getOwnerId();
		
		for (Fixture colliding : fixInfo.colliding.values()) {
			Entity collidingEntity = ((BodyInfo) colliding.getBody().getUserData()).entity;
			if (collidingEntity != null && collidingEntity.getComponent(PlayerComponent.class) != null) {
				String id = collidingEntity.getComponent(IdComponent.class).id;
				if (!ownerId.equals(id)) {
					onConfirmedCollision(engine, entity, collidingEntity);
				}
			}
		}

		if (body.getPosition().y < -HEIGHT) {
			engine.removeEntity(entity);
		}

		Vector2 position = body.getPosition();

		// wrap around
		float maxX = PoopGame.getInstance().mapDimensions.x;
		if (position.x < 0) body.setTransform(maxX, position.y, 0);
		else if (position.x > maxX) body.setTransform(0, position.y, 0);
	}

	private static void onConfirmedCollision(TimeEngine engine, Entity poop, Entity collidingEntity) {
		int damage = (int) (getStats(poop).getDamage() * (1 + (getStats(poop).getStat(SECONDS_IN_AIR, Float.class) * getStats(poop).getStat(DAMAGE_AMP, Float.class))));
		getStats(collidingEntity).setHealth(getStats(collidingEntity).getHealth() - damage);
		engine.removeEntity(poop);
	}

	@Override
	protected String getTextureName() {
		return "poop.png";
	}
}
