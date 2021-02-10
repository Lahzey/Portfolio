package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Police.SimplePoliceBuilding;

public class PoliceStation extends SimplePoliceBuilding{

	public PoliceStation(Game game) {
		super(game, 250000, 100000, 10, 0.5f, 5, 0.2f, 2);
	}

	@Override
	public String getName() {
		return "Police Station";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("police_station.png");
	}

	@Override
	protected float getWidth() {
		return 1;
	}

	@Override
	protected float getHeight() {
		return 0.5f;
	}

}
