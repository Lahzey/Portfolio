package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Fire.SimpleFireBuilding;

public class FireStation extends SimpleFireBuilding{

	public FireStation(Game game) {
		super(game, 250000, 100000, 5, 10, 0.2f, 50);
	}

	@Override
	public String getName() {
		return "Firefighter Station";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("fire_station.png");
	}

	@Override
	protected float getWidth() {
		return 1;
	}

	@Override
	protected float getHeight() {
		return 1;
	}

}
