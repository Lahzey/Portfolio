package bankcity.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import bankcity.gamelogic.systems.EconomySystem;
import bankcity.gamelogic.systems.EducationSystem;
import bankcity.gamelogic.systems.HabitantSystem;
import bankcity.gamelogic.systems.InputSystem;
import bankcity.gamelogic.systems.JobSystem;
import bankcity.gamelogic.systems.TimeSystem;
import bankcity.graphics.systems.AnimationSystem;
import bankcity.graphics.systems.RenderingSystem;
import bankcity.physics.systems.PhysicsSystem;

public class SystemPriorities {
	
	public static final Map<Class<? extends EntitySystem>, Integer> MAP = getMapping();
	
	private static Map<Class<? extends EntitySystem>, Integer> getMapping(){
		Map<Class<? extends EntitySystem>, Integer> map = new HashMap<>();
		
		map.put(AnimationSystem.class, 1);
		map.put(RenderingSystem.class, 2);
		map.put(PhysicsSystem.class, 3);
		map.put(TimeSystem.class, 4);
		map.put(JobSystem.class, 5);
		map.put(EconomySystem.class, 6);
		map.put(HabitantSystem.class, 7);
		map.put(EducationSystem.class, 8);
		map.put(InputSystem.class, 9);
		
		return map;
	}
	
	public static void set(Engine engine){
		for(EntitySystem system : engine.getSystems()){
			if(MAP.containsKey(system.getClass())) system.priority = MAP.get(system.getClass());
		}
	}
	
}
