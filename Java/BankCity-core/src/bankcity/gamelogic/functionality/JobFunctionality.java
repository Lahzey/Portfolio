package bankcity.gamelogic.functionality;

import com.badlogic.ashley.core.Engine;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Job;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.JobProviderComponent;

public class JobFunctionality implements Functionality{
	
	public Job[] jobs;
	
	public boolean affectEfficiency = true;
	
	public JobFunctionality(EducationLevel educationLevel, int capacity){
		this(new Job(null, educationLevel, capacity));
	}
	
	public JobFunctionality(Job... jobs){
		this.jobs = jobs;
	}
	
	private Job duplicateJob(Job job, Building workplace){
		Job duplicate = job.cpy();
		duplicate.workplace = workplace;
		return duplicate;
	}

	@Override
	public void addToBuilding(Engine engine, Building building) {
		JobProviderComponent jobComp = building.getEntity().getComponent(JobProviderComponent.class);
		if(jobComp == null){
			jobComp = engine.createComponent(JobProviderComponent.class);
			building.getEntity().add(jobComp);
		}
		jobComp.affectEfficiency = affectEfficiency;
		for(Job job : jobs){
			jobComp.jobs.add(duplicateJob(job, building));
		}
	}

}
