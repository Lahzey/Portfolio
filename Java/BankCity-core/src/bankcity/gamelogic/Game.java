package bankcity.gamelogic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import util.Language;
import util.StringFormatter;
import util.StringFormatter.DefaultNumberAbbreviations;

import bankcity.gamelogic.buildings.BuildingInitializer;
import bankcity.gamelogic.systems.EconomySystem;
import bankcity.gamelogic.systems.EducationSystem;
import bankcity.gamelogic.systems.HabitantSystem;
import bankcity.gamelogic.systems.InputSystem;
import bankcity.gamelogic.systems.JobSystem;
import bankcity.gamelogic.systems.ResourceSystem;
import bankcity.gamelogic.systems.TimeSystem;
import bankcity.gamelogic.systems.UpdateSystem;
import bankcity.gamelogic.systems.ResourceSystem.Resource;
import bankcity.graphics.systems.AnimationSystem;
import bankcity.graphics.systems.RenderingSystem;
import bankcity.physics.FixtureInfo;
import bankcity.physics.systems.PhysicsDebugSystem;
import bankcity.physics.systems.PhysicsSystem;
import bankcity.tiledmap.TiledMapCollision;
import bankcity.tiledmap.TiledMapRenderable;
import bankcity.ui.Inspectable;
import bankcity.ui.Inspector;
import bankcity.util.DynamicLabel;
import bankcity.util.DynamicLabel.DynamicString;
import bankcity.util.GameDate;
import bankcity.util.SystemPriorities;

public class Game implements ContactListener, Inspectable {
	
	private static final float TILE_SIZE = 1;

	public World world;
	public PooledEngine engine;
	
	public static boolean debug = false;
	
	public double money = Double.POSITIVE_INFINITY;
	public DefaultNumberAbbreviations abbreviations = new DefaultNumberAbbreviations(Language.DE);
	
	private final Map<Achievement, Integer> achievements = new LinkedHashMap<>();

	//Ashley Systems
	public AnimationSystem animationSystem;
	public RenderingSystem renderingSystem;
	public PhysicsSystem physicsSystem;
	public PhysicsDebugSystem physicsDebugSystem;
	public TimeSystem timeSystem;
	public EconomySystem economySystem;
	public JobSystem jobSystem;
	public HabitantSystem habitantSystem;
	public EducationSystem educationSystem;
	public InputSystem inputSystem;
	public UpdateSystem updateSystem;
	public ResourceSystem resourceSystem;

	//Tiled Map
	public TiledMap tiledMap;
	public TiledMapRenderable tiledMapRenderable;
	
	//Scheduled operations
	private final List<Runnable> executeAfterNextUpdate = new ArrayList<>();
	private final List<Runnable> executionQueue = new ArrayList<>();


	
	public Game(SpriteBatch batch){
		//Create Box2D World
		world = new World(new Vector2(), false);
		world.setContactListener(this);
		
		//Create Ashley Engine
		engine = new PooledEngine();
		animationSystem = new AnimationSystem();
		engine.addSystem(animationSystem);
		renderingSystem = new RenderingSystem(batch);
		engine.addSystem(renderingSystem);
		physicsSystem = new PhysicsSystem(world);
		engine.addSystem(physicsSystem);
		if(debug){
			physicsDebugSystem = new PhysicsDebugSystem(world, renderingSystem.getCamera());
			engine.addSystem(physicsDebugSystem);
		}
		timeSystem = new TimeSystem(new GameDate(01, 01, 2000));
		engine.addSystem(timeSystem);
		economySystem = new EconomySystem(this);
		engine.addSystem(economySystem);
		jobSystem = new JobSystem();
		engine.addSystem(jobSystem);
		habitantSystem = new HabitantSystem(this);
		engine.addSystem(habitantSystem);
		educationSystem = new EducationSystem(this);
		engine.addSystem(educationSystem);
		inputSystem = new InputSystem(this);
		engine.addSystem(inputSystem);
		updateSystem = new UpdateSystem();
		engine.addSystem(updateSystem);
		resourceSystem = new ResourceSystem(this);
		engine.addSystem(resourceSystem);
		
		SystemPriorities.set(engine);
		
		//Create Tiled Map
		tiledMap = new TmxMapLoader().load("maps/island/island.tmx");
		tiledMapRenderable = new TiledMapRenderable(tiledMap);
		tiledMapRenderable.setTileSize(TILE_SIZE);
		renderingSystem.renderBefore.add(tiledMapRenderable);
		renderingSystem.setMaxSize(new Vector2(tiledMapRenderable.getWidth(), tiledMapRenderable.getHeight()));
		TiledMapCollision tiledMapCollision = new TiledMapCollision(tiledMap, TILE_SIZE);
		tiledMapCollision.create(world);
		
		BuildingInitializer.createAll(this);
	}
	
