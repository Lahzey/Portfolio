package bankcity.gamelogic.functionality;

import com.badlogic.ashley.core.Engine;

import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.UpgradeComponent;
import bankcity.gamelogic.buildings.components.UpgradeComponent.Upgrader;

public class UpgradeFunctionality implements Functionality{
	
	public Upgrader upgrader;
	
	public UpgradeFunctionality(Upgrader upgrader){
		this.upgrader = upgrader;
	}

	@Override
	public void addToBuilding(Engine engine, Building building) {
		UpgradeComponent upgradeComp = building.getEntity().getComponent(UpgradeComponent.class);
		if(upgradeComp == null){
			upgradeComp = engine.createComponent(UpgradeComponent.class);
			building.getEntity().add(upgradeComp);
		}
		upgradeComp.upgrader = upgrader;
	}

}
