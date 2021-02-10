package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.Fountain;
import bankcity.gamelogic.buildings.types.Tree;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class Leisure implements BuildingCategory{


	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new Tree(game));
		buildings.add(new Fountain(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "leisure.png";
	}

	@Override
	public String getName() {
		return "Leisure";
	}

	@Override
	public String getDescription() {
		return "Raises the attractivity of your town for relatively low costs.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.LEISURE;
	}
	
	public static abstract class SimpleLeisureBuilding extends Building{

		public SimpleLeisureBuilding(Game game, float price, float income, float upkeep, float attractivity, float energyConsumption, float waterConsumption) {
			super(game, price, income, upkeep, energyConsumption, waterConsumption);
			resources.addNetYield(Resource.ATTRACTIVITY, attractivity);
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.LEISURE;
		}
		
	}
}
