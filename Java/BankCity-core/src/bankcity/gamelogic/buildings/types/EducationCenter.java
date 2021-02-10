package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.School.SimpleSchoolBuilding;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class EducationCenter extends SimpleSchoolBuilding{

	public EducationCenter(Game game) {
		super(game, 25000000, 50500000, 5, 50, new Type(EducationLevel.LOW, 300, 15), new Type(EducationLevel.LOW, 300, 15), new Type(EducationLevel.LOW, 300, 10));
		
		resources.addNetYield(Resource.ATTRACTIVITY, 1);
		resources.addNetYield(Resource.HEALTH_CAPACITY, 250);
	}

	@Override
	public String getName() {
		return "Education Center";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("education_center.png");
	}

	@Override
	protected float getWidth() {
		return 5f;
	}

	@Override
	protected float getHeight() {
		return 5f;
	}

}
