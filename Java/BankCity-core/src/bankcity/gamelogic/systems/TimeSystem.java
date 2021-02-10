package bankcity.gamelogic.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import bankcity.gamelogic.TimedAction;
import bankcity.gamelogic.components.TimeComponent;
import bankcity.util.GameDate;

public class TimeSystem extends IteratingSystem{
	
	/** How long a day is (in seconds) */
	public static final float DAY_DURATION = 0.1f;
	
	public final GameDate date;
	private float storedDelta = 0;
	
	private boolean timeChanged = false;
	private ComponentMapper<TimeComponent> timeMapper = ComponentMapper.getFor(TimeComponent.class);

	public TimeSystem(GameDate date) {
		super(Family.all(TimeComponent.class).get());
		this.date = date;
	}

	@Override
	public void update(float deltaTime) {
		storedDelta += deltaTime;
		while(storedDelta > DAY_DURATION){
			date.days++;
			storedDelta -= DAY_DURATION;
			timeChanged = true;
		}
		super.update(deltaTime);
		timeChanged = false;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(timeChanged){
			TimeComponent timeComponent = timeMapper.get(entity);
			for(int i = 0; i < timeComponent.actions.size(); i++){
				TimedAction action = timeComponent.actions.get(i);
				if(action.time <= date.days){
					if(action.perform(date.days)){
						timeComponent.actions.remove(i);
						i--;
					}
				}
			}
		}
	}

	
}
