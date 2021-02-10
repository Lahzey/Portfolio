package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Shop.SimpleShopBuilding;

public class Supermarket extends SimpleShopBuilding{

	public Supermarket(Game game) {
		super(game, 150000, 50000, 10000, 250, 250, 10, 0.5f, 5);
	}

	@Override
	public String getName() {
		return "Supermarket";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("supermarket.png");
	}

	@Override
	protected float getWidth() {
		return 2.5f;
	}

	@Override
	protected float getHeight() {
		return 2;
	}

}
