package physics.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Created by barry on 12/8/15 @ 10:29 PM.
 */
public class BodyComponent implements Component, Poolable {

    public Body body;

	@Override
	public void reset() {
		body = null;
	}
}
