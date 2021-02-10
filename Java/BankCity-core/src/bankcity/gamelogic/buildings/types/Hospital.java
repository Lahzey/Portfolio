package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Health.SimpleHealthBuilding;

public class Hospital extends SimpleHealthBuilding{

	public Hospital(Game game) {
		super(game, 5000000, 2500000, 1000, 25, 1, 10);
	}

	@Override
	public String getName() {
		return "Hospital";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("hospital.png");
	}

	@Override
	protected float getWidth() {
		return 3;
	}

	@Override
	protected float getHeight() {
		return 3;
	}

}
