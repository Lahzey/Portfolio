package poopgame.gamelogic;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import poopgame.gamelogic.components.IdComponent;
import poopgame.gamelogic.components.PlayerComponent;
import poopgame.gamelogic.components.UpdateComponent;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.graphics.components.TransformComponent;
import poopgame.physics.components.BodyComponent;
import poopgame.util.InternalAssetLoader;

public class Bird extends GameEntity {

	private static final float WIDTH = 0.5f;
	private static final float HEIGHT = 0.5f;

	private static final float SPEED = 2.5f; // in meters per second
	private static final float POOPING_SPEED = 0.75f; // in seconds between
	private static final float DURATION = 10f;

	private static final String POOP_COOLDOWN = "poopCooldown";

	public Entity owner;

	public Bird(Entity owner) {
		super(WIDTH, HEIGHT, true);
		this.owner = owner;
	}

	@Override
	public String generateId() {
		return generateChildId(owner, "Bird");
	}

	@Override
	public Entity create(TimeEngine engine, Vector2 position) {
		Entity entity = super.create(engine, position);
		entity.getComponent(TransformComponent.class).mustBeInFrame = true;
		entity.add(new UpdateComponent(Bird.class));

		getStats(entity).setOwnerId(getId(owner));
		getStats(entity).setRemainingTime(DURATION);
		getStats(entity).setStat(POOP_COOLDOWN, 0f);
		return entity;
	}

	// Called by UpdateComponent
	public static void update(TimeEngine engine, Entity entity, float delta) {
		Stats stats = getStats(entity);

		float remainingTime = stats.getRemainingTime() - delta;
		if (engine.getEntityById(stats.getOwnerId()) == null) {
			remainingTime = 0;
		}

		stats.setRemainingTime(remainingTime);

		// check if expired
		if (remainingTime <= 0) {
			PoopGame.getInstance().executeAfterNextUpdate(new Runnable() {

				@Override
				public void run() {
					engine.removeEntity(entity);
				}
			});
			return;
		}

		// movement
		Body body = getBody(entity);
		float maxY = getFlightHeight();
		boolean rising = maxY - body.getPosition().y > 0.01f;
		if (rising) {
			body.setTransform(body.getPosition().x, body.getPosition().y + SPEED * 3 * delta, 0);
		} else {
			Body target = findTarget(engine, entity, stats.getOwnerId());
			if (target != null) {
				float travelDist = 0;
				float xDist = target.getPosition().x - body.getPosition().x;
				if (xDist > 0) {
					travelDist = SPEED * delta;
					if (travelDist > xDist) travelDist = xDist;
				} else if (xDist < 0) {
					travelDist = -SPEED * delta;
					if (travelDist < xDist) travelDist = xDist;
				}
				body.setTransform(body.getPosition().x + travelDist, body.getPosition().y, 0);
			}
		}

		// pooping
		float poopCooldown = stats.getStat(POOP_COOLDOWN, Float.class) - delta;
		if (!rising && poopCooldown <= 0) {
			poopCooldown = POOPING_SPEED;
			BirdPoop poop = new BirdPoop(engine.getEntityById(stats.getOwnerId()));
			poop.create(engine, body.getPosition(), false, InternalAssetLoader.getSound(Champion.TRUMP.getFolderName() + "special/birdpoop.mp3"));
		}
		stats.setStat(POOP_COOLDOWN, poopCooldown);
	}

	private static Body findTarget(TimeEngine engine, Entity bird, String ownerId) {
		List<Body> targets = new ArrayList<>();
		for (Entity entity : engine.getEntitiesFor(Family.all(PlayerComponent.class).get())) {
			IdComponent idComp = entity.getComponent(IdComponent.class);
			BodyComponent bodyComp = entity.getComponent(BodyComponent.class);
			if (!ownerId.equals(idComp.id)) {
				targets.add(bodyComp.body);
			}
		}

		Body birdBody = getBody(bird);
		Body closest = null;
		float closestDist = 0;
		for (Body target : targets) {
			float dist = Math.abs(birdBody.getPosition().y - target.getPosition().y);
			if (closest == null || dist < closestDist) {
				closest = target;
				closestDist = dist;
			}
		}

		return closest;
	}

	private static float getFlightHeight() {
		return PoopGame.getInstance().mapDimensions.y - HEIGHT * 2;
	}

	@Override
	protected String getTextureName() {
		return Champion.TRUMP.getFolderName() + "special/bird.png";
	}

	@Override
	protected BodyDef getBodyDef() {
		BodyDef bodyDef = super.getBodyDef();
		bodyDef.type = BodyType.KinematicBody;
		return bodyDef;
	}

	private static class BirdPoop extends Poop {

		public BirdPoop(Entity owner) {
			super(owner);
			width = 0.1f;
			height = 0.3f;
		}
		
		@Override
		public Entity create(TimeEngine engine, Vector2 position, boolean physics, Sound sound) {
			Entity entity = super.create(engine, position, physics, sound);
			getStats(entity).setStat(DAMAGE_AMP, 0f);
			return entity;
		}

		@Override
		public String getTextureName() {
			return Champion.TRUMP.getFolderName() + "special/birdpoop.png";
		}

	}
}
