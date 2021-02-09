package poopgame.gamelogic;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import poopgame.gamelogic.components.PlayerComponent;
import poopgame.gamelogic.engine.TimeEngine;
import poopgame.gamelogic.engine.actions.Action;
import poopgame.gamelogic.engine.actions.ActionType;
import poopgame.gamelogic.systems.MovementSystem;
import poopgame.gamelogic.systems.UpdateSystem;
import poopgame.graphics.systems.AnimationSystem;
import poopgame.graphics.systems.CameraSystem;
import poopgame.graphics.systems.RenderingSystem;
import poopgame.physics.FixtureInfo;
import poopgame.physics.systems.PhysicsDebugSystem;
import poopgame.physics.systems.PhysicsSystem;
import poopgame.server.ActionRequest;
import poopgame.server.GameServer;
import poopgame.server.GameServer.ActionReceiver;
import poopgame.server.LocalServer;
import poopgame.tiledmap.TiledMapCollision;
import poopgame.tiledmap.TiledMapRenderable;
import poopgame.ui.InputAdapter;
import poopgame.ui.SwingFrame;
import poopgame.ui.WinScreen;
import poopgame.util.SystemPriorities;

public class PoopGame extends InputAdapter implements ContactListener, ActionReceiver, Disposable {

	private static PoopGame INSTANCE;

	private static final float TILE_SIZE = 1;
	private static final boolean DEBUG = false;

	public static final InputMap INPUT_MAP = new InputMap();

	public World world;
	public TimeEngine engine;

	private GameServer server;
	private Long activePlayerId;

	// Systems
	public AnimationSystem animationSystem;
	public RenderingSystem renderingSystem;
	public CameraSystem cameraSystem;
	public PhysicsSystem physicsSystem;
	public PhysicsDebugSystem physicsDebugSystem;

	// Tiled Map
	public TiledMap tiledMap;
	public TiledMapRenderable tiledMapRenderable;
	public TiledMapCollision tiledMapCollision;

	// Scheduled operations
	private final List<Runnable> executeAfterNextUpdate = new ArrayList<>();
	private final List<Runnable> executionQueue = new ArrayList<>();

	/**
	 * If a Fixture is in this list, other fixtures won't detect collision with it, but it will still detect collision with others.
	 */
	public final List<Fixture> sensors = new ArrayList<>();

	private boolean initialized = false;

