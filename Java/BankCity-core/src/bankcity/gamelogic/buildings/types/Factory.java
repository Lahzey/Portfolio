package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Achievement;
import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.Job;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.categories.Industry.SimpleIndustryBuilding;
import bankcity.gamelogic.buildings.components.JobProviderComponent;
import bankcity.gamelogic.buildings.components.ResourceComponent;
import bankcity.gamelogic.buildings.components.UpgradeComponent.SimpleUpgrader;
import bankcity.gamelogic.buildings.components.UpgradeComponent.UpgradeInfo;
import bankcity.gamelogic.functionality.UpgradeFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;
import bankcity.graphics.components.TextureComponent;

public class Factory extends SimpleIndustryBuilding {
	
	protected static FactoryUpgrader upgrader = new FactoryUpgrader();
	
	public Factory(Game game) {
		super(game, 250000, 50000, 10000, Resource.MATERIAL, 150, 25, 2.5f, 50);
		resources.addNetYield(Resource.ATTRACTIVITY, -0.25f);
		functionalities.add(new UpgradeFunctionality(upgrader));
	}

	@Override
	public String getName() {
		return "Factory";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("factory_1.png");
	}

	@Override
	protected float getWidth() {
		return 2f;
	}

	@Override
	protected float getHeight() {
		return 2f;
	}
	
	protected static class FactoryUpgrader extends SimpleUpgrader{
		
		public FactoryUpgrader(){
			super(new UpgradeInfo("Adds machines, increasing production. Offers less but better jobs afterwards.", 250000),
					new UpgradeInfo("Adds hightech machines, further increasing production and now also attractivity. Offers less but better jobs afterwards.", 500000));
		}

		@Override
		public void upgrade(int level, Building building) {
			ResourceComponent resourceComp = building.getEntity().getComponent(ResourceComponent.class);
			Job job = building.getEntity().getComponent(JobProviderComponent.class).jobs.get(0);
			switch(level){
			case 2:
				resourceComp.resourceProduction.get(Resource.MATERIAL).setBase(200);
				resourceComp.resourceConsumption.get(Resource.ELECTRICITY).setBase(5);
				job.capacity = 10;
				job.educationLevel = EducationLevel.LOW;
				job.fireAll();
				building.getEntity().getComponent(TextureComponent.class).region = building.getTextureByPath("factory_2.png");
				building.stats.get(Stat.INCOME).setBase(70000);
			case 3:
				resourceComp.resourceProduction.get(Resource.MATERIAL).setBase(250);
				resourceComp.resourceConsumption.get(Resource.ATTRACTIVITY).setBase(-0.05f);
				resourceComp.resourceConsumption.get(Resource.ELECTRICITY).setBase(10);
				job.capacity = 2;
				job.educationLevel = EducationLevel.MEDIUM;
				job.fireAll();
				building.getEntity().getComponent(TextureComponent.class).region = building.getTextureByPath("factory_3.png");
				building.stats.get(Stat.INCOME).setBase(105000);
			}
		}
		
		@Override
		public boolean canUpgrade(int level, Building building){
			return building.getGame().getAchievementLevel(Achievement.DIGITAL_REVOULUTION) > 0;
		}
		
	}
}
