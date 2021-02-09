package poopgame.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

import poopgame.ui.InGameStage;

public class GdxWrapper implements ApplicationListener {

	private InGameStage inGameStage;
	
	@Override
	public void create() {
		inGameStage = new InGameStage();
	}

	@Override
	public void render() {
		inGameStage.act(Gdx.graphics.getDeltaTime());
		inGameStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		inGameStage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		inGameStage.dispose();
	}
}
