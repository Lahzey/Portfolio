package bankcity.gamelogic.functionality;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Engine;

import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.ResourceComponent;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class ResourceFunctionality implements Functionality {
	
	public Map<Resource, Float> resourceNetYield;
	
	public ResourceFunctionality(float energyNetYield, float waterNetYield){
		this(new HashMap<>());
		resourceNetYield.put(Resource.ELECTRICITY, energyNetYield);
		resourceNetYield.put(Resource.WATER, waterNetYield);
	}
	
	public ResourceFunctionality(float energyNetYield, float waterNetYield, Resource resource, float netYield){
		this(energyNetYield, waterNetYield);
		addNetYield(resource, netYield);
	}
	
	public ResourceFunctionality(Map<Resource, Float> resourceNetYield){
		this.resourceNetYield = resourceNetYield;
	}
	
	public void addNetYield(Resource resource, float netYield){
		Float currentValue = resourceNetYield.get(resource);
		if(currentValue == null) currentValue = 0f;
		resourceNetYield.put(resource, currentValue + netYield);
	}

	@Override
	public void addToBuilding(Engine engine, Building building) {
		ResourceComponent resourceComp = building.getEntity().getComponent(ResourceComponent.class);
		if(resourceComp == null){
			resourceComp = engine.createComponent(ResourceComponent.class);
			building.getEntity().add(resourceComp);
		}
		for(Resource res : resourceNetYield.keySet()){
			float netYield = resourceNetYield.get(res);
			resourceComp.putResourceNetYield(res, netYield);
		}
	}

}
