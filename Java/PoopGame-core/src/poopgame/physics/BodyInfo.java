package poopgame.physics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class BodyInfo {
	
	private static int ID_COUNTER = 0;
	
	public final Body body;
	public final int id = ID_COUNTER++;

	public Entity entity;
	public Object origin;
	public Fixture mainFixture;
	public Map<String, Fixture> otherFixtures = new HashMap<>();
	
	
	public Object additionalInfo;
	

	public BodyInfo(Body body){
		this.body = body;
	}
	
	public static BodyInfo get(Body body){
		return (BodyInfo) body.getUserData();
	}
	
}
