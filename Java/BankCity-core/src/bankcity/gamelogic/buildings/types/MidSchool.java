package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.School.SimpleSchoolBuilding;

public class MidSchool extends SimpleSchoolBuilding{

	public MidSchool(Game game) {
		super(game, 2000000, 1000000, 1, 10, new Type(EducationLevel.MEDIUM, 90, 6));
	}

	@Override
	public String getName() {
		return "Mid School";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("mid_school.png");
	}

	@Override
	protected float getWidth() {
		return 2f;
	}

	@Override
	protected float getHeight() {
		return 2f;
	}

}
