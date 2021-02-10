package bankcity.gamelogic.buildings.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bankcity.BankCity;
import bankcity.gamelogic.Stat;
import bankcity.gamelogic.Stat.StatList;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.interfaces.InspectableComponent;
import bankcity.gamelogic.buildings.components.interfaces.StatComponent;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class ShopComponent implements InspectableComponent, StatComponent{
	
	public final Map<Resource, Float> offer = new HashMap<>();
	public final Map<Resource, Float> sold = new HashMap<>();
	
	public boolean affectEfficiency = true;

	@Override
	public void update(StatList stats, float deltaTime) {
		if(affectEfficiency){
			float totalOfferAmount = 0;
			for(Float offerAmount : offer.values()){
				totalOfferAmount += offerAmount;
			}
			float totalSellAmount = 0;
			for(Float sellAmount : sold.values()){
				totalSellAmount += sellAmount;
			}
			stats.get(Stat.EFFICIENCY).putMult(this, totalSellAmount / totalOfferAmount);
		}
	}

	@Override
	public Actor getInspectionUI(Building building) {
		Table table = new Table(BankCity.SKIN);
		table.add("Sells:").left();
		for(Resource res : offer.keySet()){
			table.row();
			table.add(sold.get(res) + "/" + offer.get(res) + " " + res.name().replaceAll("_", " ")).left();
		}
		return table;
	}

	@Override
	public void reset() {
		offer.clear();
		sold.clear();
	}

}
