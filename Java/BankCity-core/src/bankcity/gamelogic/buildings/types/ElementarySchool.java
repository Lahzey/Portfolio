package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.School.SimpleSchoolBuilding;

public class ElementarySchool extends SimpleSchoolBuilding{

	public ElementarySchool(Game game) {
		super(game, 1000000, 500000, 0.5f, 5, new Type(EducationLevel.LOW, 45, 3));
	}

	@Override
	public String getName() {
		return "Elementary School";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("elementary_school.png");
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
