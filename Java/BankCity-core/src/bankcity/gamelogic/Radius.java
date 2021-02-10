package bankcity.gamelogic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

import bankcity.gamelogic.buildings.BuildingEffect;
import bankcity.gamelogic.buildings.components.BuildingComponent;
import bankcity.physics.FixtureInfo;
import bankcity.physics.FixtureInfo.CollisionListener;

public class Radius extends GameObject{
	
	protected float radius;
	protected BuildingEffect effect;
	public Color color = new Color(123f / 255, 209f / 255, 252f / 255, 1f);

	public Radius(Game game, Body radiusFor, float radius, BuildingEffect effect) {
		super(game);
		this.body = radiusFor;
		this.radius = radius;
		this.effect = effect;
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("radius.png");
	}

	@Override
	protected float getWidth() {
		return radius * 2;
	}

	@Override
	protected float getHeight() {
		return radius * 2;
	}
	
	@Override
	protected Shape getShape(){
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		return shape;
	}
	
	@Override
	protected Body getBody(){
		if(fixture == null){
			Shape shape = getShape();
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.isSensor = true;
			fixtureDef.filter.categoryBits = getCategoryBits();
			fixtureDef.filter.maskBits = getMaskBits();
			fixture = body.createFixture(fixtureDef);
			shape.dispose();

			FixtureInfo fixtureInfo = new FixtureInfo(fixture);
			fixtureInfo.entity = entity;
			fixture.setUserData(fixtureInfo);
			
		}
		return body;
	}
	
	public void build(){
		build(body.getWorldCenter());
	}
	
	@Override
	protected void build(Vector2 position){
		super.build(position);
		
		textureComponent.size.set(getWidth(), getHeight());
		textureComponent.tint.set(color);
		color = textureComponent.tint;
		if(!preview){
			textureComponent.isHidden = true;
			getFixtureInfo().collisionListeners.add(new CollisionListener() {
				
				@Override
				public void endCollision(FixtureInfo collidingWith) {
					if(collidingWith.entity == null) return;
					BuildingComponent buildingComp = collidingWith.entity.getComponent(BuildingComponent.class);
					if(buildingComp != null){
						effect.tryRemove(buildingComp.building);
					}
				}
				
				@Override
				public void beginCollision(FixtureInfo collidingWith) {
					if(collidingWith.entity == null) return;
					BuildingComponent buildingComp = collidingWith.entity.getComponent(BuildingComponent.class);
					if(buildingComp != null){
						effect.tryApply(buildingComp.building);
					}
				}
			});
		}
	}
	
	//public because it is only called in destroy method of Building which is already executed safely
	public void destroy(boolean bodyDestroyed){
		body = null;
		if(!bodyDestroyed){
			body.destroyFixture(fixture);
		}
		super.destroy();
	}

	@Override
	protected short getCategoryBits() {
		return CollisionBits.RADIUSES.bits;
	}

	@Override
	protected short getMaskBits() {
		return CollisionBits.BUILDINGS.bits;
	}

}
