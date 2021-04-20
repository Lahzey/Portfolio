package bankcity.gamelogic.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import util.StringFormatter;

import bankcity.gamelogic.Game;
import bankcity.gamelogic.buildings.components.ResourceComponent;
import bankcity.gamelogic.buildings.components.ShopComponent;
import bankcity.util.Calculation;
import bankcity.util.GameDate;

public class ResourceSystem extends IntervalIteratingSystem{
	
	private static final float INTERVAL_TIME = 1f;
	
	private ComponentMapper<ResourceComponent> rm = ComponentMapper.getFor(ResourceComponent.class);
	private ComponentMapper<ShopComponent> sm = ComponentMapper.getFor(ShopComponent.class);
	
	// Food and Material handling
	public long lastImportExportTime = 0;
	
	public static float FOOD_CONSUMPTION_PER_HABITANT = 1;
	public float foodImportPrice = 1000f;
	public float foodExportPrice = 500f;
	public float foodImportExportYield = 0f;
	
	public static float MATERIAL_CONSUMPTION_PER_HABITANT = 1;
	public float materialImportPrice = 1000f;
	public float materialExportPrice = 500f;
	public float materialImportExportYield = 0f;
	
	
	// general resource handling
	public Map<Resource, Float> resources = new HashMap<>();
	public Map<Resource, Float> totalProduction = new HashMap<>();
	public Map<Resource, Float> totalConsumption = new HashMap<>();
	public Map<Resource, Float> retailCapacity = new HashMap<>();
	
	// resource producer / consumer
	private List<Entity> queue = new ArrayList<>();
	private List<Entity> waterCleaner = new ArrayList<>();
	private List<ShopComponent> shopQueue = new ArrayList<>();
	public Map<Resource, List<Entity>> unsatisfied = new HashMap<>();
	
	private Game game;

	public ResourceSystem(Game game) {
		super(Family.one(ResourceComponent.class, ShopComponent.class).get(), INTERVAL_TIME);
		this.game = game;
		setToZero();
	}
	
	private void setToZero(){
		for(Resource res : Resource.values()){
			resources.put(res, 0f);
			totalProduction.put(res, 0f);
			totalConsumption.put(res, 0f);
			retailCapacity.put(res, 0f);
			
			List<Entity> list = unsatisfied.get(res);
			if(list == null) unsatisfied.put(res, new ArrayList<>());
			else list.clear();
		}
	}
	
	@Override
	protected void updateInterval(){
		queue.clear();
		waterCleaner.clear();
		shopQueue.clear();
		unsatisfied.clear();
		super.updateInterval();
		setToZero();
		
		// production
		for(int i = 0; i < queue.size(); i++){
			Entity entity = queue.get(i);
			ResourceComponent producer = rm.get(entity);
			for(Resource resource : producer.resourceProduction.keySet()){
				Calculation calc = producer.resourceProduction.get(resource);
				calc.putMult(this, producer.netYieldMult);
				float production = calc.getResult();
				Float stock = resources.get(resource);
				if(stock == null) stock = 0f;
				stock += production;
				resources.put(resource, stock);
				totalProduction.put(resource, stock);
			}
		}

		// consumption
		for(int i = 0; i < queue.size(); i++){
			Entity entity = queue.get(i);
			ResourceComponent consumer = rm.get(entity);
			consumer.efficiencyMult = 1;
			for(Resource resource : consumer.resourceConsumption.keySet()){
				if(resource == Resource.DIRTY_WATER){
					waterCleaner.add(entity);
					continue;
				}
				Calculation calc = consumer.resourceConsumption.get(resource);
				calc.putMult(this, consumer.netYieldMult);
				float consumption = calc.getResult();
				Float stock = resources.get(resource);
				if(stock == null) stock = 0f;
				if(consumption > stock && resource != Resource.ATTRACTIVITY){ // attractivity may go under 0 and should definitely not block consumption and production
					float usage = (consumption > 0 ? stock / consumption : 1);
					consumption = stock;
					if(consumer.efficiencyMult > usage) consumer.efficiencyMult = usage;
					unsatisfied.get(resource).add(entity);
				}
				stock -= consumption;
				resources.put(resource, stock);
				totalConsumption.put(resource, totalConsumption.get(resource) + consumption);
			}
		}

		// water cleaning
		for(Entity entity : waterCleaner){
			ResourceComponent cleaner = rm.get(entity);
			
			float totalWaterConsumption = totalConsumption.get(Resource.WATER);
			float totalWaterCleaning = totalConsumption.get(Resource.DIRTY_WATER);
			float remainingDirtyWater = totalWaterConsumption - totalWaterCleaning;
			
			Calculation calc = cleaner.resourceConsumption.get(Resource.DIRTY_WATER);
			calc.putMult(this, cleaner.netYieldMult);
			float cleanAmount = calc.getResult();
			float usage = 1;
			if(cleanAmount > 0 && cleanAmount > remainingDirtyWater){
				usage = remainingDirtyWater / cleanAmount;
				cleanAmount = remainingDirtyWater;
				if(cleaner.efficiencyMult > usage) cleaner.efficiencyMult = usage;
				unsatisfied.get(Resource.DIRTY_WATER).add(entity);
			}
			totalConsumption.put(Resource.DIRTY_WATER, totalWaterCleaning + cleanAmount);
		}
		
		// food and material handling
		float habitantCount = game.habitantSystem.habitants.size();
		float yearsPassed = (game.timeSystem.date.days - lastImportExportTime) / GameDate.DAYS_PER_YEAR;
		lastImportExportTime = game.timeSystem.date.days;
		
		float totalFoodConsumption = habitantCount * FOOD_CONSUMPTION_PER_HABITANT;
		totalConsumption.put(Resource.FOOD, totalConsumption.get(Resource.FOOD) + totalFoodConsumption);
		float totalMaterialConsumption = habitantCount * MATERIAL_CONSUMPTION_PER_HABITANT;
		totalConsumption.put(Resource.MATERIAL, totalConsumption.get(Resource.MATERIAL) + totalMaterialConsumption);
		
		float remainingFood = resources.get(Resource.FOOD) - totalFoodConsumption;
		float remainingMaterial = resources.get(Resource.MATERIAL) - totalMaterialConsumption;
		
		if(remainingFood > 0) game.money += remainingFood * foodExportPrice * yearsPassed;
		else game.money += remainingFood * foodImportPrice * yearsPassed;
		foodImportExportYield = remainingFood;
		// TODO: handle food shortages
		
		if(remainingMaterial > 0) game.money += remainingMaterial * materialExportPrice * yearsPassed;
		else game.money += remainingMaterial * materialImportPrice * yearsPassed;
		materialImportExportYield = remainingMaterial;
		// TODO: handle material shortages
		

		// shops
		for(ShopComponent shop : shopQueue){
			for(Resource resource : shop.offer.keySet()){
				float offer = shop.offer.get(resource);
				float currentRetail = retailCapacity.get(resource);
				float consumption = totalConsumption.get(resource);
				retailCapacity.put(resource, currentRetail + offer);
				float sold = Math.min(offer, Math.max(0, consumption - currentRetail));
				shop.sold.put(resource, sold);
			}
		}
	}
	
