package bankcity.gamelogic.buildings;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Family;

public abstract class BuildingEffect {

	protected Building origin;
	protected final List<Building> appliedTo = new ArrayList<>();
	protected Family targets;
	
	public BuildingEffect(Building origin, Family targets){
		this.origin = origin;
		this.targets = targets;
	}
	
	public void tryApply(Building building){
		if(isTarget(building) && !appliedTo.contains(building)){
			appliedTo.add(building);
			apply(building);
		}
	}
	public void tryRemove(Building building){
		if(appliedTo.contains(building)){
			appliedTo.remove(building);
			remove(building);
		}
	}
	
	public Building getOrigin(){
		return origin;
	}
	
	protected boolean isTarget(Building building){
		return targets.matches(building.getEntity());
	}
	
	protected abstract void apply(Building building);
	protected abstract void remove(Building building);
}
