package bankcity.gamelogic.buildings.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pool.Poolable;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Habitant;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.Stat.StatList;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.interfaces.InspectableComponent;
import bankcity.gamelogic.buildings.components.interfaces.StatComponent;
import bankcity.gamelogic.systems.HabitantSystem;
import bankcity.ui.InternalBrowser.InspectableList;
import bankcity.ui.InternalBrowser.Link;

public class HomeProviderComponent implements InspectableComponent, StatComponent, Poolable{
	
	public final InspectableList<Habitant> habitants = new InspectableList<Habitant>("");
	public int capacity;
	public EducationLevel minEducationLevel = EducationLevel.UNEDUCATED;

	public final Map<Entity, Float> leisureOpportunities = new HashMap<>();
	public final Map<Entity, Float> shoppingOpportunities = new HashMap<>();
	
	@Override
	public void reset() {
		habitants.clear();
		capacity = 0;
		minEducationLevel = EducationLevel.UNEDUCATED;
		leisureOpportunities.clear();
		shoppingOpportunities.clear();
	}

	@Override
	public Table getInspectionUI(Building building) {
		Table table = new Table();
		
		String linkText = "Houses " + habitants.size() + "/" + capacity + " People";
		habitants.title = "Habitants of " + building.getName();
		Link link = new Link(habitants, linkText);
		table.add(link);
		
		return table;
	}

	@Override
	public void update(StatList stats, float deltaTime) {
		float efficiency = 0;
		if(capacity > 0 && habitants.size() > 0){
			for(Habitant habitant : habitants){
				efficiency += HabitantSystem.getEfficiencyFor(habitant);
			}
			efficiency /= capacity;
		}
		stats.get(Stat.EFFICIENCY).putMult(this, efficiency);
	}

}
