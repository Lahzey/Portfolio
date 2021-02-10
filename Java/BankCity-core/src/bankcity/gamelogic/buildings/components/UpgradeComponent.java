package bankcity.gamelogic.buildings.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import bankcity.BankCity;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.components.interfaces.InspectableComponent;
import bankcity.ui.stages.InGameStage;
import bankcity.util.ColorDrawable;
import bankcity.util.DynamicLabel;
import bankcity.util.DynamicLabel.DynamicString;
import bankcity.util.Scene2DUtil;

public class UpgradeComponent implements InspectableComponent{
	
	public int level = 1;
	public Upgrader upgrader;


	@Override
	public Actor getInspectionUI(Building building) {
		Table table = new Table(BankCity.SKIN);
		Label levelLabel = new Label("Level " + level, BankCity.SKIN);
		table.add(levelLabel);
		if(level < upgrader.getMaxLevel(building)){
			ImageButtonStyle style = Scene2DUtil.createImageButton(BankCity.TEXTURE_MANAGER, "ui/icons/upgrade", "png").getStyle();
			ImageButton upgradeButton = new ImageButton(style){

				@Override
				public boolean isDisabled() {
					return !upgrader.canUpgrade(level + 1, building) || upgrader.getCost(level + 1, building) > building.getGame().money;
				}
				
			};
			upgradeButton.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					if(!upgradeButton.isDisabled()){
						level++;
						upgrader.upgrade(level, building);
						building.getGame().money -= upgrader.getCost(level, building);
						levelLabel.setText("Level " + level);
						if(level >= upgrader.getMaxLevel(building)) table.removeActor(upgradeButton);
					}
				}
				
			});
			DynamicLabel tooltipText = new DynamicLabel(new DynamicString() {
				@Override
				public String toString(){
					if(level > upgrader.getMaxLevel(building)){
						return "max level reached";
					}else if(!upgrader.canUpgrade(level, building)){
						return "this is not yet unlocked";
					}else if(upgrader.getCost(level + 1, building) > building.getGame().money){
						return "not enough money\nCosts " + upgrader.getCost(level + 1, building) + "$";
					}else{
						return upgrader.getUpgradeText(level + 1, building) + "\nCosts " + upgrader.getCost(level + 1, building) + "$";
					}
				}
			});
			Table tooltip = new Table();
			tooltip.setBackground(new ColorDrawable(Color.WHITE, Color.BLACK, 3));
			tooltip.pad(5);
			tooltip.add(tooltipText);
			upgradeButton.addListener(new Tooltip<Table>(tooltip));
			table.add(upgradeButton).padLeft(InGameStage.SMALL_PADDING).height(Value.percentHeight(2f, levelLabel));
		}
		return table;
	}
	
	@Override
	public void reset() {
		upgrader = null;
	}
	
	public static interface Upgrader{
		
		public int getMaxLevel(Building building);
		
		public boolean canUpgrade(int level, Building building);
		
		public String getUpgradeText(int level, Building building);
		
		public double getCost(int level, Building building);
		
		public void upgrade(int level, Building building);
		
		
	}
	
	public static class UpgradeInfo{
		public String text;
		public double cost;
		
		public UpgradeInfo(String text, float cost){
			this.text = text;
			this.cost = cost;
		}
	}
	
	public static abstract class SimpleUpgrader implements Upgrader{
		
		public UpgradeInfo[] upgrades;
		
		public SimpleUpgrader(UpgradeInfo... upgrades){
			this.upgrades = upgrades;
		}
		
		public int getMaxLevel(Building building){
			return upgrades.length + 1; //because first upgrade is for level 2
		}
		
		public boolean canUpgrade(int level, Building building){
			return level <= getMaxLevel(building);
		}
		
		public String getUpgradeText(int level, Building building){
			return upgrades[level - 2].text;
		}
		
		public double getCost(int level, Building building){
			if(level <= upgrades.length + 1){
				return upgrades[level - 2].cost;
			}else return 0;
		}
		
		public abstract void upgrade(int level, Building building);
		
	}

}
