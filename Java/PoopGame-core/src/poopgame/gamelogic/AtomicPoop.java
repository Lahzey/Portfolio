package poopgame.gamelogic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;

import poopgame.gamelogic.components.PlayerComponent;
import poopgame.gamelogic.components.UpdateComponent;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.graphics.components.TransformComponent;
import poopgame.physics.BodyInfo;
import poopgame.physics.FixtureInfo;

public class AtomicPoop extends Poop {

	public AtomicPoop(Entity pooper) {
		super(pooper);
	}
	
	@Override
	public Entity create(TimeEngine engine, Vector2 position, boolean physics, Sound sound) {
		Entity entity = super.create(engine, position, physics, sound);
		getStats(entity).setDamage(1000000);
		getStats(entity).setStat(DAMAGE_AMP, 0f);
		return entity;
	}

	@Override
	public String getTextureName() {
		return Champion.KIM.getFolderName() + "special/atomicpoop.png";
	}

	protected boolean isCollidingTargetValid(Fixture colliding) {
		return true;
	}
	
	public static void detonate(TimeEngine engine, Entity poop) {
		new AtomicWaste(poop).create(engine, getBody(poop).getPosition());
		engine.removeEntity(poop);
	}

	public static class AtomicWaste extends GameEntity {

		private static final float DURATION = 7f;
		private static final float EXPAND_DURATION = 1f;
		private static final float MIN_DIAMETER = 0.5f;
		private static final float MAX_DIAMETER = 5f;
		private static final float RISE_HEIGHT = 10f;
		private static final int DAMAGE_PER_SEC = 50;
		
		private Entity poop;

		public AtomicWaste(Entity poop) {
			super(MIN_DIAMETER, MIN_DIAMETER, true);
			this.poop = poop;
		}
		
		@Override
		public String generateId() {
			return generateChildId(poop, "AtomicWaste");
		}

		@Override
		public Entity create(TimeEngine engine, Vector2 position) {
			Entity entity = super.create(engine, position);
			entity.getComponent(TransformComponent.class).mustBeInFrame = true;
			entity.add(new UpdateComponent(AtomicWaste.class));

			getStats(entity).setRemainingTime(DURATION);
			return entity;
		}

		// Called by UpdateComponent
		public static void update(TimeEngine engine, Entity entity, float delta) {
			Stats stats = getStats(entity);

			float remainingTime = stats.getRemainingTime() - delta;
			stats.setRemainingTime(remainingTime);
			
			// check collisions
			Body body = getBody(entity);
			FixtureInfo fixInfo = (FixtureInfo) ((BodyInfo) body.getUserData()).mainFixture.getUserData();
			for (Fixture colliding : fixInfo.colliding.values()) {
				Entity collidingEntity = ((BodyInfo) colliding.getBody().getUserData()).entity;
				if (collidingEntity.getComponent(PlayerComponent.class) != null) {
					getStats(collidingEntity).setHealth(getStats(collidingEntity).getHealth() - (DAMAGE_PER_SEC * delta));
				}
			}

			if (remainingTime >= DURATION - EXPAND_DURATION) {
				// expand atomic waste
				CircleShape shape = (CircleShape) fixInfo.fixture.getShape();
				float progress = (DURATION - remainingTime) / EXPAND_DURATION;
				float diameter = (float) (MIN_DIAMETER + (MAX_DIAMETER - MIN_DIAMETER) * progress);
				shape.setRadius(diameter / 2);
				fixInfo.width = diameter;
				fixInfo.height = diameter;
			} else if (remainingTime > 0) {
				// push atomic waste up
				Vector2 position = body.getPosition();
				body.setTransform(position.x, position.y + RISE_HEIGHT * delta / DURATION, 0);
			} else {
				// expired, can remove now
				engine.removeEntity(entity);
			}
		}

		@Override
		protected Shape getShape() {
			CircleShape shape = new CircleShape();
			shape.setRadius(MIN_DIAMETER / 2);
			return shape;
		}

		@Override
		protected String getTextureName() {
			return Champion.KIM.getFolderName() + "special/atomicwaste.png";
		}

		@Override
		protected BodyDef getBodyDef() {
			BodyDef bodyDef = super.getBodyDef();
			bodyDef.type = BodyType.StaticBody;
			return bodyDef;
		}
	}

}
