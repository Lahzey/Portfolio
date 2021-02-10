package bankcity.gamelogic.buildings.components;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pool.Poolable;

import bankcity.BankCity;
import bankcity.gamelogic.Job;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.Stat.StatList;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.interfaces.InspectableComponent;
import bankcity.gamelogic.buildings.components.interfaces.StatComponent;
import bankcity.ui.InternalBrowser.InspectableList;
import bankcity.ui.InternalBrowser.Link;

public class JobProviderComponent implements InspectableComponent, StatComponent, Poolable{
	
	public final InspectableList<Job> jobs = new InspectableList<>("");
	
	public boolean affectEfficiency = true;

	@Override
	public void reset() {
		jobs.clear();
	}
	
	public int getTotalJobCapacity(){
		int capacity = 0;
		for(Job job : jobs){
			capacity += job.capacity;
		}
		return capacity;
	}
	
	public int getTotalOccupiedJobCount(){
		int occupied = 0;
		for(Job job : jobs){
			occupied += job.occupiedBy.size();
		}
		return occupied;
	}

	@Override
	public Table getInspectionUI(Building building) {
		Table table = new Table(BankCity.SKIN);
		
		int capacity = getTotalJobCapacity();
		jobs.title = "Jobs of " + building.getName();
		table.add("Provides ").left();
		Link link = new Link(jobs, getTotalOccupiedJobCount() + "/" + (capacity == Integer.MAX_VALUE ? "\u221E" : capacity) + " Jobs");
		table.add(link).left();
		
		return table;
	}

	@Override
	public void update(StatList stats, float deltaTime) {
		if(affectEfficiency){
			float efficiency = ((float) getTotalOccupiedJobCount()) / getTotalJobCapacity();
			stats.get(Stat.EFFICIENCY).putMult(this, efficiency);
		}
	}

}
