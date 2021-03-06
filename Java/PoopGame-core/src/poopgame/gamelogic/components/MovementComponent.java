package poopgame.gamelogic.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

import poopgame.util.InternalAssetLoader;

public class MovementComponent extends LogicComponent {

	public boolean moveLeft = false;
	public boolean moveRight = false;

	private String folderName;
	public Animation<TextureRegion> walkLeft;
	public Animation<TextureRegion> walkRight;
	public Animation<TextureRegion> standLeft;
	public Animation<TextureRegion> standRight;

	public MovementComponent() { }

	public MovementComponent(String folderName) {
		this.folderName = folderName;
		loadAnimations();
	}

	private void loadAnimations() {
		walkLeft = InternalAssetLoader.getAnimation(folderName + "walk_left.gif");
		walkRight = InternalAssetLoader.getAnimation(folderName + "walk_right.gif");
		standLeft = InternalAssetLoader.getAnimation(folderName + "stand_left.gif");
		standRight = InternalAssetLoader.getAnimation(folderName + "stand_right.gif");
	}

	@Override
	public Object storeState(Engine engine, World world) {
		return new Object[] { moveLeft, moveRight, folderName };
	}

	@Override
	public void loadState(Object state, Engine engine, World world) {
		Object[] values = (Object[]) state;
		moveLeft = (boolean) values[0];
		moveRight = (boolean) values[1];
		folderName = (String) values[2];

		if (walkLeft == null) {
			loadAnimations();
		}
	}

}
