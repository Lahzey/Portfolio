package bankcity.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import bankcity.BankCity;
import bankcity.data.FileLocations;
import bankcity.gamelogic.buildings.Building;
import bankcity.ui.stages.InGameStage;
import bankcity.util.ColorDrawable;
import bankcity.util.DynamicLabel;
import bankcity.util.DynamicLabel.DynamicString;

public class BuildingButton extends ImageButton{

	private InGameStage inGameStage;
	private Building building;
	private TextureRegion normal;
	private TextureRegion selected;
	private TextureRegion disabled;
	
	private Table tooltip;
	
	public BuildingButton(InGameStage inGameStage, Building building){
		super(new TextureRegionDrawable(building.getTexture()));
		this.inGameStage = inGameStage;
		this.building = building;
		normal = BankCity.getImage(FileLocations.BUILDING_BUTTON + "/normal.png");
		selected = BankCity.getImage(FileLocations.BUILDING_BUTTON + "/selected.png");
		disabled = BankCity.getImage(FileLocations.BUILDING_BUTTON + "/disabled.png");
		getCell(getImage()).pad(5);
		
		addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(!isDisabled()) inGameStage.buildingCreator.setPreview(building);
			}
			
		});
		
		tooltip = new Table();
		tooltip.setBackground(new ColorDrawable(new Color(building.getSector().color).mul(Color.WHITE), new Color(building.getSector().color).mul(Color.BLACK), 3));
		tooltip.pad(5);
		DynamicLabel textLabel = new DynamicLabel(new DynamicString() {
			
			@Override
			public String toString() {
				String text = "[#000000]" + building.getName();
				if(building.getPrice() > building.getGame().money) text = "[#f46b42]Cannot afford\n" + text;
				else if(!building.isEnabled()) text = "[#f46b42]Requirements not met\n" + text;
				return text;
			}
		});
		textLabel.getStyle().font.getData().markupEnabled = true;
		tooltip.add(textLabel).grow();
		Tooltip<Table> tooltipWrapper = new Tooltip<Table>(tooltip);
		tooltipWrapper.setInstant(true);
		addListener(tooltipWrapper);
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		if(isDisabled()){
			batch.draw(disabled, x, y, getWidth(), getHeight());
		}else if(isSelected() || isOver()){
			batch.draw(selected, x, y, getWidth(), getHeight());
		}else{
			batch.draw(normal, x, y, getWidth(), getHeight());
		}
	}
	
	public boolean isSelected(){
		Building selection = inGameStage.buildingCreator.getPreview();
		return (selection != null && selection.getClass().equals(building.getClass()));
	}
	
	@Override
	public boolean isDisabled(){
		return super.isDisabled() || (building == null ? true : building.getPrice() > inGameStage.game.money);
	}
	
	

}
