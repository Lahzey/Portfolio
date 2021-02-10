package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Water.SimpleWaterBuilding;

public class WaterPump extends SimpleWaterBuilding{

	public WaterPump(Game game) {
		super(game, 50000, 1000, 1, 0.2f, 100);
	}

	@Override
	public String getName() {
		return "Water Pump";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("water_pump.png");
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
