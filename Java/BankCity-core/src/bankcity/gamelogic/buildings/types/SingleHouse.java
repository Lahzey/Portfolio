package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.categories.Housing.SimpleHousingBuilding;
import bankcity.gamelogic.buildings.components.ResourceComponent;
import bankcity.gamelogic.buildings.components.UpgradeComponent.SimpleUpgrader;
import bankcity.gamelogic.buildings.components.UpgradeComponent.UpgradeInfo;
import bankcity.gamelogic.functionality.UpgradeFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;
import bankcity.graphics.components.TextureComponent;

public class SingleHouse extends SimpleHousingBuilding{
	
	protected static SingleHouseUpgrader upgrader = new SingleHouseUpgrader();

	public SingleHouse(Game game) {
		super(game, 10000, 5000, 1000, 5, 0.01f, 0.25f, 2.5f);
		
		functionalities.add(new UpgradeFunctionality(upgrader));
	}

	@Override
	public String getName() {
		return "Detached House";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("single_house_1.png");
	}

	@Override
	protected float getWidth() {
		return 1;
	}

	@Override
	protected float getHeight() {
		return 1;
	}
	
	protected static class SingleHouseUpgrader extends SimpleUpgrader{
		
		public SingleHouseUpgrader(){
			super(new UpgradeInfo("Increases income and attractivity", 25000), new UpgradeInfo("Increases income and attractivity", 50000));
		}

		@Override
		public void upgrade(int level, Building building) {
			ResourceComponent resourceComp = building.getEntity().getComponent(ResourceComponent.class);
			switch(level){
			case 2:
				resourceComp.resourceProduction.get(Resource.ATTRACTIVITY).setBase(0.02f);
				resourceComp.resourceConsumption.get(Resource.WATER).setBase(5f);
				resourceComp.resourceConsumption.get(Resource.ELECTRICITY).setBase(0.5f);
				building.getEntity().getComponent(TextureComponent.class).region = building.getTextureByPath("single_house_2.png");
				building.stats.get(Stat.INCOME).setBase(10000);
			case 3:
				resourceComp.resourceProduction.get(Resource.ATTRACTIVITY).setBase(0.03f);
				resourceComp.resourceConsumption.get(Resource.WATER).setBase(7.5f);
				resourceComp.resourceConsumption.get(Resource.ELECTRICITY).setBase(0.75f);
				building.getEntity().getComponent(TextureComponent.class).region = building.getTextureByPath("single_house_3.png");
				building.stats.get(Stat.INCOME).setBase(15000);
			}
		}
		
	}

}
