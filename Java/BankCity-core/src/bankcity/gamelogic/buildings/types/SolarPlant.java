package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Electricity.SimpleElectricityBuilding;

public class SolarPlant extends SimpleElectricityBuilding{

	public SolarPlant(Game game) {
		super(game, 100000, 5000, 0, 10, 0);
	}

	@Override
	public String getName() {
		return "Solar Power Plant";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("solar_plant.png");
	}

	@Override
	protected float getWidth() {
		return 2;
	}

	@Override
	protected float getHeight() {
		return 1;
	}

}
