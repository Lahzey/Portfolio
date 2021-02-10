package bankcity.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class UpdateableComponent implements Component, Poolable{

	public Updateable updateable;
	public float interval;
	public float storedDelta;
	
	
	@Override
	public void reset() {
		updateable = null;
		interval = 0;
		storedDelta = 0;
	}

	public static interface Updateable{
		public void update(float deltaTime);
	}
	
}
