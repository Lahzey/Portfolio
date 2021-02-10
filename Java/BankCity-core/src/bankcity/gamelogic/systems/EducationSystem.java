package bankcity.gamelogic.systems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import bankcity.gamelogic.Education;
import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.Habitant;
import bankcity.gamelogic.buildings.components.EducationProviderComponent;
import bankcity.gamelogic.buildings.components.HomeProviderComponent;
import bankcity.util.GameDate;

public class EducationSystem extends IntervalIteratingSystem{
	
	private static final float INTERVAL_TIME = 1f;
	
	private Map<EducationLevel, Integer> educationDurations = new HashMap<>();
	private Map<EducationLevel, Integer> educationMinAges = new HashMap<>();

	private Map<EducationLevel, List<Habitant>> lookingForSchool = new HashMap<>();
	private Map<EducationLevel, List<Education>> freeEducationSpaces = new HashMap<>();
	private Map<EducationLevel, List<Habitant>> adultsLookingForSchool = new HashMap<>();
	private Map<EducationLevel, List<Habitant>> adultOccupants = new HashMap<>();
	
	private Map<Habitant, Long> educationStarts = new HashMap<>();
	
	private Map<EducationLevel, Integer> freeEducation = new HashMap<>();
	
	private ComponentMapper<HomeProviderComponent> hm = ComponentMapper.getFor(HomeProviderComponent.class);
	private ComponentMapper<EducationProviderComponent> em = ComponentMapper.getFor(EducationProviderComponent.class);
	
	private Game game;
	public boolean educationOverJob = false;

	public EducationSystem(Game game) {
		super(Family.one(HomeProviderComponent.class, EducationProviderComponent.class).get(), INTERVAL_TIME);
		this.game = game;
		
		educationDurations.put(EducationLevel.LOW, 3 * GameDate.DAYS_PER_YEAR);
		educationDurations.put(EducationLevel.MEDIUM, 3 * GameDate.DAYS_PER_YEAR);
		educationDurations.put(EducationLevel.HIGH, 4 * GameDate.DAYS_PER_YEAR);
		
		educationMinAges.put(EducationLevel.LOW, 6);
		educationMinAges.put(EducationLevel.MEDIUM, 10);
		educationMinAges.put(EducationLevel.HIGH, 14);
		
		for(EducationLevel level : EducationLevel.values()){
			lookingForSchool.put(level, new ArrayList<>());
			freeEducationSpaces.put(level, new ArrayList<>());
			adultsLookingForSchool.put(level, new ArrayList<>());
			adultOccupants.put(level, new ArrayList<>());
		}
	}
	
	public void initHabitantEducation(Habitant habitant){
		for(EducationLevel level = EducationLevel.HIGH; level != null; level = level.lower()){
			int free = freeEducation.getOrDefault(level, 0);
			if(free > 0){
				habitant.educationLevel = level;
				free--;
				freeEducation.put(level, free);
				break;
			}
		}
	}
	
	public void addFreeEducation(EducationLevel level, int count){
		freeEducation.put(level, Math.max(freeEducation.getOrDefault(level, 0) + count, 0));
	}
	
	private void startEducation(Education education, Habitant habitant){
		education.occupiedBy.add(habitant);
		habitant.education = education;
		if(habitant.job != null){
			habitant.job.occupiedBy.remove(habitant);
			habitant.job = null;
		}
		educationStarts.put(habitant, game.timeSystem.date.days);
	}
	
	private void endEducation(Habitant habitant){
		if(educationStarts.get(habitant) + educationDurations.get(habitant.education.level) <= game.timeSystem.date.days){
			habitant.educationLevel = habitant.education.level;
		}
		habitant.education.occupiedBy.remove(habitant);
		habitant.education = null;
	}

	@Override
	public void updateInterval(){
		super.updateInterval();
		
		for(EducationLevel level : EducationLevel.values()){
			List<Habitant> searchingHabitants = lookingForSchool.get(level);
			List<Habitant> searchingAdults = adultsLookingForSchool.get(level);
			List<Education> freeSpaces = freeEducationSpaces.get(level);
			while(!freeSpaces.isEmpty() && !searchingHabitants.isEmpty()){
				Education education = freeSpaces.get(0);
				Habitant habitant = searchingHabitants.get(0);
				if(education.capacity > education.occupiedBy.size()){
					startEducation(education, habitant);
					searchingHabitants.remove(habitant);
				}else{
					freeSpaces.remove(education);
				}
				if(freeSpaces.isEmpty() && !searchingHabitants.isEmpty()){
					List<Habitant> adultsToRemove = adultOccupants.get(level);
					if(!adultsToRemove.isEmpty()){
						adultsToRemove.sort(new Comparator<Habitant>() {

							@Override
							public int compare(Habitant o1, Habitant o2) {
								return (int) (educationStarts.get(o2) - educationStarts.get(o1));
							}
						});
						Habitant removed = adultsToRemove.get(0);
						endEducation(removed);
						adultsToRemove.remove(0);
					}
				}
			}
			while(!freeSpaces.isEmpty() && !searchingAdults.isEmpty()){
				Education education = freeSpaces.get(0);
				Habitant habitant = searchingAdults.get(0);
				if(education.capacity > education.occupiedBy.size()){
					education.occupiedBy.add(habitant);
					habitant.education = education;
					startEducation(education, habitant);
					adultOccupants.get(level).add(habitant);
					searchingAdults.remove(habitant);
				}else{
					freeSpaces.remove(education);
				}
			}
			searchingHabitants.clear();
			searchingAdults.clear();
			freeSpaces.clear();
		}
	}

	@Override
	protected void processEntity(Entity entity) {
		HomeProviderComponent homeComp = hm.get(entity);
		EducationProviderComponent educationComp = em.get(entity);
		
		if(homeComp != null){
			for(Habitant habitant : homeComp.habitants){
				int age = habitant.getAge();
				Education education = habitant.education;
				if(education != null && educationStarts.get(habitant) + educationDurations.get(education.level) <= game.timeSystem.date.days){
					endEducation(habitant);
				}
				
				EducationLevel nextLevel = habitant.educationLevel.higher();
				if(education == null && nextLevel != null && age >= educationMinAges.get(nextLevel) && (educationOverJob || habitant.job == null)){
					if(age >= HabitantSystem.AGE_OF_MAJORITY) adultsLookingForSchool.get(nextLevel).add(habitant);
					else lookingForSchool.get(nextLevel).add(habitant);
				}
			}
		}
		
		if(educationComp != null){
			for(Education education : educationComp.educations){
				if(education.capacity > education.occupiedBy.size()){
					freeEducationSpaces.get(education.level).add(education);
				}
			}
		}
	}

}
