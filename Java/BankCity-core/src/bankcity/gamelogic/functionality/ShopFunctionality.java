package bankcity.gamelogic.functionality;

import java.util.Map;

import com.badlogic.ashley.core.Engine;

import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.ShopComponent;
import bankcity.gamelogic.systems.ResourceSystem.Resource;

public class ShopFunctionality implements Functionality{
	
	public Map<Resource, Float> offer;
	public boolean affectEfficiency = true;
	
	public ShopFunctionality(Map<Resource, Float> offer){
		this.offer = offer;
	}

	@Override
	public void addToBuilding(Engine engine, Building building) {
		ShopComponent shopComp = building.getEntity().getComponent(ShopComponent.class);
		if(shopComp == null){
			shopComp = engine.createComponent(ShopComponent.class);
			building.getEntity().add(shopComp);
		}
		for(Resource res : offer.keySet()){
			float currentOffer = shopComp.offer.getOrDefault(res, 0f);
			shopComp.offer.put(res, currentOffer + offer.get(res));
		}
		shopComp.affectEfficiency = affectEfficiency;
	}

}
