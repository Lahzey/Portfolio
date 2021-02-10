package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.BlockHouse;
import bankcity.gamelogic.buildings.types.SingleHouse;
import bankcity.gamelogic.functionality.HomeFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class Housing implements BuildingCategory{


	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new SingleHouse(game));
		buildings.add(new BlockHouse(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "housing.png";
	}

	@Override
	public String getName() {
		return "Housing";
	}

	@Override
	public String getDescription() {
		return "Provides homes for habitants.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.RESIDENTIAL;
	}
	
	public static abstract class SimpleHousingBuilding extends Building{

		public SimpleHousingBuilding(Game game, float price, float income, float upkeep, int capacity, float attractivity, float energyConsumption, float waterConsumption) {
			super(game, price, income, upkeep, energyConsumption, waterConsumption);
			functionalities.add(new HomeFunctionality(capacity));
			
			resources.addNetYield(Resource.ATTRACTIVITY, attractivity);
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.RESIDENTIAL;
		}
		
	}
}
