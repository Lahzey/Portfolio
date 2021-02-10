package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Electricity.SimpleElectricityBuilding;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class CoalPlant extends SimpleElectricityBuilding{

	public CoalPlant(Game game) {
		super(game, 500000, 100000, 5, 50, 20);
		
		resources.addNetYield(Resource.ATTRACTIVITY, -0.25f);
	}

	@Override
	public String getName() {
		return "Coal Power Plant";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("coal_plant.png");
	}

	@Override
	protected float getWidth() {
		return 2;
	}

	@Override
	protected float getHeight() {
		return 2;
	}

}
