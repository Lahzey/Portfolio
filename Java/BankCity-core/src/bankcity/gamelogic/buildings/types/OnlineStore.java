package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Achievement;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.categories.Shop.SimpleShopBuilding;

public class OnlineStore extends SimpleShopBuilding{

	public OnlineStore(Game game) {
		super(game, 500000, 200000, 5000, 1000, 1000, 0, 1, 0);
	}

	@Override
	public String getName() {
		return "Online Store";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("online_store.png");
	}

	@Override
	protected float getWidth() {
		return 1.5f;
	}

	@Override
	protected float getHeight() {
		return 1.5f;
	}
	
	@Override
	public boolean isEnabled(){
		return super.isEnabled() && game.getAchievementLevel(Achievement.POST) > 0  && game.getAchievementLevel(Achievement.DIGITAL_REVOULUTION) > 0;
	}

}
