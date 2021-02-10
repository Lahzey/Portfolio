package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Police.SimplePoliceBuilding;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class PoliceCenter extends SimplePoliceBuilding{

	public PoliceCenter(Game game) {
		super(game, 1500000, 500000, 20, 0.2f, 25, 2, 20);
		
		resources.addNetYield(Resource.ATTRACTIVITY, 0.25f);
	}

	@Override
	public String getName() {
		return "Police Center";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("police_center.png");
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
