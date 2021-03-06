package bankcity.gamelogic.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

import bankcity.gamelogic.Game;
import bankcity.graphics.components.TransformComponent;
import bankcity.physics.FixtureInfo;
import bankcity.ui.control.MouseObject;

public class InputSystem extends EntitySystem implements InputProcessor{

	public final List<InputProcessor> listeners = new ArrayList<>();
	private MouseObject mouseObject;
	
	private final List<Entity> newColliding = new ArrayList<>();
	private Entity currentlyHovering;
	private Entity entered;
	private Entity exited;
	
	
	public InputSystem(Game game) {
		mouseObject = new MouseObject(game);
		mouseObject.safeBuild(new Vector2());
		listeners.add(mouseObject);
	}


	public void update (float deltaTime) {
		newColliding.clear();
		if(!mouseObject.isBuilt()) return;
		for(FixtureInfo fixtureInfo : mouseObject.getFixtureInfo().colliding){
			if(fixtureInfo.entity != null) newColliding.add(fixtureInfo.entity);
		}
		Entity highest = null;
		float highestZ = 0;
		for(Entity colliding : newColliding){
			TransformComponent transform = colliding.getComponent(TransformComponent.class);
			float z = (transform == null ? Float.NEGATIVE_INFINITY : transform.z);
			if(highest == null || z > highestZ){
				highest = colliding;
				highestZ = z;
			}
		}
		if(currentlyHovering != highest){
			exited = currentlyHovering;
			entered = highest;
			currentlyHovering = highest;
		}else{
			exited = null;
			entered = null;
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		for(InputProcessor listener : listeners){
			if(listener.keyDown(keycode)) return true;
		}
		return false;
	}


	@Override
	public boolean keyUp(int keycode) {
		for(InputProcessor listener : listeners){
			if(listener.keyUp(keycode)) return true;
		}
		return false;
	}


	@Override
	public boolean keyTyped(char character) {
		for(InputProcessor listener : listeners){
			if(listener.keyTyped(character)) return true;
		}
		return false;
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for(InputProcessor listener : listeners){
			if(listener.touchDown(screenX, screenY, pointer, button)) return true;
		}
		return false;
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for(InputProcessor listener : listeners){
			if(listener instanceof EntityInputListener){
				EntityInputListener entityListener = (EntityInputListener) listener;
				if(currentlyHovering != null && entityListener.entityClicked(currentlyHovering, screenX, screenY, pointer, button)) return true;
			}
			if(listener.touchUp(screenX, screenY, pointer, button)) return true;
		}
		return false;
	}


	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for(InputProcessor listener : listeners){
			if(listener.touchDragged(screenX, screenY, pointer)) return true;
		}
		return false;
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for(InputProcessor listener : listeners){
			if(listener instanceof EntityInputListener){
				EntityInputListener entityListener = (EntityInputListener) listener;
				if(exited != null && entityListener.entityExited(exited, screenX, screenY)) return true;
				if(entered != null && entityListener.entityEntered(entered, screenX, screenY)) return true;
			}
			if(listener.mouseMoved(screenX, screenY)) return true;
		}
		return false;
	}


	@Override
	public boolean scrolled(int amount) {
		for(InputProcessor listener : listeners){
			if(listener.scrolled(amount)) return true;
		}
		return false;
	}
	
	
	public static interface EntityInputListener extends InputProcessor{
		public boolean entityEntered(Entity entity, int screenX, int screenY);
		public boolean entityExited(Entity entity, int screenX, int screenY);
		public boolean entityClicked(Entity entity, int screenX, int screenY, int pointer, int button);
	}
	
	public static class EntityInputAdapter extends InputAdapter implements EntityInputListener{

		@Override
		public boolean entityEntered(Entity entity, int screenX, int screenY) {
			return false;
		}

		@Override
		public boolean entityExited(Entity entity, int screenX, int screenY) {
			return false;
		}

		@Override
		public boolean entityClicked(Entity entity, int screenX, int screenY, int pointer, int button) {
			return false;
		}
	}
}
