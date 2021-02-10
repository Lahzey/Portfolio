package bankcity.gamelogic.buildings.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bankcity.gamelogic.Stat;
import bankcity.gamelogic.Stat.StatList;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.interfaces.InspectableComponent;
import bankcity.gamelogic.buildings.components.interfaces.StatComponent;
import bankcity.ui.InternalBrowser.Link;
import bankcity.util.DynamicLabel;
import bankcity.util.DynamicLabel.DynamicString;

public class BuildingComponent implements StatComponent, InspectableComponent{
	
	public Building building;

	@Override
	public void reset() {
		building = null;
	}

	@Override
	public void update(StatList stats, float deltaTime) {
		stats.get(Stat.INCOME).putMult(Stat.EFFICIENCY, stats.get(Stat.EFFICIENCY).getResult());
	}

	@Override
	public Actor getInspectionUI(Building building) {
		Table table = new Table();
		table.add(new Link(building.stats.get(Stat.EFFICIENCY), new DynamicString() {
			
			@Override
			public String toString() {
				int efficiencyInPercent = (int)(building.stats.get(Stat.EFFICIENCY).getResult() * 100);
				return "Efficiency: " + efficiencyInPercent + "%";
			}
		})).left();
		table.row();
		table.add(new DynamicLabel(new DynamicString() {
			
			@Override
			public String toString() {
				int costOrIncome = (int)(building.stats.get(Stat.INCOME).getResult() - building.stats.get(Stat.UPKEEP).getResult());
				return (costOrIncome < 0 ? "Costs " : "Generates ") + costOrIncome + " $ / year";
			}
		})).left();
		return table;
	}

}