	public static PoopGame getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PoopGame();
		}
		return INSTANCE;
	}

	private PoopGame() {
		// Create Box2D World
		world = new World(new Vector2(0, -25), false);
		world.setContactListener(this);

		// Create Ashley Engine
		engine = new TimeEngine(world);
		animationSystem = new AnimationSystem();
		engine.addSystem(animationSystem);
		renderingSystem = new RenderingSystem();
		engine.addSystem(renderingSystem);
		cameraSystem = new CameraSystem(renderingSystem);
		engine.addSystem(cameraSystem);
		physicsSystem = new PhysicsSystem(world);
		engine.addSystem(physicsSystem);
		if (DEBUG) {
			physicsDebugSystem = new PhysicsDebugSystem(world, renderingSystem.getCamera());
			engine.addSystem(physicsDebugSystem);
		}
		engine.addSystem(new UpdateSystem());
		engine.addSystem(new MovementSystem());

		SystemPriorities.set(engine);
	}

	public void setServer(GameServer server) {
		this.server = server;
	}

	public void setActivePlayerId(Long playerId) {
		this.activePlayerId = playerId;

		if (playerId != null) {
			SwingFrame.addInputProcessor(this);
		} else {
			SwingFrame.removeInputProcessor(this);
		}
	}

	public List<Vector2> getSpawnLocations() {
		return tiledMapCollision.getTypeLocations("spawn");
	}

	public void executeAfterNextUpdate(Runnable runnable) {
		executeAfterNextUpdate.add(runnable);
	}

	public void step(float deltaTime) {
		if (server == null) {
			return;
		} else if (!initialized) {
			initialize();
		} else {
			synchronized (executeAfterNextUpdate) {
				executionQueue.addAll(executeAfterNextUpdate);
				executeAfterNextUpdate.clear();
			}
			synchronized (engine) {
				engine.update();
			}
			for (Runnable runnable : executionQueue)
				runnable.run();
			executionQueue.clear();
		}
	}

	private void initialize() {
		if (server == null) {
			return; // cannot initialise yet
		}


		// Create Tiled Map
		tiledMap = new TmxMapLoader().load(server.getArena().getMapPath());
		tiledMapRenderable = new TiledMapRenderable(tiledMap);
		tiledMapRenderable.setTileSize(TILE_SIZE);
		renderingSystem.renderBefore.add(tiledMapRenderable);
		renderingSystem.getCamera().viewportWidth = tiledMapRenderable.getWidth();
		renderingSystem.getCamera().viewportHeight = tiledMapRenderable.getHeight();
		renderingSystem.getCamera().position.set(renderingSystem.getCamera().viewportWidth / 2, renderingSystem.getCamera().viewportHeight / 2, 0);
		tiledMapCollision = new TiledMapCollision(tiledMap, TILE_SIZE);
		tiledMapCollision.create(world);

		Vector2 mapDimensions = tiledMapCollision.getMapDimensions();
		cameraSystem.setBounds(new Rectangle(0, 0, mapDimensions.x, mapDimensions.y));
		
		server.addReceiver(this);

		int i = 0;
		List<Vector2> spawnLocations = getSpawnLocations();
		for (PlayerComponent playerComp : server.getPlayers()) {
			if (i >= spawnLocations.size()) {
				i = 0;
			}
			Player player = new Player(playerComp);
			player.create(engine, spawnLocations.get(i).cpy().add(player.width, player.height));
			i++;
		}

		if (server.getStartTime() > 0) {
			engine.startTime = server.getStartTime();
		}

		server.setEngine(engine);

		initialized = true;
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		FixtureInfo infoA = (FixtureInfo) fixtureA.getUserData();
		FixtureInfo infoB = (FixtureInfo) fixtureB.getUserData();
		if (!sensors.contains(fixtureB)) infoA.colliding.put(infoB.id, fixtureB);
		if (!sensors.contains(fixtureA)) infoB.colliding.put(infoA.id, fixtureA);
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		FixtureInfo infoA = (FixtureInfo) fixtureA.getUserData();
		FixtureInfo infoB = (FixtureInfo) fixtureB.getUserData();
		infoA.colliding.remove(infoB.id);
		infoB.colliding.remove(infoA.id);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

	public void dispose() {
		synchronized (engine) {
			SwingFrame.removeInputProcessor(this);
			server.removeReceiver(this);
			engine.dispose();
			INSTANCE = null;
		}
	}

	@Override
	public void mousePressed(int mouseButton) {
		ActionType actionType = INPUT_MAP.getMouseMapping(mouseButton);
		if (actionType != null) {
			Action action = new Action(actionType, activePlayerId, engine.getTime() + server.estimateDelay());
			server.dispatchAction(new ActionRequest(action));
			synchronized (engine) {
				engine.dispatchAction(action);
			}
		}
	}

	@Override
	public void mouseReleased(int mouseButton) {
		ActionType actionType = INPUT_MAP.getMouseMapping(mouseButton);
		if (actionType == ActionType.MOVE_LEFT_START) {
			actionType = ActionType.MOVE_LEFT_END;
		} else if (actionType == ActionType.MOVE_RIGHT_START) {
			actionType = ActionType.MOVE_RIGHT_END;
		} else if (actionType == ActionType.POOP_START) {
			actionType = ActionType.POOP_END;
		} else {
			return; // this action type has no start-end processing
		}
		Action action = new Action(actionType, activePlayerId, engine.getTime() + server.estimateDelay());
		server.dispatchAction(new ActionRequest(action));
		synchronized (engine) {
			engine.dispatchAction(action);
		}
	}

	@Override
	public void keyPressed(int keyCode) {
		ActionType actionType = INPUT_MAP.getKeyMapping(keyCode);
		if (actionType != null) {
			Action action = new Action(actionType, activePlayerId, engine.getTime() + server.estimateDelay());
			server.dispatchAction(new ActionRequest(action));
			synchronized (engine) {
				engine.dispatchAction(action);
			}
		}
	}

	@Override
	public void keyReleased(int keyCode) {
		ActionType actionType = INPUT_MAP.getKeyMapping(keyCode);
		if (actionType == ActionType.MOVE_LEFT_START) {
			actionType = ActionType.MOVE_LEFT_END;
		} else if (actionType == ActionType.MOVE_RIGHT_START) {
			actionType = ActionType.MOVE_RIGHT_END;
		} else if (actionType == ActionType.POOP_START) {
			actionType = ActionType.POOP_END;
		} else {
			return; // this action type has no start-end processing
		}
		Action action = new Action(actionType, activePlayerId, engine.getTime() + server.estimateDelay());
		server.dispatchAction(new ActionRequest(action));
		synchronized (engine) {
			engine.dispatchAction(action);
		}
	}

	@Override
	public void receive(Action action) {
		if (server instanceof LocalServer && action.getActionTime() < engine.getTime()) {
			action.setActionTime(System.currentTimeMillis());
		}

		synchronized (engine) {
			engine.dispatchAction(action);
		}
	}

	public void goToWinScreen(Entity winner) {
		dispose();
		SwingFrame.goTo(new WinScreen(winner, server, activePlayerId));
	}
}