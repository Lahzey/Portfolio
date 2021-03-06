package poopgame.gamelogic.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import poopgame.gamelogic.Player;
import poopgame.gamelogic.components.IdComponent;
import poopgame.gamelogic.components.LogicComponent;
import poopgame.gamelogic.engine.actions.Action;
import poopgame.graphics.components.TextureComponent;
import poopgame.graphics.components.TransformComponent;
import poopgame.physics.components.BodyComponent;
import poopgame.server.StateUpdateMessage;

public class TimeEngine extends Engine implements Disposable {

	public final World world;

	private StateStore stateStore = new StateStore(1000);
	private State initialState;

	private final List<Action> actions = new ArrayList<>(); // all the actions that have ever happened or will happen, ordered by time
	private final Map<Long, Action> actionIdMapping = new HashMap<>(); // used for quickly finding the previous version of an action
	protected int nextActionIndex = 0;
	
	public final Object[] actionLock = new Object[0];
	public final Object[] updateLock = new Object[0];

	public long startTime = System.currentTimeMillis(); // only used for debugging, current millisecond time is a huge number and harder to compare
	private long currentTime = startTime;

	private ImmutableArray<Entity> entitiesWithId;

	public TimeEngine(World world) {
		this.world = world;

		initialState = new State(this);

		entitiesWithId = getEntitiesFor(Family.all(IdComponent.class).get());
	}
	
	public StateUpdateMessage createStateUpdate() {
		State mostRecent = stateStore.getState(currentTime);
		return mostRecent != null ? new StateUpdateMessage(mostRecent, stateStore.getStateTime(mostRecent)) : null;
	}
	
	public void applyStateUpdate(StateUpdateMessage stateUpdate) {
		State state = stateUpdate.createState();
		state.timeEngine = this;
		
		stateStore.deleteAfter(stateUpdate.time - 1);
		stateStore.store(state, stateUpdate.time);
		refresh(stateUpdate.time);
	}
	
	public void updatePaused() {
		currentTime = System.currentTimeMillis();
		super.update(1f);
	}
	
	public void update() {
		update(System.currentTimeMillis() - currentTime);
	}
	
	@Override
	public void update(float deltaSeconds) {
		throw new RuntimeException("TimeEngine manages time deltas itself. Call update() to progress it.");
	}

	public void update(long deltaTime) {
		updateInternal(deltaTime);
	}

	private void updateInternal(long deltaTime) {
		if (nextActionIndex < actions.size()) {
			Action nextAction = actions.get(nextActionIndex);
			long nextActionTime = nextAction.getActionTime();
			long nextActionDeltaTime = nextActionTime - currentTime;

			if (nextActionDeltaTime < deltaTime) {
				long leftOver = deltaTime - nextActionDeltaTime;
				deltaTime = nextActionDeltaTime;
				progressTime(deltaTime);
				updateInternal(leftOver);
			} else {
				progressTime(deltaTime);
			}
		} else {
			progressTime(deltaTime);
		}
	}

	private void progressTime(long deltaTime) {
		synchronized (updateLock) {
			currentTime += deltaTime;
			executeDueActions();

			super.update(deltaTime / 1000f); // convert back to seconds

			State currentState = new State(this);
			stateStore.store(currentState, currentTime);
		}
	}

	private void executeDueActions() {
		while (nextActionIndex < actions.size()) {
			Action nextAction = actions.get(nextActionIndex);
			long nextActionTime = nextAction.getActionTime();

			if (nextActionTime <= currentTime) {
				Entity player = getEntityById(Player.generateIdForPlayer(nextAction.getPlayerId()));
				if (player != null) {
					nextAction.execute(this, player);
				}
				nextActionIndex++;
			} else {
				// all due actions have been executed, following are still in the future
				break;
			}
		}
	}

