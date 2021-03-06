package poopgame.physics.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import poopgame.gamelogic.components.LogicComponent;
import poopgame.gamelogic.engine.TimeEngine;

public class BodyComponent extends LogicComponent {

    public Body body;
	
	public BodyComponent() {}

	@Override
	public float[] storeState(Engine engine, World world) {
		Vector2 position = body.getPosition();
		Vector2 linearVelocity = body.getLinearVelocity();
		float angle = body.getAngle();
		float angularVelocity = body.getAngularVelocity();
		
		return new float[] {position.x, position.y, linearVelocity.x, linearVelocity.y, angle, angularVelocity};
	}

	@Override
	public void loadState(Object state, Engine engine, World world) {
		if (body != null) {
			float[] values = (float[]) state;
			body.setTransform(new Vector2(values[0], values[1]), values[4]);
			body.setLinearVelocity(values[2], values[3]);
			body.setAngularVelocity(values[5]);
		}
	}
	
	@Override
	public void onDestroy(TimeEngine engine, World world) {
		if (body != null) {
			synchronized (engine.updateLock) {
				world.destroyBody(body);
			}
		}
	}
}
