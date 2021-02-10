package bankcity.gamelogic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bankcity.gamelogic.buildings.Building;
import bankcity.ui.Inspectable;
import bankcity.ui.Inspector;
import bankcity.ui.InternalBrowser.InspectableList;
import bankcity.ui.InternalBrowser.Link;

public class Education implements Inspectable{
	public Building school;
	public EducationLevel level;
	
	public int capacity;
	public final InspectableList<Habitant> occupiedBy = new InspectableList<>("");
	
	public Education(Building school, EducationLevel level, int capacity){
		this.school = school;
		this.level = level;
		this.capacity = capacity;
		
		occupiedBy.title = "Education to " + level;
	}

	@Override
	public void createInspectionUI(Inspector inspector) {
		Table table = inspector.getInspectionTable();
		
		table.add("Level: ").left();
		table.add(level.toString()).left();
		
		table.row();
		
		table.add("Capacity: ").left();
		table.add(new Link(occupiedBy, occupiedBy.size() + "/" + capacity));
		
		table.row();
		
		table.add("School: ");
		table.add(new Link(school, school.getName()));
	}

	@Override
	public String getTitle() {
		return "Education at " + school.getName();
	}

	@Override
	public Color getColor() {
		return Color.WHITE;
	}
}