	public void dispatchAction(Action action) {
		synchronized (actionLock) {
			long actionTime = action.getActionTime();
			long refreshTime = actionTime + 1; // small buffer to ensure local actions do not cause a refresh

			long actionId = action.getId();
			if (actionIdMapping.containsKey(actionId)) {
				// this action has already been listed (and possibly executed), replacing with new version from server
				Action existingAction = actionIdMapping.get(actionId);
				long existingActionTime = existingAction.getActionTime();
				if (actionTime != existingActionTime) {
					actions.remove(existingAction);

					if (existingActionTime < refreshTime) {
						refreshTime = existingActionTime - 1;
					}
				} else {
					// seems like this action has already been executed at the exact same time, no need for any adjustments
					return;
				}
			} else {
				actionIdMapping.put(actionId, action);
			}

			// add action at the correct index
			if (actions.isEmpty()) {
				actions.add(action);
			} else {
				for (int i = actions.size() - 1; i >= 0; i--) {
					Action listedAction = actions.get(i);
					if (listedAction.getActionTime() <= actionTime) {
						actions.add(i + 1, action);
						break;
					}
				}
			}

			if (refreshTime < currentTime) {
				refresh(refreshTime);
			}
		}
	}

	/**
	 * Rolls back the engine to the given time, then progresses it forward to the current time, executing all actions in between.
	 * @param time the time to roll back to
	 */
	public void refresh(long time) {
		synchronized (updateLock) {
			setVisualsEnabled(false);
			
			State state = stateStore.getState(time);
			long stateTime;
			if (state == null) {
				state = initialState;
				stateTime = 0;
			} else {
				stateTime = stateStore.getStateTime(state);
			}
			stateStore.deleteAfter(stateTime);

			long timeDif = currentTime - stateTime;

			state.restore();
			world.step(0, 6, 2);
			currentTime = stateTime;
			
			update(timeDif);
			setVisualsEnabled(true);
		}
	}

	public Entity createEntity(Body body, TextureRegion texture) {
		Entity entity = createEntity();
		BodyComponent bodyComp = createComponent(BodyComponent.class);
		bodyComp.body = body;
		entity.add(bodyComp);
		TransformComponent transformComp = createComponent(TransformComponent.class);
		entity.add(transformComp);
		TextureComponent textureComp = createComponent(TextureComponent.class);
		textureComp.region = texture;
		entity.add(textureComp);
		addEntity(entity);
		return entity;
	}

	@Override
	public void removeEntity(Entity entity) {
		for (Component component : entity.getComponents()) {
			if (component instanceof LogicComponent) {
				((LogicComponent) component).onDestroy(this, world);
			}
		}
		super.removeEntity(entity);
	}

	public World getWorld() {
		return world;
	}

	public long getTime() {
		return currentTime;
	}
	
	public Entity getEntityById(String id) {
		if (id == null) {
			return null;
		}
		
		// manual loop to prevent concurrent modification
		for (int i = 0; i < entitiesWithId.size(); i++) {
			Entity entity = entitiesWithId.get(i);
			IdComponent idComp = entity.getComponent(IdComponent.class);
			if (idComp != null && id.equals(idComp.id)) {
				return entity;
			}
		}
		return null;
	}
	
	private void setVisualsEnabled(boolean enabled) {
		for (EntitySystem system : getSystems()) {
			if (system instanceof VisualSystem) {
				((VisualSystem) system).setEnabled(enabled);
			}
		}
	}

	public void dispose() {
		synchronized (updateLock) {
			world.dispose();
		}
	}

	public void reset() {
		synchronized (updateLock) {
			Array<Body> array = new Array<Body>(world.getBodyCount());
			world.getBodies(array);
			for (Body body : array) {
				world.destroyBody(body);
			}
			stateStore = new StateStore(1000);
			actions.clear();
			actionIdMapping.clear();
			nextActionIndex = 0;
			setVisualsEnabled(true);
			currentTime = System.currentTimeMillis();
		}
	}

}
