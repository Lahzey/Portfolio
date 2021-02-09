package poopgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import poopgame.data.FileLocations;
import poopgame.data.TextureManager;
import poopgame.gamelogic.PoopGame;

public class InGameStage extends Stage {

	public static TextureManager TEXTURE_MANAGER;
	public static Skin SKIN;

	public static final float SMALL_PADDING = 5;
	public static final float MEDIUM_PADDING = 10;
	public static final float LARGE_PADDING = 20;

	private Table root;

	public InGameStage() {
		super(new ScreenViewport());
		TEXTURE_MANAGER = new TextureManager();
		SKIN = new Skin(Gdx.files.internal(FileLocations.SKINS + "/custom/custom.json"));
		initInterface();
	}

	private void initInterface() {
		root = new Table(); // placeholder in case I want to add In-Game UI
		root.setFillParent(true);
		root.center();

		addActor(root);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		PoopGame.getInstance().step(delta);
	}

	public void dispose() {
		PoopGame.getInstance().dispose();
	}

	public static TextureRegion getImage(String internalPath) {
		return new TextureRegion(TEXTURE_MANAGER.getTexture(internalPath));
	}

	public static TextureRegion getIcon(String name) {
		return new TextureRegion(TEXTURE_MANAGER.getTexture(FileLocations.ICONS + "/" + name));
	}

}
