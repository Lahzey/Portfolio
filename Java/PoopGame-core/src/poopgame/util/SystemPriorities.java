package poopgame.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import poopgame.gamelogic.systems.MovementSystem;
import poopgame.gamelogic.systems.UpdateSystem;
import poopgame.graphics.systems.AnimationSystem;
import poopgame.graphics.systems.CameraSystem;
import poopgame.graphics.systems.RenderingSystem;
import poopgame.physics.systems.PhysicsSystem;

public class SystemPriorities {
	
	public static final Map<Class<? extends EntitySystem>, Integer> MAP = getMapping();
	
	private static Map<Class<? extends EntitySystem>, Integer> getMapping(){
		Map<Class<? extends EntitySystem>, Integer> map = new HashMap<>();

		map.put(CameraSystem.class, 1);
		map.put(AnimationSystem.class, 2);
		map.put(RenderingSystem.class, 3);
		map.put(MovementSystem.class, 4);
		map.put(PhysicsSystem.class, 5);
		map.put(UpdateSystem.class, 6);
		
		return map;
	}
	
	public static void set(Engine engine){
		for(EntitySystem system : engine.getSystems()){
			if(MAP.containsKey(system.getClass())) system.priority = MAP.get(system.getClass());
		}
	}
	
}
