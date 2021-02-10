package bankcity.gamelogic.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import bankcity.gamelogic.components.UpdateableComponent;

public class UpdateSystem extends IteratingSystem{
	
	
	private ComponentMapper<UpdateableComponent> um = ComponentMapper.getFor(UpdateableComponent.class);

	public UpdateSystem() {
		super(Family.all(UpdateableComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		UpdateableComponent updateComponent = um.get(entity);
		updateComponent.storedDelta += deltaTime;
		if(updateComponent.storedDelta >= updateComponent.interval){
			updateComponent.updateable.update(updateComponent.storedDelta);
			updateComponent.storedDelta = 0;
		}
	}

}