	public String getMoneyString(){
		return formatMoney(money);
	}
	
	public String formatMoney(double money){
		return StringFormatter.formatNumber(money, 0.01, abbreviations, 1);
	}
	
	public void executeAfterNextUpdate(Runnable runnable){
		executeAfterNextUpdate.add(runnable);
	}
	
	public void step(float deltaTime){
		synchronized (executeAfterNextUpdate) {
			executionQueue.addAll(executeAfterNextUpdate);
			executeAfterNextUpdate.clear();
		}
		engine.update(deltaTime);
		for(Runnable runnable : executionQueue) runnable.run();
		executionQueue.clear();
	}
	
	public void setAchievementLevel(Achievement achievement, int level){
		if(level < 0) level = 0;
		else if(level > achievement.maxLevel) level = achievement.maxLevel;
		achievements.put(achievement, level);
	}
	
	public int getAchievementLevel(Achievement achievement){
		if(achievements.containsKey(achievement)) return achievements.get(achievement);
		else return 0;
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		FixtureInfo infoA = (FixtureInfo) fixtureA.getUserData();
		FixtureInfo infoB = (FixtureInfo) fixtureB.getUserData();
		infoA.beginCollision(infoB);
		infoB.beginCollision(infoA);
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		FixtureInfo infoA = (FixtureInfo) fixtureA.getUserData();
		FixtureInfo infoB = (FixtureInfo) fixtureB.getUserData();
		infoA.endCollision(infoB);
		infoB.endCollision(infoA);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

	public void dispose() {
		//TODO dispose
	}

	@Override
	public void createInspectionUI(Inspector inspector) {
		Table table = inspector.getInspectionTable();
		
		table.add("Finances", "title").colspan(2);
		table.row();
		
		table.add("Capital: ").left();
		table.add(new DynamicLabel(new DynamicString(){
			public String toString(){
				return getMoneyString();
			}
		})).left();
		table.row();
		
		table.add("Income / year: ").left();
		table.add(new DynamicLabel(new DynamicString(){
			public String toString(){
				return formatMoney(economySystem.getTotalYield());
			}
		})).left();
		
		
		inspector.hr();

		
		table.add("Social", "title").colspan(2);
		table.row();
		table.add("Habitants: ").left();
		table.add(new DynamicLabel(new DynamicString(){
			public String toString(){
				return habitantSystem.habitants.size() + "";
			}
		})).left();
		table.row();
		table.add("Attractivity: ").left();
		table.add(new DynamicLabel(new DynamicString(){
			public String toString(){
				return StringFormatter.formatNumber(resourceSystem.resources.get(Resource.ATTRACTIVITY), 0.01f);
			}
		})).left();
		
		
		inspector.hr();

		
		table.add("Resources", "title").colspan(2);
		table.row();
		
		table.add("Food: ").left();
		table.add(new DynamicLabel(new DynamicString(){
			public String toString(){
				return StringFormatter.formatNumber(resourceSystem.foodImportExportYield, 1f);
			}
		})).left();
		table.row();
		table.add("Material: ").left();
		table.add(new DynamicLabel(new DynamicString(){
			public String toString(){
				return StringFormatter.formatNumber(resourceSystem.materialImportExportYield, 1f);
			}
		})).left();
		table.row();
		
		table.add("Water: ").left();
		table.add(new DynamicLabel(new DynamicString(){
			public String toString(){
				if(resourceSystem.unsatisfied.get(Resource.WATER).isEmpty()){
					return "overproduction (" + StringFormatter.formatNumber(resourceSystem.resources.get(Resource.WATER), 1f) + " l/h)";
				}else{
					return "not enough";
				}
			}
		})).left();
		table.row();
		table.add().left();
		table.add(new DynamicLabel(new DynamicString(){
			public String toString(){
				float totalWaterConsumption = resourceSystem.totalConsumption.get(Resource.WATER);
				float totalWaterCleaning = resourceSystem.totalConsumption.get(Resource.DIRTY_WATER);
				return (totalWaterCleaning / totalWaterConsumption) * 100 + "% cleaned";
			}
		})).left();
		table.row();
		
		table.add("Electricity: ").left();
		table.add(new DynamicLabel(new DynamicString(){
			public String toString(){
				if(resourceSystem.unsatisfied.get(Resource.ELECTRICITY).isEmpty()){
					return "overproduction (" + StringFormatter.formatNumber(resourceSystem.resources.get(Resource.ELECTRICITY), 0.1f) + " MW/h)";
				}else{
					return "not enough";
				}
			}
		})).left();
	}

	@Override
	public String getTitle() {
		return "General Game Stats";
	}

	@Override
	public Color getColor() {
		return Color.WHITE;
	}
}
