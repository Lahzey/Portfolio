package bankcity.tiledmap;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import bankcity.physics.BodyInfo;
import bankcity.physics.FixtureInfo;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class TiledMapCollision {

	private TiledMap tiledMap;
	private PolygonShape tileShape;
	private BodyDef bodyDef = new BodyDef();
	private float tileSize;
	
	public TiledMapCollision(TiledMap tiledMap, float tileSize){
		this.tiledMap = tiledMap;
		bodyDef.type = BodyType.StaticBody;
		this.tileSize = tileSize;
	}
	
	public void create(World world){
		tileShape = new PolygonShape();
		tileShape.setAsBox(tileSize / 2, tileSize / 2);
		
		MapProperties prop = tiledMap.getProperties();
		int mapHeight = prop.get("height", Integer.class);
		int mapWidth = prop.get("width", Integer.class);
		
		for(MapLayer layer : tiledMap.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
				for(int x = 0; x < mapWidth; x++){
					for(int y = 0; y < mapHeight; y++){
						Cell cell = tiledLayer.getCell(x, y);
						if(cell.getTile().getProperties().get("blocked", Boolean.class)){
							bodyDef.position.set(x * tileSize + tileSize/2, y * tileSize + tileSize/2);
							Body body = world.createBody(bodyDef);
							Fixture mainFixture = body.createFixture(tileShape, 0);
							FixtureInfo mainFixtureInfo = new FixtureInfo(mainFixture);
							mainFixture.setUserData(mainFixtureInfo);
							BodyInfo bodyInfo = new BodyInfo(body);
							bodyInfo.mainFixture = mainFixture;
							body.setUserData(bodyInfo);
						}
					}
				}
			}
		}
		
		tileShape.dispose();
		tileShape = null; //Set to null so the Garbage Collector can clean up the rest
	}
}
