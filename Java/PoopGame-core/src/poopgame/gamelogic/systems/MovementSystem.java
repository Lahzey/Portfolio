package poopgame.gamelogic.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import poopgame.gamelogic.components.MovementComponent;
import poopgame.gamelogic.components.StatsComponent;
import poopgame.gamelogic.engine.LogicSystem;
import poopgame.physics.components.BodyComponent;

public class MovementSystem extends EntitySystem implements LogicSystem {

    private static final float STEP_TIME = 1/45f;

	private ImmutableArray<Entity> updateableEntites;

    private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<MovementComponent> movementMapper = ComponentMapper.getFor(MovementComponent.class);
    private ComponentMapper<StatsComponent> statsMapper = ComponentMapper.getFor(StatsComponent.class);

    private float accumulator = 0f;

	@Override
	public void addedToEngine (Engine engine) {
		updateableEntites = engine.getEntitiesFor(Family.all(BodyComponent.class, MovementComponent.class, StatsComponent.class).get());
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
    		Body body = bodyMapper.get(entity).body;
    		MovementComponent movementComp = movementMapper.get(entity);
    		StatsComponent statsComp = statsMapper.get(entity);
    		
    		// get movement direction
    		int direction = 0;
    		if (movementComp.moveRight) direction += 1;
    		if (movementComp.moveLeft) direction -= 1;
    		
    		// apply movement
    		float desiredXVelocity = statsComp.stats.getSpeed() * direction;
    		float currentXVelocity = body.getLinearVelocity().x;
    		float diff = desiredXVelocity - currentXVelocity;
    		if(Math.abs(diff) > 0.01) body.applyLinearImpulse(new Vector2(body.getMass() * diff, 0), body.getWorldCenter(), true);
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
