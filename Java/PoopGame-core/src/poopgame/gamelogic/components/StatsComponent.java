package poopgame.gamelogic.components;

import java.util.HashMap;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.World;

import poopgame.gamelogic.Stats;

public class StatsComponent extends LogicComponent {

	public final Stats stats = new Stats();
	
	public StatsComponent() {}

	@Override
	public Object storeState(Engine engine, World world) {
		return new HashMap<>(stats);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadState(Object state, Engine engine, World world) {
		stats.clear();
		stats.putAll((HashMap<String, Object>) state);
	}

}
