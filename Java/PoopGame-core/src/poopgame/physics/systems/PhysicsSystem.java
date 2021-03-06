package poopgame.physics.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import poopgame.gamelogic.engine.LogicSystem;
import poopgame.graphics.components.TransformComponent;
import poopgame.physics.components.BodyComponent;

public class PhysicsSystem extends EntitySystem implements LogicSystem {

    private static final float STEP_TIME = 1/45f;

    private World world;

    private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
    
    private boolean iteratingRunnableMap = false; //If iterating, add to the queue
    private final Map<Runnable, Integer> executeAfterPhysics = new HashMap<>();
    private final Map<Runnable, Integer> executeAfterPhysicsAddQueue = new HashMap<>();
    private final List<Runnable> runnableQueue = new ArrayList<>();

	private ImmutableArray<Entity> entitiesWithBody;

    private float accumulator = 0f;

    public PhysicsSystem(World world) {
        this.world = world;
    }

	@Override
	public void addedToEngine (Engine engine) {
		entitiesWithBody = engine.getEntitiesFor(Family.all(BodyComponent.class, TransformComponent.class).get());
	}

	@Override
	public void removedFromEngine (Engine engine) {
		entitiesWithBody = null;
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
    	countDownRunnables();
        executeRunnables();
    	
        world.step(STEP_TIME, 6, 2);

        //Entity Queue
        for (Entity entity : entitiesWithBody) {
            TransformComponent tfm = transformMapper.get(entity);
            BodyComponent bodyComp = bodyMapper.get(entity);
            Vector2 position = bodyComp.body.getPosition();
            tfm.position.x = position.x;
            tfm.position.y = position.y;
            tfm.rotation = bodyComp.body.getAngle() * MathUtils.radiansToDegrees;
        }
    }
    
    private void countDownRunnables(){
		executeAfterPhysics.putAll(executeAfterPhysicsAddQueue);
		executeAfterPhysicsAddQueue.clear();
        
    	iteratingRunnableMap = true;
        for(Runnable runnable : executeAfterPhysics.keySet()){
        	int physicsCount = executeAfterPhysics.get(runnable);
        	if(physicsCount <= 0) runnableQueue.add(runnable);
        	else executeAfterPhysics.put(runnable, physicsCount - 1);
        }
    	iteratingRunnableMap = false;
    }
    
    private void executeRunnables(){
        for(Runnable runnable : runnableQueue){
        	executeAfterPhysics.remove(runnable);
        	runnable.run();
        }
        runnableQueue.clear();
    }
    
    /**
     * Executes the given Runnable outside the physics calculation.
     * <br/>It is recommended to call this if creating any bodies to make sure these bodies aren't created during a physics calculation (which would lead to a crash).
     * @param toExecute the Runnable to execute
     */
    public void executeAfterPhysics(Runnable toExecute){
    	executeAfterPhysics(toExecute, 0);
    }
    
    /**
     * Executes the given Runnable after the given amount of physics calculations.
     * @param toExecute the runnable to execute
     * @param executeAfterPhysicsCount how many physics calculations should take place before the given Runnable is executed.
     */
    public void executeAfterPhysics(Runnable toExecute, int executeAfterPhysicsCount){
    	if(iteratingRunnableMap) executeAfterPhysicsAddQueue.put(toExecute, executeAfterPhysicsCount);
    	else executeAfterPhysics.put(toExecute, executeAfterPhysicsCount);
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
