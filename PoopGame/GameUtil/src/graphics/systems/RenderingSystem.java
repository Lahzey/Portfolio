package graphics.systems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.BuildingComponent;
import bankcity.graphics.components.TextureComponent;
import bankcity.graphics.components.TransformComponent;

/**
 * Created by barry on 12/8/15 @ 9:49 PM.
 * Adjusted by Arno Rohner
 */
public class RenderingSystem extends SortedIteratingSystem {

    static final float PPM = 16.0f;
    static float FRUSTUM_WIDTH = Gdx.graphics.getWidth()/PPM;//37.5f;
    static float FRUSTUM_HEIGHT = Gdx.graphics.getHeight()/PPM;//.0f;

    public static final float PIXELS_TO_METRES = 1.0f / PPM;

    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();

    private SpriteBatch batch;
    private OrthographicCamera cam;
    private final Vector2 maxSize = new Vector2(100, 100);

    private ComponentMapper<TextureComponent> textureM;
    private ComponentMapper<TransformComponent> transformM;
    
    public final List<Renderable> renderBefore = new ArrayList<>();
    public final List<Renderable> renderAfter = new ArrayList<>();
    
    public final List<Overlay> overlays = new ArrayList<>();

    public RenderingSystem(SpriteBatch batch) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        textureM = ComponentMapper.getFor(TextureComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);

        this.batch = batch;

        cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        cam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
    }
    
    public void resize(int width, int height) {
    	calculateSize();
    }
    
    public void setMaxSize(Vector2 maxSize){
    	this.maxSize.set(maxSize);
    	calculateSize();
    }
    
    private void calculateSize(){
    	float screenWidth = Gdx.graphics.getWidth();
    	float screenHeight = Gdx.graphics.getHeight();
    	float screenRatio = screenWidth / screenHeight;
    	float maxSizeRatio = maxSize.x / maxSize.y;
    	if(screenRatio > maxSizeRatio){
    		cam.viewportWidth = maxSize.x;
    		cam.viewportHeight = maxSize.y / (screenRatio / maxSizeRatio);
    	}else{
    		cam.viewportWidth = maxSize.x / (maxSizeRatio / screenRatio);
    		cam.viewportHeight = maxSize.y;
    	}
    }

    @Override
    public void update(float deltaTime) {
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        for(Overlay overlay : overlays) overlay.setProjectionMatrix(cam.combined);
        batch.enableBlending();
        batch.begin();
        
        for(Renderable rend : renderBefore){
        	rend.update(deltaTime);
        	rend.render(cam, batch);
        }
        
        super.update(deltaTime);


        
        for(Renderable rend : renderAfter){
        	rend.update(deltaTime);
        	rend.render(cam, batch);
        }

        batch.end();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        TextureComponent tex = textureM.get(entity);
        TransformComponent t = transformM.get(entity);

        if (tex.region == null || tex.isHidden) {
            return;
        }
        
        float width = tex.size.x / PixelsToMeters(t.scale.x);
        float height = tex.size.y / PixelsToMeters(t.scale.y);

        float originX = width/2f;
        float originY = height/2f;
        
        Color oldColor = batch.getColor();
        batch.setColor(tex.tint.r, tex.tint.g, tex.tint.b, tex.tint.a * tex.opacity);

        batch.draw(tex.region, t.position.x - originX, t.position.y - originY, originX, originY, width, height, PixelsToMeters(t.scale.x), PixelsToMeters(t.scale.y), t.rotation);
        
        batch.setColor(oldColor);
        
        if(!overlays.isEmpty()){
            batch.end();
            for(Overlay overlay : overlays) overlay.draw(entity, t.position.x - originX, t.position.y - originY, originX, originY, width, height, PixelsToMeters(t.scale.x), PixelsToMeters(t.scale.y), t.rotation);
            batch.begin();
        }
    }

    public OrthographicCamera getCamera() {
        return cam;
    }
    
    
    
    public Vector2 getWorldCoordinates(Vector2 screenCoordinates){
    	return getWorldCoordinates(screenCoordinates.x, screenCoordinates.y);
    }
    
    public Vector2 getWorldCoordinates(float x, float y){
    	Vector3 coords = cam.unproject(new Vector3(x, y, 0));
    	return new Vector2(coords.x, coords.y);
    }
    public static Vector2 getScreenSizeInMeters(){
        meterDimensions.set(Gdx.graphics.getWidth()*PIXELS_TO_METRES,
                            Gdx.graphics.getHeight()*PIXELS_TO_METRES);
        return meterDimensions;
    }

    public static Vector2 getScreenSizeInPixesl(){
        pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return pixelDimensions;
    }

    public static float PixelsToMeters(float pixelValue){
        return pixelValue * PIXELS_TO_METRES;
    }
    
	
	public static interface Renderable{
		public void update(float deltaTime);
		public void render(OrthographicCamera cam, Batch batch);
	}
	
	/**
	 * Created by barry on 12/8/15 @ 10:22 PM.
	 */
	public static class ZComparator implements Comparator<Entity> {
	    private ComponentMapper<TransformComponent> transformM;
	    public boolean asceding = true;

	    public ZComparator(){
	        transformM = ComponentMapper.getFor(TransformComponent.class);
	    }

	    public ZComparator(boolean ascending) {
			this.asceding = ascending;
		}

		@Override
	    public int compare(Entity entityA, Entity entityB) {
	        return (int) Math.signum(transformM.get(asceding ? entityB : entityA).z -
	                transformM.get(asceding ? entityA : entityB).z);
	    }
	}
	
	
	
	
	public static interface Overlay{
		public void draw(Entity entity, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float degrees);
		public void setProjectionMatrix(Matrix4 combined);
	}
	
	public static abstract class BuildingOverlay implements Overlay{
		
		public static final Color DEFAULT_COLOR = new Color(122f / 255, 211f / 255, 1f, 0.3f); //Blue
		
		protected ShapeRenderer shapeRenderer = new ShapeRenderer();
		protected Color colorInstance = new Color();
		protected float fillOpacity = 0.3f;
		
		public void setProjectionMatrix(Matrix4 combined){
			shapeRenderer.setProjectionMatrix(combined);
		}

		@Override
		public void draw(Entity entity, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float degrees) {
			BuildingComponent buildingComp = entity.getComponent(BuildingComponent.class);
			if(buildingComp != null && isTarget(buildingComp.building)) draw(buildingComp.building, x, y, originX, originY, width, height, scaleX, scaleY, degrees);
		}
		
		public void draw(Building building, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float degrees) {
			Gdx.gl.glEnable(GL30.GL_BLEND);
			Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
			getColor(building, colorInstance);

			colorInstance.a = 1;
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(colorInstance);
			shapeRenderer.rect(x, y, originX, originY, width, height, scaleX, scaleY, degrees);
			shapeRenderer.end();
			
			colorInstance.a = fillOpacity;
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(colorInstance);
			shapeRenderer.rect(x, y, originX, originY, width, height, scaleX, scaleY, degrees);
			shapeRenderer.end();
			
		}
		
		public void getColor(Building building, Color colorInstance){
			colorInstance.set(DEFAULT_COLOR);
		}
		
		public abstract boolean isTarget(Building building);
		
	}
	
	public static class BuildingHighlighter extends BuildingOverlay{
		
		public Building target;

		public BuildingHighlighter(Building target) {
			this.target = target;
			super.fillOpacity = 0.7f;
		}

		@Override
		public boolean isTarget(Building building) {
			return target != null && building == target;
		}
		
	}
	
	public static class BuildingClassOverlay extends BuildingOverlay{
		
		public Class<? extends Building> target;

		public BuildingClassOverlay(Class<? extends Building> target) {
			this.target = target;
		}

		@Override
		public boolean isTarget(Building building) {
			return target != null && target.isInstance(building);
		}
		
	}
}
