package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Health.SimpleHealthBuilding;

public class Infirmary extends SimpleHealthBuilding{

	public Infirmary(Game game) {
		super(game, 500000, 500000, 100, 5, 0.2f, 2);
	}

	@Override
	public String getName() {
		return "Infirmary";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("infirmary.png");
	}

	@Override
	protected float getWidth() {
		return 1;
	}

	@Override
	protected float getHeight() {
		return 0.25f;
	}

}
