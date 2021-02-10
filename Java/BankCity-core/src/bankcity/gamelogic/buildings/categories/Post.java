package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import bankcity.gamelogic.Achievement;
import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.PostCenter;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class Post implements BuildingCategory{


	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new PostCenter(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "post.png";
	}

	@Override
	public String getName() {
		return "Post";
	}

	@Override
	public String getDescription() {
		return "Distributes packages. Allows you to build an online shop.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.SERVICES;
	}
	
	public static abstract class SimplePostBuilding extends Building{

		public SimplePostBuilding(Game game, float price, float upkeep, float capacity, int workerCount, float energyConsumption, float waterConsumption) {
			super(game, price, 0, upkeep, energyConsumption, waterConsumption);
			functionalities.add(new JobFunctionality(EducationLevel.UNEDUCATED, workerCount));
			resources.addNetYield(Resource.POSTAL_CAPACITY, capacity);
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.SERVICES;
		}
		
		@Override
		public void build(Vector2 position){
			super.build(position);
			if(!preview) game.setAchievementLevel(Achievement.POST, 1);
		}
		
	}
}
