package logic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;

import bankcity.graphics.components.TextureComponent;
import bankcity.graphics.components.TransformComponent;
import bankcity.physics.BodyInfo;
import bankcity.physics.FixtureInfo;
import bankcity.physics.components.BodyComponent;
import bankcity.util.ColorDrawable;
import main.GameInstance;

public abstract class GameObject {
	
	protected boolean preview = false;
	
	protected final GameInstance game;
	
	protected boolean built = false;
	protected Entity entity;
	protected TransformComponent transformComponent;
	protected BodyComponent bodyComponent;
	protected TextureComponent textureComponent;
	
	
	protected Body body;
	protected Fixture fixture;
	protected BodyDef bodyDef;

	public GameObject(GameInstance game){
		this.game = game;
	}
	
	public void safeBuild(Vector2 position){
		safeBuild(position, null);
	}
	
	public void safeBuild(Vector2 position, Runnable callback){
		game.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				build(position);
				if(callback != null) callback.run();
			}
		});
	}
	
	protected void build(Vector2 position){
		if(!built){
			built = true;
			entity = game.engine.createEntity();
			
			transformComponent = game.engine.createComponent(TransformComponent.class);
			if(position != null) transformComponent.position.set(position);
			transformComponent.z = getZ();
			entity.add(transformComponent);
			
			bodyComponent = game.engine.createComponent(BodyComponent.class);
			bodyComponent.body = getBody();
			entity.add(bodyComponent);
			
			textureComponent = new TextureComponent();
			textureComponent.region = getTexture();
			textureComponent.size.set(getWidth(), getHeight());
			entity.add(textureComponent);
			
			game.engine.addEntity(entity);
		}
	}
	
	public void safeDestroy(){
		safeDestroy(null);
	}
	
	public void safeDestroy(Runnable callback){
		game.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				destroy();
				if(callback != null) callback.run();
			}
		});
	}
	
	protected void destroy(){
		if(built){
			built = false;
			if(entity != null){
				game.engine.removeEntity(entity);
				entity = null;
			}
			if(body != null){
				game.world.destroyBody(body);
				body = null;
			}
			fixture = null;
			transformComponent = null;
			bodyComponent = null;
			textureComponent = null;
		}
	}
	
	protected Body getBody(){
		if(body == null){
			BodyDef bodyDef = getBodyDef();
			Vector2 position = getPosition();
			if(position != null) bodyDef.position.set(position);
			body = game.world.createBody(bodyDef);
			
			Shape shape = getShape();
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.isSensor = true;
			fixtureDef.filter.categoryBits = getCategoryBits();
			fixtureDef.filter.maskBits = getMaskBits();
			fixture = body.createFixture(fixtureDef);
			shape.dispose();
			
			//Create User Data
			
			BodyInfo bodyInfo = new BodyInfo(body);
			bodyInfo.mainFixture = fixture;
			body.setUserData(bodyInfo);

			FixtureInfo fixtureInfo = new FixtureInfo(fixture);
			fixtureInfo.entity = entity;
			fixture.setUserData(fixtureInfo);
		}
		return body;
	}
	
	protected BodyDef getBodyDef(){
		if(bodyDef == null){
			bodyDef = new BodyDef();
			bodyDef.type = BodyType.DynamicBody;
			if(transformComponent != null && transformComponent.position != null){
				bodyDef.position.set(transformComponent.position.x, transformComponent.position.y);
			}
			
		}
		return bodyDef;
	}
	
	protected Shape getShape(){
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(getWidth() / 2, getHeight() / 2);
		return shape;
	}
	
	protected TextureRegion getTextureByPath(String internalPath){
		return new TextureRegion(new Texture(internalPath));
	}
	public abstract TextureRegion getTexture();
	protected abstract float getWidth();
	protected abstract float getHeight();
	protected float getZ(){
		return 0;
	}
	protected abstract short getCategoryBits();
	protected abstract short getMaskBits();
	
	public Vector2 getPosition(){
		if(transformComponent != null) return transformComponent.position;
		else return null;
	}
	
	public void setPosition(Vector2 position){
		if(transformComponent != null) transformComponent.position.set(position);
		if(body != null) body.setTransform(position, body.getTransform().getRotation());
	}
	
	public float getRotation(){
		if(transformComponent != null) return transformComponent.rotation;
		else return 0;
	}
	
	public void setRotation(float rotation){
		if(transformComponent != null) transformComponent.rotation = rotation;
		if(body != null) body.setTransform(body.getWorldCenter(), rotation);
	}
	
	public void setPreview(boolean preview){
		if(preview != this.preview){
			this.preview = preview;
			if(built){
				Vector2 position = getPosition();
				float rotation = getRotation();
				destroy();
				build(position);
				setRotation(rotation);
			}
		}
	}
	
	public boolean isPreview(){
		return preview;
	}
	
	public Entity getEntity(){
		return entity;
	}
	
	public FixtureInfo getFixtureInfo(){
		if(fixture != null) return (FixtureInfo) fixture.getUserData();
		else return null;
	}
	
	public boolean isBuilt(){
		return built;
	}
	
	public Game getGame(){
		return game;
	}
	
	public Tooltip<Table> getTextureTooltip(){
		Table table = new Table();
		Image image = new Image(getTexture());
		table.add(image).grow().maxWidth(100).maxHeight(100);
		table.setBackground(new ColorDrawable(Color.LIGHT_GRAY, Color.DARK_GRAY, 2));
		table.pad(3);
		return new Tooltip<Table>(table);
	}
	
	
	public static enum CollisionBits{
		
		NONE(0b0000000),
		BUILDINGS(0b0000001),
		RADIUSES(0b0000010),
		OTHER(0b0000100),
		ALL(0b1111111);
		
		public final short bits;
		
		private CollisionBits(int bits){
			this.bits = (short) bits;
		}
	}
}
