package poopgame.gamelogic.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.World;

import poopgame.gamelogic.engine.TimeEngine;

public class UpdateComponent extends LogicComponent {
	
	private Class<?> updateableClass;
	
	public UpdateComponent() {}
	
	public UpdateComponent(Class<?> updateableClass) {
		this.updateableClass = updateableClass;
	}

	@Override
	public Object storeState(Engine engine, World world) {
		return updateableClass.getName();
	}

	@Override
	public void loadState(Object state, Engine engine, World world) {
		try {
			updateableClass = Class.forName((String) state);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e); // should never happen
		}
	}
	
	public void update(TimeEngine engine, Entity entity, float deltaTime) {
		try {
			updateableClass.getMethod("update", TimeEngine.class, Entity.class, float.class).invoke(null, engine, entity, deltaTime);
		} catch (Throwable e) {
			throw new RuntimeException("Failed to invoke public static method update(TimeEngine, Entity, float) for " + updateableClass.getName() + ".", e);
		}
	}

}
