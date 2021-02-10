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
import bankcity.gamelogic.buildings.types.PoliceCenter;
import bankcity.gamelogic.buildings.types.PoliceStation;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.graphics.systems.RenderingSystem.BuildingOverlay;
import bankcity.graphics.systems.RenderingSystem.Overlay;

public class Police implements BuildingCategory{


	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new PoliceStation(game));
		buildings.add(new PoliceCenter(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "police.png";
	}

	@Override
	public String getName() {
		return "Police";
	}

	@Override
	public String getDescription() {
		return "Keeps your city safe from criminals.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.SERVICES;
	}
	
	public static abstract class SimplePoliceBuilding extends Building{
		
		protected float radius;
		protected float crimeMod;

		public SimplePoliceBuilding(Game game, float price, float upkeep, float radius, float crimeMod, int workerCount, float energyConsumption, float waterConsumption) {
			super(game, price, 0, upkeep, energyConsumption, waterConsumption);
			this.radius = radius;
			this.crimeMod = crimeMod;
			functionalities.add(new JobFunctionality(EducationLevel.LOW, workerCount));
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.SERVICES;
		}
		
		@Override
		public List<Radius> createRadiuses(){
			List<Radius> radiuses = new ArrayList<>();
			radiuses.add(new Radius(game, body, radius, new BuildingEffect(SimplePoliceBuilding.this, Family.all(BuildingComponent.class).get()) {
				
				@Override
				protected void remove(Building building) {
					building.stats.get(Stat.CRIMERATE).removeMult(this);
				}
				
				@Override
				protected void apply(Building building) {
					building.stats.get(Stat.CRIMERATE).putMult(this, crimeMod);
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
					if(building instanceof SimplePoliceBuilding){
						super.getColor(building, colorInstance);
					}else{
						float crimerate = building.stats.get(Stat.CRIMERATE).getResult();
						if(crimerate > 1) crimerate = 1;
						else if(crimerate < 0) crimerate = 0;
						colorInstance.r = crimerate;
						colorInstance.g = 1 - crimerate;
						colorInstance.b = 0;
					}
				}
				
			};
		}
		
	}
}
