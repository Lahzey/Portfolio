package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Achievement;
import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.Job;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.categories.Industry.SimpleIndustryBuilding;
import bankcity.gamelogic.buildings.components.ResourceComponent;
import bankcity.gamelogic.buildings.components.UpgradeComponent.SimpleUpgrader;
import bankcity.gamelogic.buildings.components.UpgradeComponent.UpgradeInfo;
import bankcity.gamelogic.functionality.HomeFunctionality;
import bankcity.gamelogic.functionality.JobFunctionality;
import bankcity.gamelogic.functionality.UpgradeFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;
import bankcity.graphics.components.TextureComponent;

public class Farm extends SimpleIndustryBuilding {
	
	protected static FarmUpgrader upgrader = new FarmUpgrader();
	
	public Farm(Game game) {
		super(game, 100000, 20000, 1000, Resource.FOOD, 250, 0, 0.5f, 50); //giving 0 so i can make a custom job
		resources.addNetYield(Resource.ATTRACTIVITY, 0.1f);
		
		Job job = new Job(null, EducationLevel.UNEDUCATED, 5);
		job.onlyForSameBuilding = true;
		functionalities.add(new JobFunctionality(job));
		functionalities.add(new HomeFunctionality(5));
		functionalities.add(new UpgradeFunctionality(upgrader));
	}

	@Override
	public String getName() {
		return "Farm";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("farm_1.png");
	}

	@Override
	protected float getWidth() {
		return 7.5f;
	}

	@Override
	protected float getHeight() {
		return 7.5f;
	}
	
	protected static class FarmUpgrader extends SimpleUpgrader{
		
		public FarmUpgrader(){
			super(new UpgradeInfo("Increases production by adding high tech machines.", 150000));
		}

		@Override
		public void upgrade(int level, Building building) {
			ResourceComponent resourceComp = building.getEntity().getComponent(ResourceComponent.class);
			switch(level){
			case 2:
				resourceComp.resourceProduction.get(Resource.FOOD).setBase(500);
				resourceComp.resourceConsumption.get(Resource.ELECTRICITY).setBase(1.5f);
				building.getEntity().getComponent(TextureComponent.class).region = building.getTextureByPath("farm_2.png");
				building.stats.get(Stat.INCOME).setBase(20000);
			}
		}
		
		@Override
		public boolean canUpgrade(int level, Building building){
			return building.getGame().getAchievementLevel(Achievement.DIGITAL_REVOULUTION) > 0;
		}
		
	}
}
