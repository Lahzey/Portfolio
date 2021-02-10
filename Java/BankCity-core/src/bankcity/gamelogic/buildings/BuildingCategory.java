package bankcity.gamelogic.buildings;

import java.util.List;

import bankcity.gamelogic.Game;

public interface BuildingCategory {

	public List<Building> getBuildings(Game game);
	
	public String getIconName();
	
	public String getName();
	
	public String getDescription();
	
	public BuildingSector getSector();
	
}
