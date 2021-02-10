package poopgame.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.World;

import poopgame.gamelogic.engine.TimeEngine;

public abstract class LogicComponent implements Component {

	public abstract Object storeState(Engine engine, World world);
	
	public abstract void loadState(Object state, Engine engine, World world);
	
	public void onDestroy(TimeEngine engine, World world){
		
	}
	
}
