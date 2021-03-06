package poopgame.desktop;

import java.awt.Canvas;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.graphics.GL20;

import poopgame.ui.InGameStage;

public class SwingWrapper implements ApplicationListener {

	private InGameStage inGameStage;
    private LwjglAWTCanvas lwjglCanvas;
    
    public SwingWrapper() {
    	lwjglCanvas = new LwjglAWTCanvas(this);
    }
    
    public Canvas getCanvas() {
    	return lwjglCanvas.getCanvas();
    } 

	@Override
	public void create() {
		inGameStage = new InGameStage();
	}

	@Override
	public void render() {
		lwjglCanvas.getGraphics().getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		lwjglCanvas.getGraphics().getGL20().glClearColor(255, 255, 255, 255);
		inGameStage.act(lwjglCanvas.getGraphics().getDeltaTime());
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
