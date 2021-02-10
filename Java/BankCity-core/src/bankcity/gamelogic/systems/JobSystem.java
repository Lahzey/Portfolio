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

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Habitant;
import bankcity.gamelogic.Job;
import bankcity.gamelogic.buildings.components.HomeProviderComponent;
import bankcity.gamelogic.buildings.components.JobProviderComponent;

public class JobSystem extends IntervalIteratingSystem{
	
	private static final float INTERVAL_TIME = 1f;
	
	private Map<EducationLevel, List<Habitant>> habitantQueue = new HashMap<>();
	private Map<EducationLevel, List<Job>> jobQueue = new HashMap<>();
	
	private JobComparator jobComparator = new JobComparator();
	
	private ComponentMapper<HomeProviderComponent> hm = ComponentMapper.getFor(HomeProviderComponent.class);
	private ComponentMapper<JobProviderComponent> jm = ComponentMapper.getFor(JobProviderComponent.class);

	public JobSystem() {
		super(Family.one(HomeProviderComponent.class, JobProviderComponent.class).get(), INTERVAL_TIME);
		for(EducationLevel level : EducationLevel.values()){
			habitantQueue.put(level, new ArrayList<>());
			jobQueue.put(level, new ArrayList<>());
		}
	}

	@Override
	public void updateInterval(){
		super.updateInterval();
		
		for(EducationLevel level : EducationLevel.valuesDescending()){
			List<Habitant> habitants = habitantQueue.get(level);
			List<Job> jobs = jobQueue.get(level);
			
			jobs.sort(jobComparator);
			
			jobLoop:
			for(Job job : jobs){
				if(job.onlyForSameBuilding && job.occupiedBy.size() < job.capacity){
					HomeProviderComponent homeComp = job.workplace.getEntity().getComponent(HomeProviderComponent.class);
					if(homeComp != null){
						for(Habitant habitant : homeComp.habitants){
							if(job.getFreeSpace() < 1) break;
							else if(habitant.educationLevel.value >= job.educationLevel.value){
								if(habitant.job == null){
									setJob(habitant, job);
								}
								if(!(habitant.job.workplace == job.workplace && habitant.job.onlyForSameBuilding)){ //not already employed at this or similar jobs
									setJob(habitant, job);
								}
							}
						}
					}
				}else if(!job.onlyForSameBuilding){
					while(job.getFreeSpace() > 0){
						if(!habitants.isEmpty()){
							Habitant habitant = habitants.get(0);
							if(habitant.job != null){
								if(habitant.job.educationLevel.value < job.educationLevel.value){
									setJob(habitant, null);
								}else habitants.remove(habitant);
							}
							if(habitant.job == null){
								setJob(habitant, job);
								habitants.remove(habitant);
							}
						}else break jobLoop;
					}
				}
			}
			
			EducationLevel lower = level.lower();
			if(lower != null){
				List<Habitant> lowerQueue = habitantQueue.get(lower);
				for(Habitant leftOver : habitants){
					lowerQueue.add(leftOver);
				}
			}
			
			
			habitants.clear();
			jobs.clear();
		}
	}
	
	private boolean setJob(Habitant habitant, Job job){
		if(job != habitant.job){
			if(habitant.job != null){
				habitant.job.occupiedBy.remove(habitant);
				habitant.job = null;
			}
			if(job != null){
				if(job.getFreeSpace() < 1) return false;
				habitant.job = job;
				job.occupiedBy.add(habitant);
			}
		}
		return true;
	}
	
	@Override
	protected void processEntity(Entity entity) {
		HomeProviderComponent homeComponent = hm.get(entity);
		JobProviderComponent jobComponent = jm.get(entity);
		
		if(homeComponent != null){
			for(Habitant habitant : homeComponent.habitants){
				int age = habitant.getAge();
				if(age >= HabitantSystem.AGE_OF_MAJORITY && habitant.education == null && (habitant.job == null || habitant.job.educationLevel != habitant.educationLevel)){
					if(age >= HabitantSystem.AGE_OF_RETIREMENT && habitant.job != null){
						habitant.job.occupiedBy.remove(habitant);
						habitant.job = null;
					}else{
						habitantQueue.get(habitant.educationLevel).add(habitant);
					}
				}
			}
		}
		
		if(jobComponent != null){
			for(Job job : jobComponent.jobs){
				if(job.occupiedBy.size() < job.capacity){
					jobQueue.get(job.educationLevel).add(job);
				}
			}
		}
		
	}
	
	
	private static class JobComparator implements Comparator<Job>{

		@Override
		public int compare(Job o1, Job o2) {
			return o1.priority - o2.priority;
		}
		
	}

}
