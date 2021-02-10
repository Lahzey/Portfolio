package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.MainBuilding;

public class Rest implements BuildingCategory{

	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new MainBuilding(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "rest.png";
	}

	@Override
	public String getName() {
		return "Rest";
	}

	@Override
	public String getDescription() {
		return "All building not fitting in any category.";
	}

	@Override
	public BuildingSector getSector() {
		return null;
	}

}
