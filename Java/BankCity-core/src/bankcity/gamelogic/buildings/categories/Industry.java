package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.Factory;
import bankcity.gamelogic.buildings.types.Farm;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class Industry implements BuildingCategory{


	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new Factory(game));
		buildings.add(new Farm(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "industry.png";
	}

	@Override
	public String getName() {
		return "Industry";
	}

	@Override
	public String getDescription() {
		return "Produces food and material making your city self sustaining.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.BUSINESS;
	}
	
	public static abstract class SimpleIndustryBuilding extends Building{

		public SimpleIndustryBuilding(Game game, float price, float income, float upkeep, Resource product, float production, int workerCount, float energyConsumption, float waterConsumption) {
			super(game, price, income, upkeep, energyConsumption, waterConsumption);
			if(workerCount > 0) functionalities.add(new JobFunctionality(EducationLevel.UNEDUCATED, workerCount));
			resources.addNetYield(product, production);
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.BUSINESS;
		}
		
	}
}
