package bankcity.gamelogic.buildings.categories;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import bankcity.gamelogic.EducationLevel;
import bankcity.gamelogic.Game;
import bankcity.gamelogic.Job;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingCategory;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.gamelogic.buildings.types.EducationCenter;
import bankcity.gamelogic.buildings.types.ElementarySchool;
import bankcity.gamelogic.buildings.types.MidSchool;
import bankcity.gamelogic.buildings.types.University;
import bankcity.gamelogic.functionality.EducationFunctionality;
import bankcity.gamelogic.functionality.JobFunctionality;

public class School implements BuildingCategory{


	@Override
	public List<Building> getBuildings(Game game) {
		List<Building> buildings = new ArrayList<>();
		buildings.add(new ElementarySchool(game));
		buildings.add(new MidSchool(game));
		buildings.add(new University(game));
		buildings.add(new EducationCenter(game));
		return buildings;
	}

	@Override
	public String getIconName() {
		return "school.png";
	}

	@Override
	public String getName() {
		return "Education";
	}

	@Override
	public String getDescription() {
		return "Educates your habitants making them far more profitable. Education's expensive though.";
	}

	@Override
	public BuildingSector getSector() {
		return BuildingSector.SERVICES;
	}
	
	public static abstract class SimpleSchoolBuilding extends Building{
		
		protected Type[] types;

		public SimpleSchoolBuilding(Game game, float price, float upkeep, float energyConsumption, float waterConsumption, Type... types) {
			super(game, price, 0, upkeep, energyConsumption, waterConsumption);
			this.types = types;
			for(Type type : types){
				Job job = new Job(null, type.level, type.teacherCount);
				job.priority = Job.EDUCATION_PRIORITY;
				functionalities.add(new JobFunctionality(job));
				functionalities.add(new EducationFunctionality(type.level, type.capacity));
			}
		}

		@Override
		public BuildingSector getSector() {
			return BuildingSector.SERVICES;
		}
		
		protected static class Type{
			public EducationLevel level;
			public int capacity;
			public int teacherCount;
			
			public Type(EducationLevel level, int capacity, int teacherCount){
				this.level = level;
				this.capacity = capacity;
				this.teacherCount = teacherCount;
			}
		}
		
		public void build(Vector2 position){
			super.build(position);
			if(!preview){
				for(Type type : types){
					game.educationSystem.addFreeEducation(type.level, type.teacherCount);
				}
			}
		}
	}
}
