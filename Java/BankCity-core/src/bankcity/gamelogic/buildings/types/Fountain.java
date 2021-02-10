package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Leisure.SimpleLeisureBuilding;

public class Fountain extends SimpleLeisureBuilding{

	public Fountain(Game game) {
		super(game, 500, 0, 10, 0.005f, 0, 0);
	}

	@Override
	public String getName() {
		return "Fountain";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("fountain.png");
	}

	@Override
	protected float getWidth() {
		return 0.25f;
	}

	@Override
	protected float getHeight() {
		return 0.25f;
	}

}
