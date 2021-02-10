package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.GeneralStore;
import bankcity.gamelogic.buildings.types.OnlineStore;
import bankcity.gamelogic.buildings.types.Supermarket;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.gamelogic.functionality.ShopFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class Shop implements BuildingCategory{

	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new Supermarket(game));
		buildings.add(new GeneralStore(game));
		buildings.add(new OnlineStore(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "shop.png";
	}

	@Override
	public String getName() {
		return "Shop";
	}

	@Override
	public String getDescription() {
		return "Sells goods, makes profit and provides jobs. Not required, but still useful.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.BUSINESS;
	}
	
	public static abstract class SimpleShopBuilding extends Building{
		
		protected Map<Resource, Float> offer;
		
		public SimpleShopBuilding(Game game, float price, float income, float upkeep, float foodOffer, float materialOffer, int workerCount, float energyConsumption, float waterConsumption) {
			this(game, price, income, upkeep, createDefaultOffer(foodOffer, materialOffer), workerCount, energyConsumption, waterConsumption);
		}

		public SimpleShopBuilding(Game game, float price, float income, float upkeep, Map<Resource, Float> offer, int workerCount, float energyConsumption, float waterConsumption) {
			super(game, price, income, upkeep, energyConsumption, waterConsumption);
			this.offer = offer;
			if(workerCount > 0) functionalities.add(new JobFunctionality(EducationLevel.UNEDUCATED, workerCount));
			functionalities.add(new ShopFunctionality(offer));
		}
		
		protected static Map<Resource, Float> createDefaultOffer(float foodOffer, float materialOffer){
			Map<Resource, Float> map = new HashMap<>();
			map.put(Resource.FOOD, foodOffer);
			map.put(Resource.MATERIAL, materialOffer);
			return map;
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.BUSINESS;
		}
	}
}
