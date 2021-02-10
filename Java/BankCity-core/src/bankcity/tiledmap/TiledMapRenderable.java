package bankcity.tiledmap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;

import bankcity.graphics.systems.RenderingSystem.Renderable;

public class TiledMapRenderable extends OrthogonalTiledMapRenderer implements Renderable{

	public TiledMapRenderable(TiledMap tiledMap){
		super(tiledMap);
		
	}
	
	public TiledMapRenderable(TiledMap tiledMap, float tileSize){
		this(tiledMap);
		setTileSize(tileSize);
	}
	
	public void setTileSize(float size){
		unitScale = size / getMap().getProperties().get("tilewidth", Integer.class);
	}
	
	public float getWidth(){
		MapProperties prop = getMap().getProperties();
		int mapWidth = prop.get("width", Integer.class);
		int tilePixelWidth = prop.get("tilewidth", Integer.class);
		return mapWidth * tilePixelWidth * unitScale;
	}
	
	public float getHeight(){
		MapProperties prop = getMap().getProperties();
		int mapHeight = prop.get("height", Integer.class);
		int tilePixelHeight = prop.get("tileheight", Integer.class);
		return mapHeight * tilePixelHeight * unitScale;
	}
	
	public float getTileWidth(){
		return getMap().getProperties().get("tilewidth", Integer.class) * unitScale;
	}
	
	public float getTileHeight(){
		return getMap().getProperties().get("tileheight", Integer.class) * unitScale;
	}

	@Override
	public void update(float deltaTime) {
		//Nothing to do
	}

	@Override
	public void render(OrthographicCamera cam, Batch batch) {
		setView(cam);
		super.batch = batch;
		AnimatedTiledMapTile.updateAnimationBaseTime();
		for (MapLayer layer : map.getLayers()) {
			renderMapLayer(layer);
		}
	}

}
