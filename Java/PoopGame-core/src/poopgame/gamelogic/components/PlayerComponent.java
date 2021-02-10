package poopgame.gamelogic.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.World;

import poopgame.gamelogic.Champion;

public class PlayerComponent extends LogicComponent {

	public long id;
	public String name;
	public Champion champ;

	public PlayerComponent() { }

	public PlayerComponent(long id, String name, Champion champ) {
		super();
		this.id = id;
		this.name = name;
		this.champ = champ;
	}

	@Override
	public Object storeState(Engine engine, World world) {
		return new Object[] { id, name, champ };
	}

	@Override
	public void loadState(Object state, Engine engine, World world) {
		Object[] values = (Object[]) state;
		id = (long) values[0];
		name = (String) values[1];
		champ = (Champion) values[2];
	}

}
