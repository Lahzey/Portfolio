package bankcity.gamelogic.buildings.types;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.Radius;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingEffect;
import bankcity.gamelogic.buildings.categories.Housing.SimpleHousingBuilding;
import bankcity.gamelogic.buildings.components.BuildingComponent;
import bankcity.gamelogic.buildings.components.ResourceComponent;
import bankcity.gamelogic.buildings.components.UpgradeComponent.SimpleUpgrader;
import bankcity.gamelogic.buildings.components.UpgradeComponent.UpgradeInfo;
import bankcity.gamelogic.functionality.UpgradeFunctionality;
import bankcity.gamelogic.systems.ResourceSystem.Resource;
import bankcity.graphics.components.TextureComponent;

public class BlockHouse extends SimpleHousingBuilding{
	
	protected static BlockHouseUpgrader upgrader = new BlockHouseUpgrader();
	
	protected float crimeRadius = 10;
	protected float crimeAdd = 0.2f;

	public BlockHouse(Game game) {
		super(game, 50000, 35000, 5000, 50, -0.05f, 2.5f, 25);
		
		functionalities.add(new UpgradeFunctionality(upgrader));
	}

	@Override
	public String getName() {
		return "Tower Block";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("block_house_1.png");
	}

	@Override
	protected float getWidth() {
		return 1.5f;
	}

	@Override
	protected float getHeight() {
		return 1.5f;
	}
	
	@Override
	public List<Radius> createRadiuses(){
		List<Radius> radiuses = new ArrayList<>();
		Radius radius = new Radius(game, body, crimeRadius, new BuildingEffect(BlockHouse.this, Family.all(BuildingComponent.class).get()) {
			
			@Override
			protected void remove(Building building) {
				building.stats.get(Stat.CRIMERATE).removeAdd(this);
			}
			
			@Override
			protected void apply(Building building) {
				building.stats.get(Stat.CRIMERATE).putAdd(this, crimeAdd);
			}
		});
		radius.color = new Color(196f / 255, 120f / 255, 0f, 1f); //Orange
		radiuses.add(radius);
		return radiuses;
	}
	
	protected static class BlockHouseUpgrader extends SimpleUpgrader{
		
		public BlockHouseUpgrader(){
			super(new UpgradeInfo("Increases income and attractivity", 100000), new UpgradeInfo("Increases income and attractivity", 150000));
		}

		@Override
		public void upgrade(int level, Building building) {
			ResourceComponent resourceComp = building.getEntity().getComponent(ResourceComponent.class);
			switch(level){
			case 2:
				resourceComp.resourceConsumption.get(Resource.ATTRACTIVITY).setBase(0.03f);
				resourceComp.resourceConsumption.get(Resource.WATER).setBase(50);
				resourceComp.resourceConsumption.get(Resource.ELECTRICITY).setBase(5);
				building.getEntity().getComponent(TextureComponent.class).region = building.getTextureByPath("block_house_2.png");
				building.stats.get(Stat.INCOME).setBase(70000);
			case 3:
				resourceComp.resourceConsumption.get(Resource.ATTRACTIVITY).setBase(0f);
				resourceComp.resourceConsumption.get(Resource.WATER).setBase(75);
				resourceComp.resourceConsumption.get(Resource.ELECTRICITY).setBase(7.5f);
				building.getEntity().getComponent(TextureComponent.class).region = building.getTextureByPath("block_house_3.png");
				building.stats.get(Stat.INCOME).setBase(105000);
			}
		}
		
	}

}
