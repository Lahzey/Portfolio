package bankcity.gamelogic.buildings.components;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bankcity.BankCity;
import bankcity.gamelogic.Education;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.interfaces.InspectableComponent;
import bankcity.ui.InternalBrowser.InspectableList;
import bankcity.ui.InternalBrowser.Link;

public class EducationProviderComponent implements InspectableComponent{
	
	public InspectableList<Education> educations = new InspectableList<>("");

	@Override
	public void reset() {
		educations.clear();
	}
	
	public int getTotalEducationCapacity(){
		int capacity = 0;
		for(Education education : educations){
			capacity += education.capacity;
		}
		return capacity;
	}
	
	public int getTotalOccupiedEducationCount(){
		int occupied = 0;
		for(Education education : educations){
			occupied += education.occupiedBy.size();
		}
		return occupied;
	}

	@Override
	public Table getInspectionUI(Building building) {
		educations.title = "Educations at " + building.getName();
		
		Table table = new Table(BankCity.SKIN);
		table.add("Educates: ").left();
		table.add(new Link(educations, getTotalOccupiedEducationCount() + "/" + getTotalEducationCapacity()));
		return table;
	}

}
