package bankcity.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bankcity.BankCity;
import bankcity.data.FileLocations;
import bankcity.gamelogic.buildings.Building;
import bankcity.gamelogic.buildings.BuildingInitializer;
import bankcity.gamelogic.buildings.BuildingSector;
import bankcity.ui.stages.InGameStage;

public class BuildMenu extends Table{
	
	private InGameStage inGameStage;
	
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 100;
	public static final int PADDING_RIGHT = 50 - 13;
	public static final int BUTTON_HEIGHT = 26;
	public static final int BUTTON_WIDTH = 75;
	
	private TabList tabList;
	private ScrollPane contentArea;
	private Tab currentTab;

	public BuildMenu(InGameStage inGameStage){
		this.inGameStage = inGameStage;
		init();
	}

	private void init() {
		bottom();
		setBackground(new TextureRegionDrawable(BankCity.getImage(FileLocations.BUILD_MENU + "/background.png")));
		
		tabList = new TabList();
		add(tabList).height(BUTTON_HEIGHT).padLeft(PADDING_RIGHT).padRight(PADDING_RIGHT).growX();
		
		row();
		
		contentArea = new ScrollPane(new Table(), BankCity.SKIN);
		contentArea.setDebug(true);
		contentArea.getStyle().background = null;
		contentArea.setFadeScrollBars(true);
		
		add(contentArea).padLeft(PADDING_RIGHT).padRight(PADDING_RIGHT).grow();
		
		Tab residentialTab = new Tab(BuildingSector.RESIDENTIAL);
		addTab(residentialTab);
		
		Tab businessTab = new Tab(BuildingSector.BUSINESS);
		addTab(businessTab);
		
		Tab servicesTab = new Tab(BuildingSector.SERVICES);
		addTab(servicesTab);
		
		Tab leisureTab = new Tab(BuildingSector.LEISURE);
		addTab(leisureTab);
		
		if(!residentialTab.isEmpty()){
			setCurrentTab(residentialTab);
		}
		if(!businessTab.isEmpty()){
			setCurrentTab(businessTab);
		}
		if(!servicesTab.isEmpty()){
			setCurrentTab(servicesTab);
		}
		if(!leisureTab.isEmpty()){
			setCurrentTab(leisureTab);
		}
	}
	
	private void addTab(Tab tab){
		tabList.addTab(tab);
		if(currentTab == null) setCurrentTab(tab);
	}
	
	private void setCurrentTab(Tab tab){
		currentTab = tab;
		if(tab != null) contentArea.setActor(tab.content);
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		if(currentTab != null) setColor(currentTab.category.color);
		super.drawBackground(batch, parentAlpha, x, y);
		setColor(Color.WHITE);
		batch.setColor(Color.WHITE);
	}
	
	public Tab getTab(BuildingSector category){
		for(Tab tab : tabList.tabs){
			if(tab.category == category) return tab;
		}
		return null;
	}
	
	
	
	public class Tab{
		
		private BuildingSector category;
		private Table content;
		
		private Map<Building, BuildingButton> buttons = new HashMap<>();
		
		public Tab(BuildingSector category){
			this.category = category;
			content = new Table(){

				@Override
				public void draw(Batch batch, float parentAlpha) {
					refresh();
					super.draw(batch, parentAlpha);
				}
				
			};
			content.left();
			create();
		}
		
		private void create(){
			for(Building preview : BuildingInitializer.getBuildings(category)){
				if(preview.isVisible()) add(preview);
			}
		}
		
		private void recreate(){
			content.clearChildren();
			buttons.clear();
			create();
		}
		
		public void add(Building type){
			BuildingButton button = new BuildingButton(inGameStage, type){
				@Override
				public boolean isDisabled(){
					return super.isDisabled() || !type.isEnabled();
				}
			};
			content.add(button).width(50).height(50).padLeft(content.getChildren().size > 0 ? InGameStage.MEDIUM_PADDING : 0);
			buttons.put(type, button);
		}
		
		public void remove(Building type){
			BuildingButton button = buttons.get(type);
			if(button != null){
				content.removeActor(button);
				buttons.remove(type);
			}
		}
		
		public void refresh(){
			int count = 0;
			for(Building preview : BuildingInitializer.getBuildings(category)){
				if(preview.isVisible()){
					count++;
					if(!buttons.containsKey(preview)){
						recreate();
						return;
					}
				}
			}
			if(buttons.keySet().size() != count) recreate();
		}
		
		public boolean isEmpty(){
			return buttons.isEmpty();
		}
	}
	
	private class TabList extends Table{
		
		private static final int SIDE_WIDTH = 13;
		
		private List<Tab> tabs = new ArrayList<>();
		
		private TextureRegion left;
		private TextureRegion right;
		private TextureRegion middle;
		
		public TabList(){
			left = BankCity.getImage(FileLocations.BUILD_MENU + "/tab_left.png");
			right = BankCity.getImage(FileLocations.BUILD_MENU + "/tab_right.png");
			middle = BankCity.getImage(FileLocations.BUILD_MENU + "/tab_center.png");
		}
		
		public void addTab(Tab tab){
			tabs.add(tab);
			Label tabLabel = new Label(tab.category.name, BankCity.SKIN){
				@Override
				public void draw(Batch batch, float parentAlpha){
					if(currentTab == tab) batch.setColor(tab.category.color);
					else batch.setColor(tab.category.color.r * 0.75f, tab.category.color.g * 0.75f, tab.category.color.b * 0.75f, tab.category.color.a);
					batch.draw(left, getX() - SIDE_WIDTH, getY(), SIDE_WIDTH, getHeight());
					batch.draw(middle, getX(), getY(), getWidth(), getHeight());
					batch.draw(right, getX() + getWidth(), getY(), SIDE_WIDTH, getHeight());
					batch.setColor(Color.WHITE);
					super.draw(batch, parentAlpha);
				}
			};
			tabLabel.setAlignment(Align.center);
			tabLabel.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					setCurrentTab(tab);
				}
			});
			add(tabLabel).center().grow().padLeft(SIDE_WIDTH).padRight(SIDE_WIDTH);
		}
	}

}
