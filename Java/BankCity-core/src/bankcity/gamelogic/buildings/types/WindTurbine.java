package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Electricity.SimpleElectricityBuilding;

public class WindTurbine extends SimpleElectricityBuilding{

	public WindTurbine(Game game) {
		super(game, 10000, 1000, 0, 1, 0);
	}

	@Override
	public String getName() {
		return "Wind Turbine";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("wind_turbine.png");
	}

	@Override
	protected float getWidth() {
		return 0.5f;
	}

	@Override
	protected float getHeight() {
		return 0.5f;
	}

}
