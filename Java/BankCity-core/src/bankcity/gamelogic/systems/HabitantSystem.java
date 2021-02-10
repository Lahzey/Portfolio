package bankcity.gamelogic.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.Habitant;
import bankcity.gamelogic.buildings.components.BuildingComponent;
import bankcity.gamelogic.buildings.components.HomeProviderComponent;

public class HabitantSystem extends IntervalIteratingSystem{
	
	private static final float INTERVAL_TIME = 1f;
	public static int AGE_OF_MAJORITY = 18;
	public static int AGE_OF_RETIREMENT = 65;
	
	private int minAge = AGE_OF_MAJORITY;
	private int maxAge = AGE_OF_RETIREMENT;
	public int averageAge = minAge + maxAge / 2;
	
	private Random random = new Random();
	public List<Habitant> habitants = new ArrayList<>();
	
	private ComponentMapper<HomeProviderComponent> hm = ComponentMapper.getFor(HomeProviderComponent.class);
	private ComponentMapper<BuildingComponent> bm = ComponentMapper.getFor(BuildingComponent.class);
	private Game game;

	public HabitantSystem(Game game) {
		super(Family.all(HomeProviderComponent.class, BuildingComponent.class).get(), INTERVAL_TIME);
		this.game = game;
	}
	
	public static float getEfficiencyFor(Habitant habitant){
		if(habitant.job != null){
			if(habitant.job.onlyForSameBuilding) return 1f;
			switch(habitant.job.educationLevel){
			case HIGH:
				return 1f;
			case MEDIUM:
				return 0.5f;
			case LOW:
				return 0.25f;
			case UNEDUCATED:
				return 0.1f;
			default:
				return 0;
			}
		}else return 0;
	}
	
	private Habitant createHabitant(){
		int age;
		float rand = random.nextFloat();
		if(rand > 0.5f){
			age = (int) ((rand * (maxAge - averageAge)) + averageAge);
		}else{
			age = (int) ((rand * (averageAge - minAge)) + minAge);
		}
		
		Habitant habitant = new Habitant(age, game.timeSystem.date);
		game.educationSystem.initHabitantEducation(habitant);
		
		habitants.add(habitant);
		return habitant;
	}

	@Override
	protected void processEntity(Entity entity) {
		HomeProviderComponent homeComp = hm.get(entity);
		BuildingComponent buildingComp = bm.get(entity);
		while(homeComp.capacity > homeComp.habitants.size()){
			homeComp.habitants.add(createHabitant());
		}
		while(homeComp.capacity < homeComp.habitants.size()){
			Habitant habitant = homeComp.habitants.get(0);
			habitant.reset();
		}
		for(Habitant habitant : homeComp.habitants){
			if(habitant.home != buildingComp.building){
				//TODO: Exception for invalid state or ignore?
				if(habitant.home == null) habitant.home = buildingComp.building;
				else throw new IllegalStateException("Habitant " + habitant + " is homed in multiple homes.");
			}
			habitant.home = buildingComp.building;
		}
	}

}
