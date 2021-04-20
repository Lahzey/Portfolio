package poopgame.gamelogic;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
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
import poopgame.server.ActionMessage;
import poopgame.server.GameServer;
import poopgame.server.GameServer.ActionReceiver;
import poopgame.server.LocalServer;
import poopgame.tiledmap.TiledMapCollision;
import poopgame.tiledmap.TiledMapRenderable;
import poopgame.ui.InputAdapter;
import poopgame.ui.SwingFrame;
import poopgame.ui.EndScreen;
import poopgame.util.SystemPriorities;

public class PoopGame extends InputAdapter implements ContactListener, ActionReceiver, Disposable {

	private static PoopGame INSTANCE;

	private static final float TILE_SIZE = 1;
	private static final boolean DEBUG = false;

	public static final InputMap INPUT_MAP = new InputMap();

	public World world;
	public TimeEngine engine;

	private GameServer server;
	private PlayerComponent activePlayer;

	public Vector2 mapDimensions = new Vector2();;

	// Scheduled operations
	private final List<Runnable> executeAfterNextUpdate = new ArrayList<>();
	private final List<Runnable> executionQueue = new ArrayList<>();
	private final List<Runnable> executeAsap = new ArrayList<>();

	private boolean initialized = false;

	private TiledMapRenderable tiledMapRenderable;
	private RenderingSystem renderingSystem;

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

