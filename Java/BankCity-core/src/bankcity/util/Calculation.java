package bankcity.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bankcity.gamelogic.Stat;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingEffect;
import bankcity.gamelogic.buildings.components.HomeProviderComponent;
import bankcity.gamelogic.buildings.components.ResourceComponent;
import bankcity.gamelogic.buildings.components.ShopComponent;
import bankcity.gamelogic.systems.ResourceSystem;
import bankcity.ui.Inspectable;
import bankcity.ui.Inspector;
import bankcity.ui.InternalBrowser.Link;

public class Calculation extends Number implements Inspectable {
	
	private static final String BASE_COLOR = "[#000000]"; //black
	private static final String OPERATOR_COLOR = "[#595959]"; //grey
	private static final String ADD_COLOR = "[#008706]"; //green
	private static final String MULT_COLOR = "[#3db7ff]"; //light blue
	
	private float base = 0;
	private final Map<Object, Float> adds = new LinkedHashMap<>();
	private final Map<Object, Float> mults = new LinkedHashMap<>();
	
	private boolean changed = false;
	private float result = 0;
	private boolean stringChanged = false;
	private String stringResult = BASE_COLOR + "0";
	
	public Calculation(float base){
		setBase(base);
	}
	
	public float getResult(){
		if(changed){
			float value = base;
			for(float add : adds.values()) value += add;
			float multiplier = 1;
			for(float mult : mults.values()) multiplier *= mult;
			value *= multiplier;
			result = value;
			changed = false;
			stringChanged = true;
		}
		return result;
	}
	
	public String getCalcString(boolean colored){
		if(changed){
			getResult(); //load result so changed can be false again
		}
		if(stringChanged){
			StringBuilder builder = new StringBuilder();
			if(colored) builder.append(BASE_COLOR);
			builder.append(base);
			if(!adds.isEmpty()){
				if(colored) builder.append(ADD_COLOR);
				for(float mult : adds.values()){
					if(colored) builder.append(OPERATOR_COLOR);
					if(mult >= 0) builder.append(" + ");
					else builder.append(" - ");
					if(colored) builder.append(ADD_COLOR);
					if(mult >= 0) builder.append(mult);
					else builder.append(mult * -1);
				}
			}
			if(!mults.isEmpty()){
				for(float mult : mults.values()){
					if(colored) builder.append(OPERATOR_COLOR);
					builder.append(" * ");
					if(colored) builder.append(MULT_COLOR);
					if(mult >= 0) builder.append(mult);
					else builder.append(mult * -1);
				}
			}
			stringResult = builder.toString();
			stringChanged = false;
		}
		return stringResult;
	}
	
	public void setBase(float base){
		if(base != this.base){
			this.base = base;
			changed = true;
		}
	}
	
	public float getBase(){
		return base;
	}
	
	public void putMult(Object key, float mult){
		Float lastValue = mults.put(key, mult);
		if(lastValue == null || lastValue != mult) changed = true;
	}
	
	public void removeMult(Object key){
		if(mults.remove(key) != null) changed = true;
	}
	
	public Float getMult(Object key){
		return mults.get(key);
	}
	
	public void putAdd(Object key, float add){
		Float lastValue = adds.put(key, add);
		if(lastValue == null || lastValue != (Float) add) changed = true;
	}
	
	public void removeAdd(Object key){
		if(adds.remove(key) != null) changed = true;
	}
	
	public Float getAdd(Object key){
		return adds.get(key);
	}
	
	public void reset(float base){
		adds.clear();
		mults.clear();
		base = this.base;
		changed = true;
	}

	@Override
	public void createInspectionUI(Inspector inspector) {
		Table table = inspector.getInspectionTable();
		table.add("Base:").left();
		table.add(base + "").colspan(2).left();
		if(!adds.isEmpty()){
			inspector.hr();
			table.add("Bonuses:").left();
			boolean first = true;
			for(Object addKey : adds.keySet()){
				float add = adds.get(addKey);
				if(add == 1) continue;
				if(!first){
					table.row();
					table.add().left();
				}
				table.add((add >= 0 ? "+" : "") + add).left();

				Object origin = addKey;
				String originText;
				if(origin instanceof ResourceSystem || origin instanceof ResourceComponent){
					originText = "a lack of resources";
				}else if(origin instanceof BuildingEffect){
					BuildingEffect effect = (BuildingEffect) origin;
					origin = effect.getOrigin();
					originText = effect.getOrigin().getName();
				}else if(origin instanceof Building){
					Building building = (Building) origin;
					originText = building.getName();
				}else if(origin instanceof HomeProviderComponent){
					originText = "employment";
				}else if(origin == Stat.CRIMERATE){
					originText = "crimes";
				}else originText = origin.getClass().getSimpleName();
				
				if(origin instanceof Inspectable){
					table.add(new Link((Inspectable) origin, "(from " + originText + ")"));
				}else{
					table.add("(from " + originText + ")").left();
				}
				
				first = false;
			}
		}
		if(!mults.isEmpty()){
			inspector.hr();
			table.add("Multipliers:").left();
			boolean first = true;
			for(Object multKey : mults.keySet()){
				float mult = mults.get(multKey);
				if(mult == 1) continue;
				if(!first){
					table.row();
					table.add().left();
				}
				table.add("*" + mult).left();
				
				Object origin = multKey;
				String originText;
				if(origin instanceof ResourceSystem || origin instanceof ResourceComponent){
					originText = "a lack of resources";
				}else if(origin instanceof BuildingEffect){
					BuildingEffect effect = (BuildingEffect) origin;
					origin = effect.getOrigin();
					originText = effect.getOrigin().getName();
				}else if(origin instanceof Building){
					Building building = (Building) origin;
					originText = building.getName();
				}else if(origin instanceof HomeProviderComponent){
					originText = "employment";
				}else if(origin instanceof ShopComponent){
					originText = "a lack of retail";
				}else originText = origin.getClass().getSimpleName();
				
				if(origin instanceof Inspectable){
					table.add(new Link((Inspectable) origin, "(from " + originText + ")"));
				}else{
					table.add("(from " + originText + ")").left();
				}
				
				first = false;
			}
		}
		inspector.hr();
		table.add("Result:").left();
		table.add(getResult() + "").colspan(2).left();
	}

	@Override
	public String getTitle() {
		return "Calculated Value";
	}

	@Override
	public Color getColor() {
		return Color.WHITE;
	}
	
	
	// Implementation of number methods

	@Override
	public int intValue() {
		return (int) getResult();
	}

	@Override
	public long longValue() {
		return (long) getResult();
	}

	@Override
	public float floatValue() {
		return getResult();
	}

	@Override
	public double doubleValue() {
		return getResult();
	}
}
