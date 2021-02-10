package bankcity.gamelogic.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.buildings.components.BuildingComponent;

public class EconomySystem extends IntervalIteratingSystem{

	private static final float INTERVAL_TIME = 1f;
	private Game game;
	
	private double totalIncome = 0;
	private double totalUpkeep = 0;
	
	private ComponentMapper<BuildingComponent> buildingMapper = ComponentMapper.getFor(BuildingComponent.class);

	public EconomySystem(Game game) {
		super(Family.all(BuildingComponent.class).get(), INTERVAL_TIME);
		this.game = game;
	}
	
	@Override
	public void updateInterval(){
		totalIncome = 0;
		totalUpkeep = 0;
		super.updateInterval();
	}

	@Override
	protected void processEntity(Entity entity) {
		BuildingComponent buildingComponent = buildingMapper.get(entity);
		float income = buildingComponent.building.stats.get(Stat.INCOME).getResult();
		float upkeep = buildingComponent.building.stats.get(Stat.UPKEEP).getResult();
		game.money += (income - upkeep) * (INTERVAL_TIME * TimeSystem.DAY_DURATION / 365);
		totalIncome += income;
		totalUpkeep += upkeep;
	}
	
	public double getTotalIncome(){
		return totalIncome;
	}
	
	public double getTotalUpkeep(){
		return totalUpkeep;
	}
	
	public double getTotalYield(){
		return totalIncome- totalUpkeep;
	}

}
