package graphics.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Created by barry on 12/8/15 @ 9:53 PM.
 */
public class TransformComponent implements Component, Poolable {
    public final Vector2 position = new Vector2();
    public float z = 0;
    public final Vector2 scale = new Vector2(1.0f, 1.0f);
    public float rotation = 0.0f;
    
    
	@Override
	public void reset() {
		position.set(0, 0);
		z = 0;
		scale.set(1, 1);
		rotation = 0;
	}
}
