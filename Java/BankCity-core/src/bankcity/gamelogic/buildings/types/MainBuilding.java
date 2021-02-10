package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import bankcity.data.Text;
import bankcity.gamelogic.Achievement;
import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.Job;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.components.JobProviderComponent;
import bankcity.gamelogic.buildings.components.ResourceComponent;
import bankcity.gamelogic.buildings.components.UpgradeComponent.Upgrader;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.gamelogic.functionality.UpgradeFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class MainBuilding extends Building{
	
	private static final int[] RECRUITER_REQUIREMENTS = {1, 10, 20, 50, 75, 100, 150, 250, 500, 1000};

	private static double upgradeCostPerLevel = 1000000;
	private static float energyProductionPerLevel = 5;
	private static float waterProductionPerLevel = 50;
	private static float foodProductionPerLevel = 50;
	private static float attractivityPerLevel = 1;
	private static int workersPerLevel = 100;

	protected static MainBuildingUpgrader upgrader = new MainBuildingUpgrader();
	

	public MainBuilding(Game game) {
		super(game, 0, 0, 0, -energyProductionPerLevel, -waterProductionPerLevel);
		
		Job job = new Job(null, EducationLevel.HIGH, workersPerLevel);
		job.priority = Job.MAIN_BUILDING_PRIORITY;
		JobFunctionality jobFunct = new JobFunctionality(job);
		jobFunct.affectEfficiency = false;
		functionalities.add(jobFunct);
		
		resources.addNetYield(Resource.FOOD, foodProductionPerLevel);
		resources.addNetYield(Resource.ATTRACTIVITY, attractivityPerLevel);
		functionalities.add(new UpgradeFunctionality(upgrader));
	}

	@Override
	public String getName() {
		return Text.MAIN_BUILDING.get();
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("main_building.png");
	}

	@Override
	public float getWidth() {
		return 5;
	}

	@Override
	public float getHeight() {
		return 5;
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.BUSINESS;
	}
	
	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		
		JobProviderComponent jobComp = entity.getComponent(JobProviderComponent.class);
		int occupied = jobComp.getTotalOccupiedJobCount();
		for(int i = RECRUITER_REQUIREMENTS.length; i > 0; i--){
			if(occupied >= RECRUITER_REQUIREMENTS[i - 1]){
				game.setAchievementLevel(Achievement.RECRUITER, i);
				break;
			}
		}
	}
	
	@Override
	public void build(Vector2 position){
		super.build(position);
		if(!preview) game.setAchievementLevel(Achievement.GETTING_STARTED, 1);
	}

	public boolean isVisible(){
		return game.getAchievementLevel(Achievement.GETTING_STARTED) == 0;
	}
	
	protected static class MainBuildingUpgrader implements Upgrader{

		@Override
		public int getMaxLevel(Building building) {
			return 1000;
		}

		@Override
		public boolean canUpgrade(int level, Building building) {
			return level <= getMaxLevel(building);
		}

		@Override
		public String getUpgradeText(int level, Building building) {
			return "Improves production and offers more jobs.";
		}

		@Override
		public double getCost(int level, Building building) {
			return (level - 1) * upgradeCostPerLevel;
		}

		@Override
		public void upgrade(int level, Building building) {
			ResourceComponent resourceComp = building.getEntity().getComponent(ResourceComponent.class);
			resourceComp.resourceProduction.get(Resource.ELECTRICITY).setBase(energyProductionPerLevel * level);
			resourceComp.resourceProduction.get(Resource.WATER).setBase(waterProductionPerLevel * level);
			resourceComp.resourceProduction.get(Resource.FOOD).setBase(foodProductionPerLevel * level);
			resourceComp.resourceProduction.get(Resource.ATTRACTIVITY).setBase(attractivityPerLevel * level);
			
			Job job = building.getEntity().getComponent(JobProviderComponent.class).jobs.get(0);
			job.capacity = workersPerLevel * level;
		}
		
	}
	

}
