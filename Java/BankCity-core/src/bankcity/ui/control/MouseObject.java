package bankcity.ui.control;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.GameObject;

public class MouseObject extends GameObject implements InputProcessor{
	
	public MouseObject(Game game){
		super(game);
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(body != null) body.setTransform(game.renderingSystem.getWorldCoordinates(screenX, screenY), 0);
		return false;
	}

	@Override
	public TextureRegion getTexture() {
		return null;
	}

	@Override
	protected float getWidth() {
		return 0.01f;
	}

	@Override
	protected float getHeight() {
		return 0.01f;
	}

	@Override
	protected short getCategoryBits() {
		return CollisionBits.OTHER.bits;
	}

	@Override
	protected short getMaskBits() {
		return CollisionBits.BUILDINGS.bits;
	}
	
	
	//Unused methods

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
