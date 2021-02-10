package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.Radius;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingEffect;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.components.BuildingComponent;
import bankcity.gamelogic.buildings.types.FireCenter;
import bankcity.gamelogic.buildings.types.FireStation;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.graphics.systems.RenderingSystem.BuildingOverlay;
import bankcity.graphics.systems.RenderingSystem.Overlay;

public class Fire implements BuildingCategory{

	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new FireStation(game));
		buildings.add(new FireCenter(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "fire.png";
	}

	@Override
	public String getName() {
		return "Fire Protection";
	}

	@Override
	public String getDescription() {
		return "Protects your buildings from burning down.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.SERVICES;
	}
	
	public static abstract class SimpleFireBuilding extends Building{
		
		protected float radius;

		public SimpleFireBuilding(Game game, float price, float upkeep, int workerCount, float radius, float energyConsumption, float waterConsumption) {
			super(game, price, 0, upkeep, energyConsumption, waterConsumption);
			this.radius = radius;
			functionalities.add(new JobFunctionality(EducationLevel.UNEDUCATED, workerCount));
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.SERVICES;
		}
		
		@Override
		public List<Radius> createRadiuses(){
			List<Radius> radiuses = new ArrayList<>();
			radiuses.add(new Radius(game, body, radius, new BuildingEffect(SimpleFireBuilding.this, Family.all(BuildingComponent.class).get()) {
				
				@Override
				protected void remove(Building building) {
					building.stats.get(Stat.BURNDOWNRATE).removeMult(this);
				}
				
				@Override
				protected void apply(Building building) {
					building.stats.get(Stat.BURNDOWNRATE).putMult(this, 0f);
				}
			}));
			radiuses.add(new Radius(game, body, radius * 1.5f, new BuildingEffect(SimpleFireBuilding.this, Family.all(BuildingComponent.class).get()) {
				
				@Override
				protected void remove(Building building) {
					building.stats.get(Stat.BURNDOWNRATE).removeMult(this);
				}
				
				@Override
				protected void apply(Building building) {
					building.stats.get(Stat.BURNDOWNRATE).putMult(this, 0.5f);
				}
			}));
			return radiuses;
		}
		
		@Override
		public Overlay getOverlay(){
			return new BuildingOverlay(){

				@Override
				public boolean isTarget(Building building) {
					return true;
				}
				
				@Override
				public void getColor(Building building, Color colorInstance){
					if(building instanceof SimpleFireBuilding){
						super.getColor(building, colorInstance);
					}else{
						float burndownrate = building.stats.get(Stat.BURNDOWNRATE).getResult();
						if(burndownrate > 1) burndownrate = 1;
						else if(burndownrate < 0) burndownrate = 0;
						colorInstance.r = burndownrate;
						colorInstance.g = 1 - burndownrate;
						colorInstance.b = 0;
					}
				}
				
			};
		}
		
		
		
	}
}
