package bankcity.ui.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import bankcity.gamelogic.GameObject.CollisionBits;
import bankcity.gamelogic.buildings.Building;
import bankcity.graphics.components.TextureComponent;
import bankcity.graphics.systems.RenderingSystem.Overlay;
import bankcity.physics.BodyInfo;
import bankcity.physics.FixtureInfo;
import bankcity.physics.components.BodyComponent;
import bankcity.ui.stages.InGameStage;

public class BuildingCreator extends InputAdapter {

	private InGameStage inGameStage;

	public final Color VALID_COLOR = Color.GREEN;
	public final Color INVALID_COLOR = Color.RED;

	public final int EXIT_KEY = Keys.ESCAPE;
	public final int ROTATE_LEFT_KEY = Keys.Q;
	public final int ROTATE_RIGHT_KEY = Keys.E;
	public final int FILL_KEY = Keys.F;

	public final float ROTATION_PER_SEC = (float) Math.PI * 2;
	private int rotation = 0;

	private Building preview = null;
	private Overlay overlay;
	private boolean valid;

	public BuildingCreator(InGameStage inGameStage) {
		this.inGameStage = inGameStage;
	}

	private void setValid(boolean valid) {
		this.valid = valid;
		if (preview != null) {
			TextureComponent textureComponent = preview.getEntity().getComponent(TextureComponent.class);
			if (textureComponent != null) {
				if (valid)
					textureComponent.tint.set(VALID_COLOR);
				else
					textureComponent.tint.set(INVALID_COLOR);
			}
		}
	}

	public boolean isValid() {
		return valid;
	}

	private void revalidate() {
		if (preview != null) {
			if(preview.getPrice() > preview.getGame().money){
				setValid(false);
				return;
			}
			BodyInfo bodyInfo = (BodyInfo) preview.getEntity().getComponent(BodyComponent.class).body.getUserData();
			FixtureInfo mainFixtureInfo = (FixtureInfo) bodyInfo.mainFixture.getUserData();
			for(FixtureInfo colliding : mainFixtureInfo.colliding){
				if(colliding.fixture.getFilterData().categoryBits == CollisionBits.BUILDINGS.bits){
					setValid(false);
					return;
				}
			}
		}
		setValid(preview != null);
	}

	public Building getPreview() {
		return preview;
	}

	public void setPreview(Building preview) {
		if (this.preview != null) {
			this.preview.safeDestroy();
			this.preview = null;
		}
		if(overlay != null){
			inGameStage.game.renderingSystem.overlays.remove(overlay);
		}
		if (preview != null) {
			overlay = preview.getOverlay();
			if(overlay != null) inGameStage.game.renderingSystem.overlays.add(overlay);
			preview.setPreview(true);
			preview.safeBuild(getMouseCoordinates(), new Runnable(){

				@Override
				public void run() {
					preview.getEntity().getComponent(TextureComponent.class).opacity = 0.5f;
					BuildingCreator.this.preview = preview;
				}
				
			});
		}
		inGameStage.setBuildModeEnabled(preview != null);
	}

	private void build() {
		Building selection = this.preview; //make local copy to prevent Nullpointer when building and deselecting right after
		if (selection != null) {
			selection.getGame().money -= selection.getPrice();
			Building building = selection.copy();
			building.setPreview(false);
			float rotation = selection.getRotation();
			building.safeBuild(selection.getPosition(), new Runnable() {

				@Override
				public void run() {
					building.setRotation(rotation);
					if (!selection.isEnabled()) setPreview(null);
				}
			});
		}
	}

	private Vector2 getMouseCoordinates() {
		return inGameStage.game.renderingSystem.getWorldCoordinates(Gdx.input.getX(), Gdx.input.getY());
	}

	@Override
	public boolean keyUp(int keycode) {
		if (preview != null) {
			switch (keycode) {
			case EXIT_KEY:
				setPreview(null);
				return true;
			case ROTATE_LEFT_KEY:
				if (rotation == -1)
					rotation = 0;
				return true;
			case ROTATE_RIGHT_KEY:
				if (rotation == 1)
					rotation = 0;
				return true;
			case FILL_KEY:
				//will probably never be implemented
			}
		}
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (preview != null) {
			switch (keycode) {
			case ROTATE_LEFT_KEY:
				rotation = -1;
				return true;
			case ROTATE_RIGHT_KEY:
				rotation = 1;
				return true;
			}
		}
		return false;
	}

	public void update(float delta) {
		if (preview != null) {
			BodyComponent bodyComp = preview.getEntity().getComponent(BodyComponent.class);
			bodyComp.body.setTransform(getMouseCoordinates(), bodyComp.body.getAngle() + ROTATION_PER_SEC * rotation * delta);
			revalidate();
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (preview != null) {

			if (button == Buttons.LEFT) {
				revalidate();
				if(valid) build();
			} else if (button == Buttons.RIGHT) {
				setPreview(null);
			}
			return true;
		}

		return false;
	}

}
