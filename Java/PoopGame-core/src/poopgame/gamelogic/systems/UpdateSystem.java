package poopgame.gamelogic.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import poopgame.gamelogic.components.UpdateComponent;
import poopgame.gamelogic.engine.LogicSystem;
import poopgame.gamelogic.engine.TimeEngine;

public class UpdateSystem extends EntitySystem implements LogicSystem {

    private static final float STEP_TIME = 1/45f;

	private ImmutableArray<Entity> updateableEntites;

    private float accumulator = 0f;

	@Override
	public void addedToEngine (Engine engine) {
		updateableEntites = engine.getEntitiesFor(Family.all(UpdateComponent.class).get());
	}

	@Override
	public void removedFromEngine (Engine engine) {
		updateableEntites = null;
	}

    @Override
    public void update(float deltaTime) {
        accumulator += deltaTime;
        while (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
        	step();
        }
    }
    
    private void step() {
    	for (Entity entity : updateableEntites) {
    		entity.getComponent(UpdateComponent.class).update((TimeEngine) getEngine(), entity, STEP_TIME);
    	}
    }

	@Override
	public Float storeState() {
		return accumulator;
	}

	@Override
	public void loadState(Object state) {
		accumulator = (Float) state;
	}

}
