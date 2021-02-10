package bankcity.gamelogic.buildings.components.interfaces;

import bankcity.gamelogic.Stat.StatList;

public interface StatComponent extends BasicComponent {
	
	public void update(StatList stats, float deltaTime);
	
}
