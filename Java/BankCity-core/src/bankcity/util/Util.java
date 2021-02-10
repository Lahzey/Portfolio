package bankcity.util;

import java.awt.Point;

import com.badlogic.gdx.math.Vector2;

public class Util {

	
	public static Point vector2ToPoint(Vector2 vector2){
		return new Point((int)vector2.x, (int)vector2.y);
	}
	
	public static Vector2 pointToVector2(Point point){
		return new Vector2(point.x, point.y);
	}
}
