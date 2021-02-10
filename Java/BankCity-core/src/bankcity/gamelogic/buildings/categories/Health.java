package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.Hospital;
import bankcity.gamelogic.buildings.types.Infirmary;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class Health implements BuildingCategory{


	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new Infirmary(game));
		buildings.add(new Hospital(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "health.png";
	}

	@Override
	public String getName() {
		return "Healthcare";
	}

	@Override
	public String getDescription() {
		return "Keeps your habitants healthy.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.SERVICES;
	}
	
	public static abstract class SimpleHealthBuilding extends Building{

		public SimpleHealthBuilding(Game game, float price, float upkeep, int capacity, int workerCount, float energyConsumption, float waterConsumption) {
			super(game, price, 0, upkeep, energyConsumption, waterConsumption);
			functionalities.add(new JobFunctionality(EducationLevel.HIGH, workerCount));
			resources.addNetYield(Resource.HEALTH_CAPACITY, capacity);
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.SERVICES;
		}
		
	}
}
