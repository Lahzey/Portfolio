package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.SewageTreatment;
import bankcity.gamelogic.buildings.types.WaterPump;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;
import bankcity.graphics.systems.RenderingSystem.BuildingOverlay;
import bankcity.graphics.systems.RenderingSystem.Overlay;

public class Water implements BuildingCategory{

	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new WaterPump(game));
		buildings.add(new SewageTreatment(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "water.png";
	}

	@Override
	public String getName() {
		return "Water Supply";
	}

	@Override
	public String getDescription() {
		return "Provides the city with water needed for most buildings.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.SERVICES;
	}
	
	public static abstract class SimpleWaterBuilding extends Building{

		public SimpleWaterBuilding(Game game, float price, float upkeep, int workerCount, float energyConsumption, float waterProduction) {
			super(game, price, 0, upkeep, energyConsumption, -waterProduction);
			
			functionalities.add(new JobFunctionality(EducationLevel.MEDIUM, workerCount));
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.SERVICES;
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
					if(building instanceof SimpleWaterBuilding){
						super.getColor(building, colorInstance);
					}else{
						boolean hasWater = !game.resourceSystem.unsatisfied.get(Resource.WATER).contains(building.getEntity());
						colorInstance.r = hasWater ? 0 : 1;
						colorInstance.g = hasWater ? 1 : 0;
						colorInstance.b = 0;
					}
				}
				
			};
		}
	}
}
