package bankcity.gamelogic.buildings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import bankcity.data.FileLocations;
import bankcity.gamelogic.Achievement;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.GameObject;
import bankcity.gamelogic.Radius;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.Stat.StatList;
import bankcity.gamelogic.buildings.components.BuildingComponent;
import bankcity.gamelogic.buildings.components.interfaces.InspectableComponent;
import bankcity.gamelogic.buildings.components.interfaces.StatComponent;
import bankcity.gamelogic.components.UpdateableComponent;
import bankcity.gamelogic.components.UpdateableComponent.Updateable;
import bankcity.gamelogic.functionality.Functionality;
import bankcity.gamelogic.functionality.ResourceFunctionality;
import bankcity.graphics.components.TextureComponent;
import bankcity.graphics.systems.RenderingSystem.BuildingClassOverlay;
import bankcity.graphics.systems.RenderingSystem.Overlay;
import bankcity.ui.Inspectable;
import bankcity.ui.Inspector;
import bankcity.ui.stages.InGameStage;
import bankcity.util.Calculation;

public abstract class Building extends GameObject implements Updateable, Inspectable{

	protected List<Radius> radiuses;
	protected List<Functionality> functionalities = new ArrayList<>();
	protected ResourceFunctionality resources;
	public final StatList stats = new StatList();

	public Building(Game game, float price, float income, float upkeep, float energyConsumption, float waterConsumption) {
		super(game);
		
		resources = new ResourceFunctionality(-energyConsumption, -waterConsumption);
		functionalities.add(resources);

		stats.put(Stat.PRICE, new Calculation(price));
		stats.put(Stat.UPKEEP, new Calculation(upkeep));
		stats.put(Stat.INCOME, new Calculation(income));
	}
	
	public static <T extends Building> T createInstance(Class<T> type, Game game){
		try {
			Constructor<T> constructor = type.getConstructor(Game.class);
			return constructor.newInstance(game);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException("Class " + type.getName() + " does not have a valid constructor (with one parameter of Game) and therefore no instance can be created.");
		}
	}
	
	@Override
	public TextureRegion getTextureByPath(String buildingPath){
		return super.getTextureByPath(FileLocations.BUILDINGS + "/" + buildingPath);
	}
	
	
	
	//information
	
	public abstract String getName();
	
	public abstract BuildingSector getSector();

	@Override
	protected short getCategoryBits() {
		return CollisionBits.BUILDINGS.bits;
	}

	@Override
	protected short getMaskBits() {
		return CollisionBits.ALL.bits;
	}

	public boolean isEnabled(){
		return isVisible();
	}

	public boolean isVisible(){
		return game.getAchievementLevel(Achievement.GETTING_STARTED) > 0;
	}
	
	public float getUpdateInterval(){
		return 1;
	}
	
	public float getPrice(){
		return stats.get(Stat.PRICE).getResult();
	}
	
	public Overlay getOverlay(){
		return new BuildingClassOverlay(this.getClass());
	}
	
	
	
	
	//build & destroy
	
	@Override
	protected void build(Vector2 position){
		super.build(position);
		radiuses = createRadiuses();
		if(radiuses != null){
			for(Radius radius : radiuses){
				radius.setPreview(preview);
				radius.build();
			}
		}
		if(!preview){
			poplateEntity(game, entity);
		}
	}
	
	@Override
	protected void destroy(){
		super.destroy();
		if(radiuses != null){
			for(Radius radius : radiuses){
				radius.destroy(true);
			}
		}
	}

	public void poplateEntity(Game game, Entity entity) {
		BuildingComponent buildingComponent = game.engine.createComponent(BuildingComponent.class);
		buildingComponent.building = this;
		entity.add(buildingComponent);
		
		UpdateableComponent updateableComponent = game.engine.createComponent(UpdateableComponent.class);
		updateableComponent.updateable = this;
		updateableComponent.interval = getUpdateInterval();
		entity.add(updateableComponent);
		
		for(Functionality functionality : functionalities){
			functionality.addToBuilding(game.engine, this);
		}
	}
	
	public List<Radius> createRadiuses(){
		return null;
	}
	
	public Building copy(){
		Building copy = createInstance(getClass(), game);
		copy.preview = preview;
		return copy;
	}
	
	
	
	
	
	//Stats
	
	@Override
	public void update(float deltaTime){
		UpdateableComponent updateComp = entity.getComponent(UpdateableComponent.class);
		if(updateComp != null) updateComp.interval = getUpdateInterval();
		for(Component component : entity.getComponents()){
			if(component instanceof Updateable){
				((Updateable) component).update(deltaTime);
			}
			if(component instanceof StatComponent){
				((StatComponent) component).update(stats, deltaTime);
			}
		}
		
		//affect efficiency with crimerate
		float crimerate = stats.get(Stat.CRIMERATE).getResult();
		if(crimerate > 1) crimerate = 1;
		else if(crimerate < 0) crimerate = 0;
		stats.get(Stat.EFFICIENCY).putMult(Stat.CRIMERATE, 1- crimerate);
	}
	
	
	//Inspection

	@Override
	public void createInspectionUI(Inspector inspector) {
		Table table = inspector.getInspectionTable();
		
		Image image = new Image(new TextureRegionDrawable(entity.getComponent(TextureComponent.class).region){
			public TextureRegion getRegion(){
				TextureRegion region = entity.getComponent(TextureComponent.class).region;
				if(region != super.getRegion()){
					setRegion(region);
				}
				return super.getRegion();
			}

			public void draw (Batch batch, float x, float y, float width, float height) {
				batch.draw(getRegion(), x, y, width, height);
			}

			public void draw (Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX,
				float scaleY, float rotation) {
				batch.draw(getRegion(), x, y, originX, originY, width, height, scaleX, scaleY, rotation);
			}
		});
		image.setScaling(Scaling.fit);
		table.add(image).pad(InGameStage.MEDIUM_PADDING).growX().center();
		
		for(Component component : getEntity().getComponents()){
			if(component instanceof InspectableComponent){
				InspectableComponent inspectableComponent = (InspectableComponent) component;
				inspector.hr();
				table.add(inspectableComponent.getInspectionUI(this)).growX().fillY();
			}
		}
	}

	@Override
	public String getTitle() {
		return getName();
	}

	@Override
	public Color getColor() {
		return getSector().color;
	}
}
