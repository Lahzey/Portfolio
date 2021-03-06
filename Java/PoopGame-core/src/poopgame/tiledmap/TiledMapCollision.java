package poopgame.tiledmap;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import poopgame.physics.BodyInfo;
import poopgame.physics.FixtureInfo;

import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class TiledMapCollision {

	private TiledMap tiledMap;
	private BodyDef bodyDef = new BodyDef();
	public float tileSize;
	
	private float tilePixelWidth;
	private float tilePixelHeight;

	public TiledMapCollision(TiledMap tiledMap, float tileSize) {
		this.tiledMap = tiledMap;
		tilePixelWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
		tilePixelHeight = tiledMap.getProperties().get("tileheight", Integer.class);
		
		bodyDef.type = BodyType.StaticBody;
		this.tileSize = tileSize;
	}
	
	public Vector2 getMapDimensions() {
		int width = tiledMap.getProperties().get("width", Integer.class);
		int height = tiledMap.getProperties().get("height", Integer.class);
		return new Vector2(width * tileSize, height * tileSize);
	}
	
	public List<Vector2> getTypeLocations(String type) {
		List<Vector2> locations = new ArrayList<>();
		for(MapLayer uncastedLayer : tiledMap.getLayers()) {
			if(uncastedLayer instanceof TiledMapTileLayer) {
				TiledMapTileLayer layer = (TiledMapTileLayer) uncastedLayer;
				int width = tiledMap.getProperties().get("width", Integer.class);
				int height = tiledMap.getProperties().get("height", Integer.class);
				for(int x = 0; x < width; x++) {
					for(int y = 0; y < height; y++) {
						Cell cell = layer.getCell(x, y);
						if(cell != null) {
							String tileType = cell.getTile().getProperties().get("type", String.class);
							if(tileType != null && tileType.equals(type)) locations.add(new Vector2(x * tileSize, y * tileSize));
						}
					}
				}
			}
		}
		return locations;
		
	}

	public void create(World world) {
		for (MapLayer layer : tiledMap.getLayers()) {
			for (MapObject object : layer.getObjects()) {
				Shape shape;
				if (object instanceof RectangleMapObject) {
					shape = getRectangle((RectangleMapObject) object);
				} else if (object instanceof PolygonMapObject) {
					shape = getPolygon((PolygonMapObject) object);
				} else if (object instanceof PolylineMapObject) {
					shape = getPolyline((PolylineMapObject) object);
				} else if (object instanceof CircleMapObject) {
					shape = getCircle((CircleMapObject) object);
				} else {
					continue;
				}

				Body body = world.createBody(bodyDef);
				FixtureDef fixDef = new FixtureDef();
				fixDef.friction = 0;
				fixDef.shape = shape;
				Fixture mainFixture = body.createFixture(fixDef);
				FixtureInfo mainFixtureInfo = new FixtureInfo(mainFixture);
				mainFixtureInfo.width = shape.getRadius();
				mainFixtureInfo.height = shape.getRadius();
				mainFixture.setUserData(mainFixtureInfo);
				BodyInfo bodyInfo = new BodyInfo(body);
				bodyInfo.mainFixture = mainFixture;
				body.setUserData(bodyInfo);
			}
		}
	}

	private PolygonShape getRectangle(RectangleMapObject rectangleObject) {
		Rectangle rectangle = rectangleObject.getRectangle();
		rectangle.width /= tilePixelWidth;
		rectangle.height /= tilePixelHeight;
		rectangle.x /= tilePixelWidth;
		rectangle.y /= tilePixelHeight;
		
		PolygonShape polygon = new PolygonShape();
		Vector2 center = new Vector2((rectangle.x + rectangle.width * 0.5f) * tileSize, (rectangle.y + rectangle.height * 0.5f) * tileSize);
		polygon.setAsBox(rectangle.width * 0.5f * tileSize, rectangle.height * 0.5f * tileSize, center, 0.0f);
		return polygon;
	}

	private CircleShape getCircle(CircleMapObject circleObject) {
		Circle circle = circleObject.getCircle();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(circle.radius * tileSize);
		circleShape.setPosition(new Vector2(circle.x * tileSize, circle.y * tileSize));
		return circleShape;
	}

	private PolygonShape getPolygon(PolygonMapObject polygonObject) {
		PolygonShape polygon = new PolygonShape();
		float[] vertices = polygonObject.getPolygon().getTransformedVertices();

		float[] worldVertices = new float[vertices.length];

		for (int i = 0; i < vertices.length; ++i) {
			worldVertices[i] = vertices[i] * tileSize;
		}

		polygon.set(worldVertices);
		return polygon;
	}

	private ChainShape getPolyline(PolylineMapObject polylineObject) {
		float[] vertices = polylineObject.getPolyline().getTransformedVertices();
		Vector2[] worldVertices = new Vector2[vertices.length / 2];

		for (int i = 0; i < vertices.length / 2; ++i) {
			worldVertices[i] = new Vector2();
			worldVertices[i].x = vertices[i * 2] * tileSize;
			worldVertices[i].y = vertices[i * 2 + 1] * tileSize;
		}

		ChainShape chain = new ChainShape();
		chain.createChain(worldVertices);
		return chain;
	}
}
