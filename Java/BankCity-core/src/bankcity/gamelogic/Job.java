package bankcity.gamelogic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bankcity.gamelogic.buildings.Building;
import bankcity.ui.Inspectable;
import bankcity.ui.Inspector;
import bankcity.ui.InternalBrowser.InspectableList;
import bankcity.ui.InternalBrowser.Link;
import bankcity.util.ImageLabel;
import bankcity.util.ImageLabel.ImagePosition;

public class Job implements Inspectable{
	
	public static final int EDUCATION_PRIORITY = 0;
	public static final int NORMAL_PRIORITY = 1;
	public static final int MAIN_BUILDING_PRIORITY = 2;
	
	
	public Building workplace;
	public EducationLevel educationLevel;
	
	public int capacity;
	public final InspectableList<Habitant> occupiedBy = new InspectableList<>("");
	
	public boolean onlyForSameBuilding = false;
	
	public int priority = NORMAL_PRIORITY;
	
	public Job(Building workplace, EducationLevel educationLevel, int capacity){
		this.workplace = workplace;
		this.educationLevel = educationLevel;
		this.capacity = capacity;
	}
	
	public void fireAll(){
		while(!occupiedBy.isEmpty()){
			Habitant occupier = occupiedBy.get(0);
			if(occupier.job == this) occupier.job = null;
			occupiedBy.remove(occupier);
		}
	}
	
	public void reset(){
		workplace = null;
		educationLevel = null;
		capacity = 0;
		onlyForSameBuilding = false;
		fireAll();
	}
	
	public int getFreeSpace(){
		return capacity - occupiedBy.size();
	}

	@Override
	public void createInspectionUI(Inspector inspector) {
		Table table = inspector.getInspectionTable();
		occupiedBy.title = "People working at a Job at " + workplace.getName();
		
		table.add("Job at: ").left();
		Link workplaceLink = new Link(workplace, workplace.getName());
		workplaceLink.addListener(workplace.getTextureTooltip());
		table.add(workplaceLink).left();
		
		table.row();
		

		table.add("Currently employes: ").left();
		table.add(new Link(occupiedBy, occupiedBy.size() + "/" + (capacity == Integer.MAX_VALUE ? "\u221E" : capacity) + " People")).left();
		
		table.row();

		table.add("Required Education: ").left();
		ImageLabel educationLevelLabel = new ImageLabel(educationLevel.toString(), educationLevel.icon, ImagePosition.AFTER);
		educationLevelLabel.getImage().addListener(educationLevel.getTooltip());
		table.add(educationLevelLabel).left();
	}

	@Override
	public String getTitle() {
		return "Job at " + workplace.getName();
	}

	@Override
	public Color getColor() {
		return Color.WHITE;
	}
	
	public Job cpy(){
		Job copy = new Job(workplace, educationLevel, capacity);
		copy.onlyForSameBuilding = onlyForSameBuilding;
		copy.priority = priority;
		return copy;
	}
}
