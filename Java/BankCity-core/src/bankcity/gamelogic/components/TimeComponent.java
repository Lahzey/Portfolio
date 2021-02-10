package bankcity.gamelogic.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

import bankcity.gamelogic.TimedAction;

public class TimeComponent implements Component, Poolable{

	public final List<TimedAction> actions = new ArrayList<>();

	@Override
	public void reset() {
		actions.clear();
	}
	
}
