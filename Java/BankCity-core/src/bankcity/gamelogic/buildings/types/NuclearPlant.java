package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Electricity.SimpleElectricityBuilding;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class NuclearPlant extends SimpleElectricityBuilding{

	public NuclearPlant(Game game) {
		super(game, 10000000, 500000, 10, 1000, 200);
		
		resources.addNetYield(Resource.ATTRACTIVITY, -2.5f);
	}

	@Override
	public String getName() {
		return "Nuclear Power Plant";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("nuclear_plant.png");
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
