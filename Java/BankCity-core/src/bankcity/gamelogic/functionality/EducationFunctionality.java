package bankcity.gamelogic.functionality;

import com.badlogic.ashley.core.Engine;

import bankcity.gamelogic.Education;
import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.EducationProviderComponent;

public class EducationFunctionality implements Functionality{
	
	public EducationLevel level;
	public int capacity;
	
	public EducationFunctionality(EducationLevel level, int capacity){
		this.level = level;
		this.capacity = capacity;
	}

	@Override
	public void addToBuilding(Engine engine, Building building) {
		EducationProviderComponent educationComp = building.getEntity().getComponent(EducationProviderComponent.class);
		if(educationComp == null){
			educationComp = engine.createComponent(EducationProviderComponent.class);
			building.getEntity().add(educationComp);
		}
		educationComp.educations.add(new Education(building, level, capacity));
	}

}