		engine = new TimeEngine(world);
		initSystems();
	}

	private void initSystems() {
		renderingSystem = new RenderingSystem();
		engine.addSystem(renderingSystem);
		engine.addSystem(new CameraSystem(renderingSystem));
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new PhysicsSystem(world));
		if (DEBUG) {
			engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
		}
		engine.addSystem(new UpdateSystem());
		engine.addSystem(new MovementSystem());

		SystemPriorities.set(engine);
	}

	public void setServer(GameServer server) {
		this.server = server;
	}

	public void setActivePlayer(PlayerComponent player) {
		this.activePlayer = player;

		if (player != null) {
			SwingFrame.addInputProcessor(this);
		} else {
			SwingFrame.removeInputProcessor(this);
		}
	}

	public void executeAfterNextUpdate(Runnable runnable) {
		synchronized (executeAfterNextUpdate) {
			executeAfterNextUpdate.add(runnable);
		}
	}

	public void executeAsap(Runnable runnable) {
		synchronized (executeAsap) {
			executeAsap.add(runnable);
		}
	}

	private void executeRunnables(List<Runnable> runnables) {
		synchronized (runnables) {
			for (Runnable runnable : runnables) {
				runnable.run();
			}
			runnables.clear();
		}
	}

	public void step(float deltaTime) {
		if (server == null) {
			return;
		}

		if (!initialized) {
			initialize();
		}
		
		if (server.getStartTime() <= 0 || server.getStartTime() > System.currentTimeMillis()) {
			engine.updatePaused();
			return;
		}

		synchronized (executeAfterNextUpdate) {
			executionQueue.addAll(executeAfterNextUpdate);
			executeAfterNextUpdate.clear();
		}
		executeRunnables(executeAsap);
		synchronized (engine) {
			engine.update();
		}
		executeRunnables(executeAsap);
		executeRunnables(executionQueue);
	}

	private void initialize() {
		if (server == null) {
			System.out.println("Cannot init game yet...");
			return; // cannot initialise yet
		}
		System.out.println("Init Game...");

		System.out.println("Resetting engine");
		engine.reset();

		// Create Tiled Map
		System.out.println("Loading Map: " + server.getArena().getMapPath());
		TiledMap tiledMap = new TmxMapLoader().load(server.getArena().getMapPath());
		if (tiledMapRenderable == null) {
			tiledMapRenderable = new TiledMapRenderable(tiledMap);
		} else {
			tiledMapRenderable.setMap(tiledMap);
		}
		tiledMapRenderable.setTileSize(TILE_SIZE);
		renderingSystem.renderBefore.add(tiledMapRenderable);
		renderingSystem.getCamera().viewportWidth = tiledMapRenderable.getWidth();
		renderingSystem.getCamera().viewportHeight = tiledMapRenderable.getHeight();
		renderingSystem.getCamera().position.set(renderingSystem.getCamera().viewportWidth / 2, renderingSystem.getCamera().viewportHeight / 2, 0);
		TiledMapCollision tiledMapCollision = new TiledMapCollision(tiledMap, TILE_SIZE);
		tiledMapCollision.create(world);
		mapDimensions.set(tiledMapCollision.getMapDimensions());

		Vector2 mapDimensions = tiledMapCollision.getMapDimensions();
		engine.getSystem(CameraSystem.class).setBounds(new Rectangle(0, 0, mapDimensions.x, mapDimensions.y));

		server.addReceiver(this);

		System.out.println("Spawning players");
		int i = 0;
		List<Vector2> spawnLocations = tiledMapCollision.getTypeLocations("spawn");
		for (PlayerComponent playerComp : server.getPlayers()) {
			if (i >= spawnLocations.size()) {
				i = 0;
			}
			System.out.println("Spawning player[" + playerComp.id + "] " + playerComp.name + " as " + playerComp.champ + " at " + spawnLocations.get(i));
			Player player = new Player(playerComp);
			player.create(engine, spawnLocations.get(i).cpy().add(player.width, player.height));
			i++;
		}

		if (server.getStartTime() > 0) {
			engine.startTime = server.getStartTime();
		}

		server.setEngine(engine);

		for (EntitySystem system : engine.getSystems()) {
			System.out.println(system.getClass().getName());
		}

		System.out.println("Init complete");
		initialized = true;
	}

	@Override
	public void dispose() {
		synchronized (engine) {
			setActivePlayer(null);

			if (server != null) {
				server.removeReceiver(this);
				server.resetStartTime();
				server = null;
			}

			initialized = false;
			// TODO: Fix Bug where disposing world causes Client to crash
//			engine.dispose();
		}
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		FixtureInfo infoA = (FixtureInfo) fixtureA.getUserData();
		FixtureInfo infoB = (FixtureInfo) fixtureB.getUserData();
		infoA.colliding.put(infoB.id, fixtureB);
		infoB.colliding.put(infoA.id, fixtureA);
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

	@Override
	public void mousePressed(int mouseButton) {
		executeAsap(() -> {
			ActionType actionType = INPUT_MAP.getMouseMapping(mouseButton);
			if (actionType != null && server != null && initialized) {
				Action action = new Action(actionType, activePlayer.id, engine.getTime() + server.estimateDelay());
				server.dispatchAction(new ActionMessage(action));
				synchronized (engine) {
					engine.dispatchAction(action);
//					action.execute(engine, engine.getEntityById(Player.generateIdForPlayer(action.getPlayerId())));
				}
			}
		});
	}

	@Override
	public void mouseReleased(int mouseButton) {
		executeAsap(() -> {
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
			Action action = new Action(actionType, activePlayer.id, engine.getTime() + server.estimateDelay());
			server.dispatchAction(new ActionMessage(action));
			synchronized (engine) {
				engine.dispatchAction(action);
//				action.execute(engine, engine.getEntityById(Player.generateIdForPlayer(action.getPlayerId())));
			}
		});
	}

	@Override
	public void keyPressed(int keyCode) {
		executeAsap(() -> {
			ActionType actionType = INPUT_MAP.getKeyMapping(keyCode);
			if (actionType != null) {
				Action action = new Action(actionType, activePlayer.id, engine.getTime() + server.estimateDelay());
				server.dispatchAction(new ActionMessage(action));
				synchronized (engine) {
					engine.dispatchAction(action);
//					action.execute(engine, engine.getEntityById(Player.generateIdForPlayer(action.getPlayerId())));
				}
			}
		});
	}

	@Override
	public void keyReleased(int keyCode) {
		executeAsap(() -> {
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
			Action action = new Action(actionType, activePlayer.id, engine.getTime() + server.estimateDelay());
			server.dispatchAction(new ActionMessage(action));
			synchronized (engine) {
				engine.dispatchAction(action);
//				action.execute(engine, engine.getEntityById(Player.generateIdForPlayer(action.getPlayerId())));
			}
		});
	}

	@Override
	public void receive(Action action) {
		if (server instanceof LocalServer && action.getActionTime() < engine.getTime()) {
			action.setActionTime(System.currentTimeMillis());
		}

		synchronized (engine) {
			engine.dispatchAction(action);
//			action.execute(engine, engine.getEntityById(Player.generateIdForPlayer(action.getPlayerId())));
		}
	}

	public void goToWinScreen(Entity winner) {
		SwingFrame.goTo(new EndScreen(winner, server, activePlayer));
		dispose();
	}
}
