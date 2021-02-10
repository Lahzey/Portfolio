package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Water.SimpleWaterBuilding;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class SewageTreatment extends SimpleWaterBuilding{

	public SewageTreatment(Game game) {
		super(game, 1000000, 50000, 10, 5, 1000);
		
		resources.addNetYield(Resource.DIRTY_WATER, -1000);
	}

	@Override
	public String getName() {
		return "Sewage Treatment Plant";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("sewage_treatment.png");
	}

	@Override
	protected float getWidth() {
		return 1.5f;
	}

	@Override
	protected float getHeight() {
		return 1.5f;
	}

}
