package bankcity.gamelogic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bankcity.BankCity;
import bankcity.data.Text;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.HomeProviderComponent;
import bankcity.ui.Inspectable;
import bankcity.ui.Inspector;
import bankcity.ui.InternalBrowser.Link;
import bankcity.util.GameDate;
import bankcity.util.ImageLabel;
import bankcity.util.ImageLabel.ImagePosition;

public class Habitant implements Inspectable{
	
	public String prename = "Max";
	public String surname = "Mustermann";
	public GameDate bornAt;

	public Building home;
	public EducationLevel educationLevel = EducationLevel.UNEDUCATED;
	public Education education;
	public Job job;
	public Occupation occupation = Occupation.NONE;
	public float efficiency = 1;
	
	public Habitant(int age, GameDate currentDate){
		this(currentDate.cpy().sub(age * GameDate.DAYS_PER_YEAR));
	}
	
	public Habitant(GameDate bornAt){
		this.bornAt = bornAt;
	}
	
	public void reset(){
		if(home != null){
			HomeProviderComponent homeComp = home.getEntity().getComponent(HomeProviderComponent.class);
			if(homeComp != null){
				homeComp.habitants.remove(this);
			}
			home = null;
		}
		if(education != null){
			education.occupiedBy.remove(this);
		}
		educationLevel = EducationLevel.UNEDUCATED;
		if(job != null && job.occupiedBy.contains(this)){
			job.occupiedBy.remove(this);
		}
		occupation = Occupation.NONE;
		job = null;
		bornAt = null;
	}
	
	public int getAge(){
		long days = BankCity.getDate().days - bornAt.days;
		return (int) (days / GameDate.DAYS_PER_YEAR);
	}
	
	public static enum Occupation{
		NONE(Text.UNOCCUPIED), SCHOOL(Text.SCHOOL), WORK(Text.WORK), RETIRED(Text.RETIRED);
		
		private Text text;
		
		private Occupation(Text text){
			this.text = text;
		}
		
		public String toString(){
			return text.get();
		}
	}

	@Override
	public void createInspectionUI(Inspector inspector) {
		Table table = inspector.getInspectionTable();
		
		table.add("Name: ").left();
		table.add(prename + " " + surname).left();
		
		table.row();
		
		table.add("Age: ").left();
		table.add(getAge() + "").left();
		
		table.row();
		
		table.add("Lives in: ").left();
		table.add(new Link(home, home.getName())).left();
		
		inspector.hr();
		
		table.add("Education Level: ").left();
		ImageLabel educationLevelLabel = new ImageLabel(educationLevel.toString(), educationLevel.icon, ImagePosition.AFTER);
		educationLevelLabel.getImage().addListener(educationLevel.getTooltip());
		table.add(educationLevelLabel).left();
		
		if(education != null){
			table.row();
			table.add("Currently being educated at: ").left();
			table.add(new Link(education, education.school.getName())).left();
		}
		
		if(job != null){
			table.row();
			table.add("Currently working at: ").left();
			table.add(new Link(job, job.workplace.getName())).left();
		}
	}

	@Override
	public String getTitle() {
		return "Habitant: " + prename + " " + surname + " [" + getAge() + "]";
	}

	@Override
	public Color getColor() {
		return Color.WHITE;
	}
	
}
