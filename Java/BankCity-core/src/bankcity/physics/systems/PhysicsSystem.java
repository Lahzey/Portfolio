package bankcity.physics.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import bankcity.graphics.components.TransformComponent;
import bankcity.physics.components.BodyComponent;

/**
 * Created by barry on 12/8/15 @ 10:11 PM.
 */
public class PhysicsSystem extends IteratingSystem {

    private static final float MAX_STEP_TIME = 1/45f;
    private static float accumulator = 0f;

    private World world;
    private Array<Entity> bodiesQueue;

    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    
    private boolean iteratingRunnableMap = false; //If iterating, add to the queue
    private final Map<Runnable, Integer> executeAfterPhysics = new HashMap<>();
    private final Map<Runnable, Integer> executeAfterPhysicsAddQueue = new HashMap<>();
    private final List<Runnable> runnableQueue = new ArrayList<>();

    public PhysicsSystem(World world) {
        super(Family.all(BodyComponent.class, TransformComponent.class).get());

        this.world = world;
        this.bodiesQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        if(accumulator >= MAX_STEP_TIME) {
            
        	countDownRunnables();
            executeRunnables();

            world.step(MAX_STEP_TIME, 6, 2);
            accumulator -= MAX_STEP_TIME;

            //Entity Queue
            for (Entity entity : bodiesQueue) {
                TransformComponent tfm = tm.get(entity);
                BodyComponent bodyComp = bm.get(entity);
                Vector2 position = bodyComp.body.getPosition();
                tfm.position.x = position.x;
                tfm.position.y = position.y;
                tfm.rotation = bodyComp.body.getAngle() * MathUtils.radiansToDegrees;
            }
            
        }


        bodiesQueue.clear();

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
    protected void processEntity(Entity entity, float deltaTime) {
        bodiesQueue.add(entity);
    }
}
