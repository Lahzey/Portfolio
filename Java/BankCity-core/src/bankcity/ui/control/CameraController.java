package bankcity.ui.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import bankcity.graphics.systems.RenderingSystem;
import bankcity.tiledmap.TiledMapRenderable;

public class CameraController extends InputAdapter{
	
	private RenderingSystem system;
	private TiledMapRenderable map;

	public int upKey = Input.Keys.W;
	public int downKey = Input.Keys.S;
	public int leftKey = Input.Keys.A;
	public int rightKey = Input.Keys.D;
	public boolean allowZoom = true;
	public float zoomStrength = 0.5f;
	public float minZoom = 0.01f;
	public float maxZoom = 10f;
	
	private boolean upPressed;
	private boolean downPressed;
	private boolean leftPressed;
	private boolean rightPressed;
	private int scrollAmount = 0;
	
	private Vector2 dragPoint = new Vector2();
	private Vector2 realDragPoint = new Vector2();
	private boolean leftMouseDown = false;
	
	private Vector2 movementDirection = new Vector2();
	
	public CameraController(RenderingSystem system, TiledMapRenderable map){
		this.system = system;
		this.map = map;
	}
	
	
	public void update(float delta){
		OrthographicCamera cam = system.getCamera();
		updateZoom(cam);
		
		movementDirection.set(0, 0);
		if(upPressed){
			movementDirection.y += 1;
		}if(downPressed){
			movementDirection.y -= 1;
		}if(leftPressed){
			movementDirection.x -= 1;
		}if(rightPressed){
			movementDirection.x += 1;
		}
		movementDirection.nor();
		float mov = getMovementSpeed() * delta;
		cam.position.add(movementDirection.x * mov, movementDirection.y * mov, 0);

		float halfWidth = cam.viewportWidth * cam.zoom / 2;
		float halfHeight = cam.viewportWidth * cam.zoom / 2;
		
		if(cam.position.x - halfWidth < getMinX()){
			cam.position.x = getMinX() + halfWidth;
		}else if(cam.position.x + halfWidth > getMaxX()){
			cam.position.x = getMaxX() - halfWidth;
		}
		if(cam.position.y - halfHeight < getMinY()){
			cam.position.y = getMinY() + halfWidth;
		}else if(cam.position.y + halfHeight > getMaxY()){
			cam.position.y = getMaxY() - halfWidth;
		}
	}
	
	private void updateZoom(OrthographicCamera cam){
		if(!allowZoom) scrollAmount = 0;
		if(scrollAmount == 0) return;
		Vector3 mousePosBefore = getMousePosition(cam);
		float minZoom = getMinZoom(cam);
		float maxZoom = getMaxZoom(cam);
		float zoom = scrollAmount * zoomStrength;
		scrollAmount = 0;
		if(zoom < 0){ //zoom in
			cam.zoom /= 1 + Math.abs(zoom) * zoomStrength;
			if(cam.zoom < minZoom) cam.zoom = minZoom;
		}else if(zoom > 0){ //zoom out
			cam.zoom *= 1 + Math.abs(zoom) * zoomStrength;
			if(cam.zoom > maxZoom) cam.zoom = maxZoom;
		}
	    cam.update();
		Vector3 mousePosAfter = getMousePosition(cam);
		cam.position.x = cam.position.x - mousePosAfter.x + mousePosBefore.x;
		cam.position.y = cam.position.y - mousePosAfter.y + mousePosBefore.y;
	}
	
	private Vector3 getMousePosition(OrthographicCamera cam){
		return cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
	}


	protected float getMovementSpeed(){
		return (system.getCamera().viewportWidth + system.getCamera().viewportHeight) / 2;
	}
	
	private float getMinZoom(OrthographicCamera cam) {
		return minZoom;
	}
	
	protected float getMaxZoom(OrthographicCamera cam){
		return Math.min(maxZoom, Math.min(((getMaxX() - getMinX()) / cam.viewportWidth), (getMaxY() - getMinY()) / cam.viewportHeight));
	}
	
	/**
	 * @return lowest X coordinate the user should be able to see when moving the camera around.
	 */
	protected float getMinX(){
		return 0;
	}

	
	/**
	 * @return highest X coordinate the user should be able to see when moving the camera around.
	 */
	protected float getMaxX(){
		return map.getWidth();
	}

	
	/**
	 * @return lowest Y coordinate the user should be able to see when moving the camera around.
	 */
	protected float getMinY(){
		return 0;
	}

	
	/**
	 * @return highest Y coordinate the user should be able to see when moving the camera around.
	 */
	protected float getMaxY(){
		return map.getHeight();
	}
	

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == upKey){
			upPressed = true;
			return true;
		}else if(keycode == downKey){
			downPressed = true;
			return true;
		}else if(keycode == leftKey){
			leftPressed = true;
			return true;
		}else if(keycode == rightKey){
			rightPressed = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == upKey){
			upPressed = false;
			return true;
		}else if(keycode == downKey){
			downPressed = false;
			return true;
		}else if(keycode == leftKey){
			leftPressed = false;
			return true;
		}else if(keycode == rightKey){
			rightPressed = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		scrollAmount += amount;
		return true;
	}

	public boolean touchDown(int x, int y, int pointer, int button) {
		if(button == Buttons.LEFT){
			dragPoint.set(system.getWorldCoordinates(x, y));
			realDragPoint.set(x, y);
			leftMouseDown = true;
		}
		return false;
	}
	
	public boolean touchUp(int x, int y, int pointer, int button) {
		if(button == Buttons.LEFT){
			if(Math.abs(realDragPoint.x - x) > 0 || Math.abs(realDragPoint.y - y) > 0){
				return true; //Drag release, prevent other events
			}
			leftMouseDown = false;
		}
		return false;
	} 

	public boolean touchDragged(int x, int y, int pointer) {
		if(leftMouseDown){
			Vector2 worldCoords = system.getWorldCoordinates(x, y);
			system.getCamera().position.sub(worldCoords.x - dragPoint.x, worldCoords.y - dragPoint.y, 0);
			return true;
		}else return false;
	}

}
