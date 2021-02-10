package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Post.SimplePostBuilding;

public class PostCenter extends SimplePostBuilding{

	public PostCenter(Game game) {
		super(game, 3000000, 250000, 100, 10, 0.5f, 2);
	}

	@Override
	public String getName() {
		return "Post Center";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("post_center.png");
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
