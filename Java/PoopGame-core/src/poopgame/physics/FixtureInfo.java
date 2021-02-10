package poopgame.physics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.physics.box2d.Fixture;

public class FixtureInfo {
	
	private static int ID_COUNTER = 0;
	
	public final Fixture fixture;
	public final int id = ID_COUNTER++;
	
	public Map<Integer, Fixture> colliding = new HashMap<>();
	public float width;
	public float height;
	
	
	public Object additionalInfo;
	
	
	public FixtureInfo(Fixture fixture){
		this.fixture = fixture;
	}
	
	public static FixtureInfo get(Fixture fixture){
		return (FixtureInfo) fixture.getUserData();
	}
}
