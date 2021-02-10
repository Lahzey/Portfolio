package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import bankcity.gamelogic.Achievement;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.IT.SimpleITBuilding;

public class ITGaming extends SimpleITBuilding{

	public ITGaming(Game game) {
		super(game, 75000, 12500, 1000, 15, 0.1f, 1);
	}

	@Override
	public String getName() {
		return "IT: Gaming";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("it_gaming.png");
	}

	@Override
	protected float getWidth() {
		return 1;
	}

	@Override
	protected float getHeight() {
		return 1;
	}
	
	@Override
	protected void build(Vector2 position){
		super.build(position);
		if(!preview) game.setAchievementLevel(Achievement.PLAYFUL, 1);
	}

}
