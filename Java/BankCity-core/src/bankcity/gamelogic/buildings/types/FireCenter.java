package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Fire.SimpleFireBuilding;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class FireCenter extends SimpleFireBuilding{

	public FireCenter(Game game) {
		super(game, 1500000, 500000, 25, 25, 1, 250);
		
		resources.addNetYield(Resource.ATTRACTIVITY, 0.25f);
	}

	@Override
	public String getName() {
		return "Firefighter Center";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("fire_center.png");
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
