package bankcity.gamelogic.buildings.components.interfaces;

import com.badlogic.gdx.scenes.scene2d.Actor;

import bankcity.gamelogic.buildings.Building;

public interface InspectableComponent extends BasicComponent{
	
	public Actor getInspectionUI(Building building);
	
}
