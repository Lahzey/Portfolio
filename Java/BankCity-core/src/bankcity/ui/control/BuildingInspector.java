package bankcity.ui.control;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input.Buttons;

import bankcity.gamelogic.buildings.components.BuildingComponent;
import bankcity.gamelogic.systems.InputSystem.EntityInputAdapter;
import bankcity.ui.stages.InGameStage;

public class BuildingInspector extends EntityInputAdapter {
	
	private InGameStage inGameStage;
	
	public BuildingInspector(InGameStage inGameStage) {
		this.inGameStage = inGameStage;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.LEFT) {
			inGameStage.inspectionInterface.inspect(null);
		}
		return false;
	}

	@Override
	public boolean entityClicked(Entity entity, int screenX, int screenY, int pointer, int button) {
		if(button == Buttons.LEFT && entity.getComponent(BuildingComponent.class) != null){
			inGameStage.inspectionInterface.inspect(entity.getComponent(BuildingComponent.class).building);
			return true;
		}
		return false;
	}
	
	
}
