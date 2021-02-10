package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Leisure.SimpleLeisureBuilding;

public class Tree extends SimpleLeisureBuilding{

	public Tree(Game game) {
		super(game, 100, 0, 0, 0.001f, 0, 0);
	}

	@Override
	public String getName() {
		return "Tree";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("tree.png");
	}

	@Override
	protected float getWidth() {
		return 0.2f;
	}

	@Override
	protected float getHeight() {
		return 0.2f;
	}

}
