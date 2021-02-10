package bankcity.gamelogic;

import java.util.HashMap;

import bankcity.util.Calculation;

public class Stat {

	public static Stat EFFICIENCY = new Stat(1f);
	public static Stat CRIMERATE = new Stat(0.5f);
	public static Stat BURNDOWNRATE = new Stat(1f);
	
	public static Stat PRICE = new Stat(0f);
	public static Stat UPKEEP = new Stat(0f);
	public static Stat INCOME = new Stat(0f);
	
//	public static Stat RESOURCE_PRODUCTION = new Stat(0f);
//	public static Stat RESOURCE_CONSUMPTION = new Stat(0f);
//	
//	public static Stat HOUSING_CAPACITY = new Stat(0f);
//	public static Stat WORKPLACE_CAPACITY = new Stat(0f);
//	public static Stat HEALTH_CAPACITY = new Stat(0f);
	
	public final float defaultValue;
	
	private Stat(float defaultValue){
		this.defaultValue = defaultValue;
	}
	
	
	public static class StatList extends HashMap<Stat, Calculation>{
		private static final long serialVersionUID = 1L;
	
		public Calculation get(Object stat){
			if(stat instanceof Stat) return get((Stat) stat);
			else throw new IllegalArgumentException("Must give a paramater of the class Stat");
		}
		
		public Calculation get(Stat stat){
			Calculation value = super.get(stat);
			if(value == null){
				value = new Calculation(stat.defaultValue);
				put(stat, value);
			}
			return value;
		}
	}
}
