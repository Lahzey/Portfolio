package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.ITBusiness;
import bankcity.gamelogic.buildings.types.ITGaming;
import bankcity.gamelogic.functionality.JobFunctionality;

public class IT implements BuildingCategory{


	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new ITBusiness(game));
		buildings.add(new ITGaming(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "it.png";
	}

	@Override
	public String getName() {
		return "IT";
	}

	@Override
	public String getDescription() {
		return "Porvides the city with the newest technology.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.BUSINESS;
	}
	
	public static abstract class SimpleITBuilding extends Building{

		public SimpleITBuilding(Game game, float price, float income, float upkeep, int workerCount, float energyConsumption, float waterConsumption) {
			super(game, price, income, upkeep, energyConsumption, waterConsumption);
			functionalities.add(new JobFunctionality(EducationLevel.MEDIUM, workerCount));
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.BUSINESS;
		}
		
	}
}
