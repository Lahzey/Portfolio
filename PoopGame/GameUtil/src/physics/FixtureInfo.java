package physics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;

public class FixtureInfo {
	
	private static int ID_COUNTER = 0;
	
	public final Fixture fixture;
	public Entity entity;
	public final int id = ID_COUNTER++;
	
	public final List<FixtureInfo> colliding = new ArrayList<>();
	public final List<CollisionListener> collisionListeners = new ArrayList<>();
	
	
	public FixtureInfo(Fixture fixture){
		this.fixture = fixture;
	}
	
	public static FixtureInfo get(Fixture fixture){
		return (FixtureInfo) fixture.getUserData();
	}
	
	public void beginCollision(FixtureInfo collidingWith){
		colliding.add(collidingWith);
		for(CollisionListener listener : collisionListeners) listener.beginCollision(collidingWith);
	}
	
	public void endCollision(FixtureInfo collidingWith){
		colliding.remove(collidingWith);
		for(CollisionListener listener : collisionListeners) listener.endCollision(collidingWith);
	}
	
	public static interface CollisionListener{
		public void beginCollision(FixtureInfo collidingWith);
		public void endCollision(FixtureInfo collidingWith);
	}
}
