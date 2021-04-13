package poopgame.util;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import poopgame.gamelogic.systems.MovementSystem;
import poopgame.gamelogic.systems.UpdateSystem;
import poopgame.graphics.systems.AnimationSystem;
import poopgame.graphics.systems.CameraSystem;
import poopgame.graphics.systems.RenderingSystem;
import poopgame.physics.systems.PhysicsSystem;

public class SystemPriorities {
	
	public static void set(Engine engine){
		List<Class<? extends EntitySystem>> priorityList = new ArrayList<>();

		priorityList.add(PhysicsSystem.class);
		priorityList.add(UpdateSystem.class);
		priorityList.add(CameraSystem.class);
		priorityList.add(AnimationSystem.class);
		priorityList.add(RenderingSystem.class);
		
		// when starting movement it will begin at next loop, not previous
		priorityList.add(MovementSystem.class);
		
		for(EntitySystem system : engine.getSystems()){
			if(priorityList.contains(system.getClass())) {
				system.priority = priorityList.indexOf(system.getClass());
			}
		}
	}
	
}
