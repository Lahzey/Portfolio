package bankcity.gamelogic.buildings;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Electricity;
import bankcity.gamelogic.buildings.categories.Fire;
import bankcity.gamelogic.buildings.categories.Health;
import bankcity.gamelogic.buildings.categories.Housing;
import bankcity.gamelogic.buildings.categories.IT;
import bankcity.gamelogic.buildings.categories.Industry;
import bankcity.gamelogic.buildings.categories.Leisure;
import bankcity.gamelogic.buildings.categories.Police;
import bankcity.gamelogic.buildings.categories.Post;
import bankcity.gamelogic.buildings.categories.Rest;
import bankcity.gamelogic.buildings.categories.School;
import bankcity.gamelogic.buildings.categories.Shop;
import bankcity.gamelogic.buildings.categories.Water;

public class BuildingInitializer {
	
	private static List<BuildingCategory> categories = new ArrayList<>();
	private static Map<BuildingCategory, List<Building>> categoryToBuildingsMapping = new HashMap<>();
	private static List<Building> allBuildings = new ArrayList<>();
	private static Map<BuildingSector, List<Building>> sectorToBuildingsMapping = new HashMap<>();
	private static Rest rest;
	
	static{
		categories.add(new Electricity());
		categories.add(new Fire());
		categories.add(new Health());
		categories.add(new Housing());
		categories.add(new Industry());
		categories.add(new IT());
		categories.add(new Leisure());
		categories.add(new Police());
		categories.add(new Post());
		categories.add(new School());
		categories.add(new Shop());
		categories.add(new Water());
		
		rest = new Rest();
		
	}

	public static void createAll(Game game){
		createAll(game, null);
	}
	
	public static void createAll(Game game, ProgressMonitor monitor){
		//Load objects
		for(BuildingCategory category : categories){
			List<Building> buildings = category.getBuildings(game);
			allBuildings.addAll(buildings);
			categoryToBuildingsMapping.put(category, buildings);
		}
		List<Building> buildings = rest.getBuildings(game);
		allBuildings.addAll(buildings);
		categoryToBuildingsMapping.put(rest, buildings);
		
		
		 //Load textures
		for(int i = 0; i < allBuildings.size(); i++){
			Building instance = allBuildings.get(i);
			List<Building> mapped = sectorToBuildingsMapping.get(instance.getSector());
			if(mapped == null){
				mapped = new ArrayList<>();
				sectorToBuildingsMapping.put(instance.getSector(), mapped);
			}
			mapped.add(instance);
			
			instance.setPreview(true);
			instance.getTexture();
			if(monitor != null){
				monitor.update(((float) i) / allBuildings.size());
			}
		}
	}
	
	public static List<Building> getBuildings(BuildingSector sector){
		return sectorToBuildingsMapping.getOrDefault(sector, new ArrayList<>());
	}
	
	
	public static interface ProgressMonitor{
		public void update(float progress);
	}
}
