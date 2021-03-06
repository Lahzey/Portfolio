package poopgame.gamelogic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import poopgame.gamelogic.components.IdComponent;
import poopgame.gamelogic.components.StatsComponent;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.physics.BodyInfo;
import poopgame.physics.FixtureInfo;
import poopgame.physics.components.BodyComponent;
import poopgame.util.InternalAssetLoader;

public abstract class GameEntity {
	
	protected float width;
	protected float height;
	protected boolean isSensor;

	public GameEntity(float width, float height, boolean isSensor) {
		this.width = width;
		this.height = height;
		this.isSensor = isSensor;
	}

	public Entity create(TimeEngine engine, Vector2 position) {
		BodyDef bodyDef = getBodyDef();
		Body body = engine.world.createBody(bodyDef);
		body.setTransform(position.x, position.y, 0);
		
		FixtureDef fixtureDef = new FixtureDef();
		Shape shape = getShape();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = isSensor;
		Fixture fixture = body.createFixture(fixtureDef);

		Entity entity = engine.createEntity(body, getTexture());
		entity.add(new IdComponent(generateId()));
		entity.add(new StatsComponent());

		BodyInfo bodyInfo = new BodyInfo(body);
		bodyInfo.mainFixture = fixture;
		bodyInfo.entity = entity;
		bodyInfo.origin = this;
		body.setUserData(bodyInfo);
		
		FixtureInfo fixInfo = new FixtureInfo(fixture);
		fixInfo.width = width;
		fixInfo.height = height;
		fixture.setUserData(fixInfo);

		shape.dispose();
		
		return entity;
	}

	protected Shape getShape() {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, height / 2);
		return shape;
	}

	protected BodyDef getBodyDef() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		bodyDef.bullet = isBullet();
		return bodyDef;
	}
	
	protected boolean isBullet() {
		return false;
	}

	protected TextureRegion getTexture() {
		return InternalAssetLoader.getTexture(getTextureName());
	}

	protected abstract String getTextureName();
	
	public abstract String generateId();
	
	// --- Utility functions for accessing an entities information ---
	
	public static Body getBody(Entity entity) {
		return entity.getComponent(BodyComponent.class).body;
	}
	
	public static Stats getStats(Entity entity) {
		return entity.getComponent(StatsComponent.class).stats;
	}
	
	public static String getId(Entity entity) {
		return entity.getComponent(IdComponent.class).id;
	}
	
	public static String generateChildId(Entity entity, String childName) {
		Stats stats = getStats(entity);
		int idIndex = stats.getIdIndex();
		stats.setIdIndex(idIndex + 1);
		return getId(entity) + "/" + childName + "[" + idIndex + "]";
	}

}
