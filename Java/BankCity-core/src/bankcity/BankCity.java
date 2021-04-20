package bankcity;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import bankcity.data.FileLocations;
import bankcity.data.NameGenerator;
import bankcity.data.TextureManager;
import bankcity.ui.stages.InGameStage;
import bankcity.ui.stages.LoadingStage;
import bankcity.util.GameDate;

public class BankCity extends Game {
	
	public static TextureManager TEXTURE_MANAGER;
	public static Skin SKIN;
	
	public static Stage CURRENT_STAGE;
	
	
	@Override
	public void create () {
		TEXTURE_MANAGER = new TextureManager();
		SKIN = new Skin(Gdx.files.internal(FileLocations.SKINS + "/custom/custom.json"));
		
		CURRENT_STAGE = new LoadingStage(this);
		
		Timer.schedule(new Task() {
			
			@Override
			public void run() {
				GdxNativesLoader.load();
				NameGenerator.init();
				InGameStage inGameStage = new InGameStage(BankCity.this);
				CURRENT_STAGE.dispose();
				CURRENT_STAGE = inGameStage;
				Gdx.input.setInputProcessor(inGameStage.getInputProcessor());
			}
		}, 0);
	}
	

	@Override
	public void render () {
		super.render();
		CURRENT_STAGE.act(Gdx.graphics.getDeltaTime());
		CURRENT_STAGE.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		CURRENT_STAGE.getViewport().update(width, height, true);
		if(CURRENT_STAGE instanceof InGameStage){
			((InGameStage) CURRENT_STAGE).game.renderingSystem.resize(width, height);
		}
	}
	
	public static GameDate getDate(){
		if(CURRENT_STAGE instanceof InGameStage) {
			return ((InGameStage) CURRENT_STAGE).game.timeSystem.date;
		}else return null;
	}
	
	public static TextureRegion getImage(String internalPath) {
		return new TextureRegion(TEXTURE_MANAGER.getTexture(internalPath));
	}
	
	public static TextureRegion getIcon(String name) {
		return new TextureRegion(TEXTURE_MANAGER.getTexture(FileLocations.ICONS + "/" + name));
	}
}
