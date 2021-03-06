package poopgame.server;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.EntitySystem;

import poopgame.gamelogic.engine.State;

public class StateUpdateMessage {
	
	public long time;

	public int nextActionIndex;
	
	public List<String> entityIds;
	public Map<String, List<String>> entityComponentTypes;
	public Map<String, Map<String, Object>> entityStates;
	public Map<String, Object> systemStates;
	
	public StateUpdateMessage() {}
	
	public StateUpdateMessage(State state, long time) {
		this.time = time;
		this.nextActionIndex = state.nextActionIndex;
		this.entityIds = state.entityIds;
		
		entityComponentTypes = new HashMap<>();
		for (String entityId : state.entityComponentTypes.keySet()) {
			List<String> classNames = new ArrayList<>();
			for (Class<? extends Component> componentType : state.entityComponentTypes.get(entityId)) {
				classNames.add(componentType.getName());
			}
			entityComponentTypes.put(entityId, classNames);
		}
		
		entityStates = new HashMap<>();
		for (String entityId : state.entityStates.keySet()) {
			Map<Class<? extends Component>, Object> componentStates = state.entityStates.get(entityId);
			Map<String, Object> states = new HashMap<>();
			for (Class<? extends Component> componentType : componentStates.keySet()) {
				states.put(componentType.getName(), componentStates.get(componentType));
			}
			entityStates.put(entityId, states);
		}
		
		systemStates = new HashMap<>();
		for (Class<? extends EntitySystem> systemType : state.systemStates.keySet()) {
			systemStates.put(systemType.getName(), state.systemStates.get(systemType));
		}
	}
	
	@SuppressWarnings("unchecked")
	public State createState() {
		try {
			State state = new State();
			state.nextActionIndex = nextActionIndex;
			state.entityIds = entityIds;
			
			state.entityComponentTypes = new HashMap<>();
			for (String entityId : entityComponentTypes.keySet()) {
				List<Class<? extends Component>> componentTypes = new ArrayList<>();
				for (String className : entityComponentTypes.get(entityId)) {
					componentTypes.add((Class<? extends Component>) Class.forName(className));
				}
				state.entityComponentTypes.put(entityId, componentTypes);
			}
			
			state.entityStates = new HashMap<>();
			for (String entityId : entityStates.keySet()) {
				Map<String, Object> componentStates = entityStates.get(entityId);
				Map<Class<? extends Component>, Object> states = new HashMap<>();
				for (String className : componentStates.keySet()) {
					states.put((Class<? extends Component>) Class.forName(className), componentStates.get(className));
				}
				state.entityStates.put(entityId, states);
			}
			
			state.systemStates = new HashMap<>();
			for (String className : systemStates.keySet()) {
				state.systemStates.put((Class<? extends EntitySystem>) Class.forName(className), systemStates.get(className));
			}
			
			return state;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to deserialize state update.", e);
		}
	}
	
}
