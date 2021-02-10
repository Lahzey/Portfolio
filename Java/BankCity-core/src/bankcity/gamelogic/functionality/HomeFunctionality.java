package bankcity.gamelogic.functionality;

import com.badlogic.ashley.core.Engine;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.HomeProviderComponent;

public class HomeFunctionality implements Functionality{
	
	private int capacity;
	public EducationLevel minEducationLevel = EducationLevel.UNEDUCATED;
	
	public HomeFunctionality(int capacity){
		this.capacity = capacity;
	}

	@Override
	public void addToBuilding(Engine engine, Building building) {
		HomeProviderComponent homeComp = building.getEntity().getComponent(HomeProviderComponent.class);
		if(homeComp == null){
			homeComp = engine.createComponent(HomeProviderComponent.class);
			building.getEntity().add(homeComp);
		}
		homeComp.capacity = capacity;
		homeComp.minEducationLevel = minEducationLevel;
	}

}
