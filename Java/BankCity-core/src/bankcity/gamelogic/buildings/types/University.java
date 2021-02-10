package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.School.SimpleSchoolBuilding;

public class University extends SimpleSchoolBuilding{

	public University(Game game) {
		super(game, 5000000, 2500000, 2, 20, new Type(EducationLevel.HIGH, 180, 6));
	}

	@Override
	public String getName() {
		return "University";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("university.png");
	}

	@Override
	protected float getWidth() {
		return 3f;
	}

	@Override
	protected float getHeight() {
		return 3f;
	}

}
