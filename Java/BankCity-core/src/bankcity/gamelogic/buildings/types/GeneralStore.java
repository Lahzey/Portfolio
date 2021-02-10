package bankcity.gamelogic.buildings.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.Job;
import bankcity.gamelogic.buildings.categories.Shop.SimpleShopBuilding;
import bankcity.gamelogic.functionality.HomeFunctionality;
import bankcity.gamelogic.functionality.JobFunctionality;

public class GeneralStore extends SimpleShopBuilding{

	public GeneralStore(Game game) {
		super(game, 15000, 10000, 1000, 50, 50, 0, 0.1f, 1); //giving 0 so i can make a custom job
		
		Job job = new Job(null, EducationLevel.UNEDUCATED, 2);
		job.onlyForSameBuilding = true;
		functionalities.add(new JobFunctionality(job));
		functionalities.add(new HomeFunctionality(5));
	}

	@Override
	public String getName() {
		return "General Store";
	}

	@Override
	public TextureRegion getTexture() {
		return getTextureByPath("general_store.png");
	}

	@Override
	protected float getWidth() {
		return 1f;
	}

	@Override
	protected float getHeight() {
		return 1;
	}

}