	private void test() {
		Map<Resource, Number> desiredResources = new HashMap<Resource, Number>();
		Map<Resource, Number> spareResources = new HashMap<Resource, Number>();
		for(Resource resource : Resource.values()) {
			desiredResources.put(resource, 0f);
			spareResources.put(resource, 0f);
		}
		
		List<ResourceComponent> resourceComponents = new ArrayList<ResourceComponent>();
		Iterator<ResourceComponent> itr = resourceComponents.iterator();
		while(itr.hasNext()) {
			ResourceComponent resourceComponent = itr.next();
			if(doesConsume(resourceComponent)) {
				addAll(desiredResources, resourceComponent.resourceConsumption);
			} else {
				addAll(spareResources, resourceComponent.resourceProduction);
				itr.remove();
			}
		}
		
		
		
	}
	
	private boolean doesConsume(ResourceComponent resourceComponent) {
		for(Calculation consumption : resourceComponent.resourceConsumption.values()) {
			if(consumption.getResult() > 0) {
				return true;
			}
		}
		return false;
	}
	
	private void addAll(Map<Resource, Number> addTo, Map<Resource, ? extends Number> addFrom) {
		for(Resource resource : addFrom.keySet()) {
			Number currentValue = addTo.get(resource);
			float newValue = addFrom.get(resource).floatValue() + (currentValue == null ? 0 : currentValue.floatValue());
			addTo.put(resource, newValue);
		}
	}

	@Override
	protected void processEntity(Entity entity) {
		if(rm.get(entity) != null) queue.add(entity);
		ShopComponent shop = sm.get(entity);
		if(shop != null) shopQueue.add(shop);
	}
	
	public float getResource(Resource resource){
		if(resources.containsKey(resource)){
			return resources.get(resource);
		}else{
			return 0;
		}
	}

	
	public enum Resource{
		
		FOOD("Provides food for ", " People", "1"),
		WATER("liters / hour [clean water]", "0.1"),
		DIRTY_WATER("liters / hour [dirty water]", "0.1"),
		ELECTRICITY("MW / hour", "0.01"),
		MATERIAL("materials", "1"), 
		
		POSTAL_CAPACITY("Provides ", " Postal Capacity", "1"),
		HEALTH_CAPACITY("Provides Healthcare for ", " People", "1"),
		ATTRACTIVITY("Increases the attractivity of your town by " , "", "0.01");

		private static final String PLACEHOLDER = "%RES%";
		private String displayText;
		private String formattingStep;
		
		private Resource(String resourceName, String formattingStep){
			this("Produces ", " " + resourceName, formattingStep);
		}
		
		private Resource(String before, String after, String formattingStep){
			displayText = before + PLACEHOLDER + after;
			this.formattingStep = formattingStep;
		}
		
		public String getDisplayText(float resourceAmount){
			return displayText.replace(PLACEHOLDER, StringFormatter.formatNumber(resourceAmount, formattingStep));
		}
	}
}
