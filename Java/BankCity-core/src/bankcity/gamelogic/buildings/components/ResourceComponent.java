package bankcity.gamelogic.buildings.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bankcity.gamelogic.Stat;
import bankcity.gamelogic.Stat.StatList;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.interfaces.InspectableComponent;
import bankcity.gamelogic.buildings.components.interfaces.StatComponent;
import bankcity.gamelogic.systems.ResourceSystem.Resource;
import bankcity.util.Calculation;
import bankcity.util.DynamicLabel;
import bankcity.util.DynamicLabel.DynamicString;

public class ResourceComponent implements StatComponent, InspectableComponent{
	
	public final Map<Resource, Calculation> resourceProduction = new HashMap<>();
	public final Map<Resource, Calculation> resourceConsumption = new HashMap<>();
	
	/**
	 * Availability of the resources.
	 * <br/>Set by the ResourceSystem.
	 */
	public float efficiencyMult = 1;
	/**
	 * How well this works (excluding the resource availability).
	 * <br/>Set in the update method of this class.
	 */
	public float netYieldMult = 1;
	
	public boolean thisInfluencesEfficiency = true;
	public boolean efficiencyInfluencesThis = true;
	
	public void putResourceNetYield(Resource resource, float netYield){
		if(netYield >= 0){
			resourceProduction.put(resource, new Calculation(netYield));
		}else{
			resourceConsumption.put(resource, new Calculation(-netYield));
		}
	}

	@Override
	public Actor getInspectionUI(Building building) {
		Table table = new Table();
		boolean first = true;
		for(Resource res : resourceProduction.keySet()){
			if(!first) table.row();
			table.add(new DynamicLabel(new DynamicString(){

				@Override
				public String toString() {
					return res.getDisplayText(resourceProduction.get(res).getResult());
				}
				
			}));
			first = false;
		}
		return table;
	}

	@Override
	public void update(StatList stats, float deltaTime) {
		float efficiencyMult = this.efficiencyMult;
		Calculation efficiency = stats.get(Stat.EFFICIENCY);
		
		if(thisInfluencesEfficiency){
			efficiency.putMult(this, efficiencyMult);
		}else{
			efficiency.removeMult(this);
			efficiencyMult = 1;
		}
		
		if(efficiencyInfluencesThis){
			netYieldMult = efficiency.getResult() / efficiencyMult;
		}else{
			netYieldMult = 1;
		}
	}

	@Override
	public void reset() {
		resourceProduction.clear();
		resourceConsumption.clear();
		efficiencyMult = 1;
		netYieldMult = 1;
		thisInfluencesEfficiency = true;
		efficiencyInfluencesThis = true;
	}

}
