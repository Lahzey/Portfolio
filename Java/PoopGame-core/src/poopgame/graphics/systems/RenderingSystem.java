package poopgame.graphics.systems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import poopgame.gamelogic.components.PlayerComponent;
import poopgame.gamelogic.components.StatsComponent;
import poopgame.gamelogic.engine.VisualSystem;
import poopgame.graphics.components.TextureComponent;
import poopgame.graphics.components.TransformComponent;
import poopgame.physics.BodyInfo;
import poopgame.physics.FixtureInfo;
import poopgame.physics.components.BodyComponent;
import poopgame.util.InternalAssetLoader;

public class RenderingSystem extends SortedIteratingSystem implements VisualSystem, Disposable {

	private final Texture BACKGROUND = new Texture(InternalAssetLoader.generateColoredImage(Color.BLACK, 1, 1)); // making this static somehow breaks it

	public final Rectangle mustRender = new Rectangle(); // this rectangle will always be rendered, this system may expand on it to fit screen size
	private final Vector2 baseCoords = new Vector2();

	private SpriteBatch batch = new SpriteBatch();
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private ScreenViewport viewport = new ScreenViewport();

	private ComponentMapper<TextureComponent> textureM;
	private ComponentMapper<TransformComponent> transformM;
	private ComponentMapper<BodyComponent> bodyM;

	private ComponentMapper<PlayerComponent> playerM;
	private ComponentMapper<StatsComponent> statsM;

	public final List<Renderable> renderBefore = new ArrayList<>();
	public final List<Renderable> renderAfter = new ArrayList<>();

	private boolean enabled = true;

	public RenderingSystem() {
		super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

		textureM = ComponentMapper.getFor(TextureComponent.class);
		transformM = ComponentMapper.getFor(TransformComponent.class);
		bodyM = ComponentMapper.getFor(BodyComponent.class);
		playerM = ComponentMapper.getFor(PlayerComponent.class);
		statsM = ComponentMapper.getFor(StatsComponent.class);

		viewport.setScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void update(float deltaTime) {
		if (!enabled || mustRender.width == 0 || mustRender.height == 0) {
			return;
		}

		// the screen size in pixels
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		// calculate the rendering scale based on the rectangle that has to be rendered
		float xMod = screenWidth / mustRender.width;
		float yMod = screenHeight / mustRender.height;
		float pixelsPerMeter = Math.min(xMod, yMod);

		// the screen size in internal units
		float screenWidthInMeters = screenWidth / pixelsPerMeter;
		float screenHeightInMeters = screenHeight / pixelsPerMeter;

		// the gaps between the desired rectangle and the actual size (one of them should be 0)
		float horizontalSideGap = (screenWidthInMeters - mustRender.width) / 2;
		float verticalSideGap = (screenHeightInMeters - mustRender.height) / 2;

		// location of the bottom left corner in the coordinate system
		baseCoords.set(mustRender.x - horizontalSideGap, mustRender.y - verticalSideGap);

		// set the scale (pixels to internal units) on the viewport
		viewport.setUnitsPerPixel(1 / pixelsPerMeter);
		viewport.update(screenWidth, screenHeight, true);

		// set camera position
		Camera cam = getCamera();
		cam.position.set(baseCoords.x + screenWidthInMeters / 2, baseCoords.y + screenHeightInMeters / 2, cam.position.z);
		cam.update();
		
		// Initialise the batch
		batch.setProjectionMatrix(getCamera().combined);
		batch.enableBlending();
		batch.begin();

		shapeRenderer.setProjectionMatrix(getCamera().combined);

		// draw a background on the area that will be shown on the screen
		batch.draw(BACKGROUND, baseCoords.x, baseCoords.y, screenWidthInMeters, screenHeightInMeters);

		// things like background textures and tile maps
		for (Renderable rend : renderBefore) {
			rend.update(deltaTime);
			rend.render(getCamera(), batch);
		}

		// draw all the texture components
		super.update(deltaTime);

		// overlays
		for (Renderable rend : renderAfter) {
			rend.update(deltaTime);
			rend.render(getCamera(), batch);
		}

		batch.end();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TextureComponent tex = textureM.get(entity);
		TransformComponent t = transformM.get(entity);
		BodyComponent body = bodyM.get(entity);
		PlayerComponent player = playerM.get(entity);
		StatsComponent stats = statsM.get(entity);

		if (tex.region == null || t.isHidden) {
			return;
		}

		float width;
		float height;
		if (body != null) {
			BodyInfo bodyInfo = (BodyInfo) body.body.getUserData();
			FixtureInfo maFixtureInfo = (FixtureInfo) bodyInfo.mainFixture.getUserData();
			width = maFixtureInfo.width;
			height = maFixtureInfo.height;
		} else {
			width = tex.region.getRegionWidth();
			height = tex.region.getRegionHeight();
		}

		float originX = width / 2f;
		float originY = height / 2f;

		Color oldColor = batch.getColor();
		if (tex.tint != null) batch.setColor(tex.tint.r, tex.tint.g, tex.tint.b, tex.opacity);

		float x = t.position.x - originX;
		float y = t.position.y - originY;

		batch.draw(tex.region, x, y, originX, originY, width, height, 1, 1, t.rotation);

		// draw health and energy bar of player
		if (player != null && stats != null) {
			batch.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.YELLOW);
			shapeRenderer.rect(x, y + height + 0.05f, width * (stats.stats.getEnergy() / 100f), 0.03f);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.rect(x, y + height + 0.1f, width * (stats.stats.getHealth() / 100f), 0.06f);
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(x, y + height + 0.05f, width, 0.03f);
			shapeRenderer.rect(x, y + height + 0.1f, width, 0.06f);
			shapeRenderer.end();
			batch.begin();
		}

		batch.setColor(oldColor);
	}

	public Camera getCamera() {
		return viewport.getCamera();
	}

	public Vector2 getWorldCoordinates(Vector2 screenCoordinates) {
		return getWorldCoordinates(screenCoordinates.x, screenCoordinates.y);
	}

	public Vector2 getWorldCoordinates(float x, float y) {
		Vector3 coords = viewport.unproject(new Vector3(x, y, 0));
		return new Vector2(coords.x, coords.y);
	}

	public static interface Renderable {
		public void update(float deltaTime);

		public void render(Camera cam, Batch batch);
	}

	/**
	 * Created by barry on 12/8/15 @ 10:22 PM.
	 */
	public static class ZComparator implements Comparator<Entity> {
		private ComponentMapper<TransformComponent> transformM;
		public boolean asceding = true;

		public ZComparator() {
			transformM = ComponentMapper.getFor(TransformComponent.class);
		}

		public ZComparator(boolean ascending) {
			this.asceding = ascending;
		}

		@Override
		public int compare(Entity entityA, Entity entityB) {
			return (int) Math.signum(transformM.get(asceding ? entityB : entityA).position.z - transformM.get(asceding ? entityA : entityB).position.z);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
	}
}
