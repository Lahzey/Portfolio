package poopgame.gamelogic.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

import poopgame.gamelogic.components.IdComponent;
import poopgame.gamelogic.components.LogicComponent;

public class State {

	public TimeEngine timeEngine;

	public int nextActionIndex;

	public List<String> entityIds = new ArrayList<>();
	public Map<String, List<Class<? extends Component>>> entityComponentTypes = new HashMap<>();
	public Map<String, Map<Class<? extends Component>, Object>> entityStates = new HashMap<>();
	public Map<Class<? extends EntitySystem>, Object> systemStates = new HashMap<>();
	
	public State() {}

	public State(TimeEngine timeEngine) {
		this.timeEngine = timeEngine;
		nextActionIndex = timeEngine.nextActionIndex;

		// get entity states
		for (Entity entity : timeEngine.getEntities()) {
			IdComponent idComp = entity.getComponent(IdComponent.class);
			if (idComp != null) {
				entityIds.add(idComp.id);

				List<Class<? extends Component>> componentTypes = new ArrayList<>();
				entityComponentTypes.put(idComp.id, componentTypes);

				Map<Class<? extends Component>, Object> entityState = null;
				for (Component component : entity.getComponents()) {
					componentTypes.add(component.getClass());

					if (component instanceof LogicComponent) {
						Object state = ((LogicComponent) component).storeState(timeEngine, timeEngine.getWorld());
						if (state != null) {
							if (entityState == null) {
								entityState = new HashMap<>();
								entityStates.put(idComp.id, entityState);
							}
							entityState.put(component.getClass(), state);
						}
					}
				}
			}
		}

		// get system states
		for (EntitySystem system : timeEngine.getSystems()) {
			if (system instanceof LogicSystem) {
				systemStates.put(system.getClass(), ((LogicSystem) system).storeState());
			}
		}
	}

	public void restore() {
		Map<String, Entity> existingEntities = new HashMap<>();
		for (Entity entity : timeEngine.getEntities()) {
			IdComponent idComp = entity.getComponent(IdComponent.class);
			if (idComp != null) {
				// check if entity should be kept
				if (!entityIds.contains(idComp.id)) {
					timeEngine.removeEntity(entity);
				} else {
					existingEntities.put(idComp.id, entity);

					// check which components should be kept
					List<Class<? extends Component>> componentTypes = entityComponentTypes.get(idComp.id);
					for (Component component : entity.getComponents()) {
						Class<? extends Component> componentType = component.getClass();
						if (!componentTypes.contains(componentType)) {
							entity.remove(componentType);
						}
					}
				}
			}
		}

		for (String entityId : entityIds) {
			Entity entity = existingEntities.get(entityId);
			if (entity == null) {
				entity = timeEngine.createEntity();
				entity.add(new IdComponent(entityId));
			}

			Map<Class<? extends Component>, Object> entityStateObjects = entityStates.get(entityId);
			for (Class<? extends Component> componentType : entityComponentTypes.get(entityId)) {
				Component component = entity.getComponent(componentType);
				if (component == null) {
					try {
						component = componentType.getConstructor().newInstance();
						entity.add(component);
					} catch (Throwable e) {
						throw new RuntimeException("Failed to access empty constructor of " + componentType.getName(), e);
					}
				}

				Object stateObject = entityStateObjects.get(componentType);
				if (stateObject != null) {
					((LogicComponent) component).loadState(stateObject, timeEngine, timeEngine.getWorld());
				}
			}
		}
		
		for (Class<? extends EntitySystem> systemType : systemStates.keySet()) {
			EntitySystem system = timeEngine.getSystem(systemType);
			if (system instanceof LogicSystem) {
				((LogicSystem) system).loadState(systemStates.get(systemType));
			}
		}

		timeEngine.nextActionIndex = nextActionIndex;
	}

}
