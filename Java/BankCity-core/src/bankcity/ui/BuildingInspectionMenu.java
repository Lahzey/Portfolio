package bankcity.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bankcity.BankCity;
import bankcity.data.FileLocations;
import bankcity.gamelogic.buildings.Building;
import bankcity.graphics.systems.RenderingSystem;
import bankcity.graphics.systems.RenderingSystem.BuildingHighlighter;
import bankcity.graphics.systems.RenderingSystem.Overlay;
import bankcity.ui.InternalBrowser.Link;
import bankcity.util.InputCatcher.TouchInputCatcher;

public class BuildingInspectionMenu extends Table implements Inspector{
	
	private static final float LINE_THICKNESS = 8;
	private static final float BUTTON_SIZE = 20;
	private static final float IMAGE_SIZE = 150;
	private static final int CLOSING_DURATION = 200; //in milliseconds
	
	private static TextureRegion borderTop = BankCity.getImage(FileLocations.INSPECTION_MENU + "/border_top.png");
	private static TextureRegion borderTopLeft = BankCity.getImage(FileLocations.INSPECTION_MENU + "/border_top_left.png");
	private static TextureRegion borderTopRight = BankCity.getImage(FileLocations.INSPECTION_MENU + "/border_top_right.png");
	
	private static TextureRegion borderBot = BankCity.getImage(FileLocations.INSPECTION_MENU + "/border_bot.png");
	private static TextureRegion borderBotLeft = BankCity.getImage(FileLocations.INSPECTION_MENU + "/border_bot_left.png");
	private static TextureRegion borderBotRight = BankCity.getImage(FileLocations.INSPECTION_MENU + "/border_bot_right.png");

	private static TextureRegion borderLeft = BankCity.getImage(FileLocations.INSPECTION_MENU + "/border_left.png");
	private static TextureRegion center = BankCity.getImage(FileLocations.INSPECTION_MENU + "/center.png");
	private static TextureRegion borderRight = BankCity.getImage(FileLocations.INSPECTION_MENU + "/border_right.png");
	
	private static TextureRegion hrLeft = BankCity.getImage(FileLocations.INSPECTION_MENU + "/hr_left.png");
	private static TextureRegion hrCenter = BankCity.getImage(FileLocations.INSPECTION_MENU + "/hr_center.png");
	private static TextureRegion hrRight = BankCity.getImage(FileLocations.INSPECTION_MENU + "/hr_right.png");
	
	private List<Actor> horizonalRules = new ArrayList<>();
	private Comparator<Actor> hrComparator = new Comparator<Actor>() {
		
		@Override
		public int compare(Actor o1, Actor o2) {
			return (int) (o1.getY() - o2.getY());
		}
	};

	private Building toInspect;
	private BuildingHighlighter highlighter = new BuildingHighlighter(null);
	private Overlay buildingOverlay;
	private RenderingSystem overlayOn;
	
	//for closing animation
	private long closingStartedAt;
	private float originalX;
	
	public BuildingInspectionMenu(){
		super(BankCity.SKIN);
		pad(LINE_THICKNESS);
		addListener(new TouchInputCatcher());
	}
	
	public void inspect(Building toInspect){
		originalX = getX();
		if(toInspect != null){
			resetInternal();
			this.toInspect = toInspect;
			
			Label title = new Label(toInspect.getTitle(), BankCity.SKIN);
			title.setAlignment(Align.center);
			add(title).growX().center();
			
			ImageButton closeButton = new ImageButton(new TextureRegionDrawable(BankCity.getImage(FileLocations.UI + "/close_button.png")));
			closeButton.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					inspect(null);
				}
				
			});
			add(closeButton).size(BUTTON_SIZE).center();
			
			hr();
			
			toInspect.createInspectionUI(this);
		}else{
			//start closing animation
			closingStartedAt = System.currentTimeMillis();
		}
		setOverlays(toInspect);
	}
	
	private void setOverlays(Building building){
		if(overlayOn != null){
			overlayOn.overlays.remove(highlighter);
			if(buildingOverlay != null) overlayOn.overlays.remove(buildingOverlay);
			buildingOverlay = null;
			overlayOn = null;
		}
		
		highlighter.target = building;
		
		if(building != null){
			overlayOn = toInspect.getGame().renderingSystem;
			overlayOn.overlays.add(highlighter);
			buildingOverlay = toInspect.getOverlay();
			if(buildingOverlay != null) overlayOn.overlays.add(buildingOverlay);
		}
	}

	public Building getBuilding() {
		return toInspect;
	}
	
	private void resetInternal(){
		toInspect = null;
		clearChildren();
		setPosition(originalX, getY());
		closingStartedAt = 0;
	}

	
	@Override
	public void hr(){
		row();
		Actor hr = new Actor();
		hr.setHeight(LINE_THICKNESS);
		add(hr).growX();
		row();
		horizonalRules.add(hr);
	}

	@Override
	public Table getInspectionTable() {
		return this;
	}
	

	@Override
	public <T extends Actor> Cell<T> add(T actor) {
		Cell<T> cell = super.add(actor);
		onAdd(cell);
		return cell;
	}
	
	public <T extends Actor> void onAdd(Cell<T> cell){
		Actor actor = cell.getActor();
		if(actor instanceof Image) cell.maxSize(IMAGE_SIZE);
		else if(actor instanceof Link && toInspect != null) ((Link) actor).setColor(toInspect.getColor());
		else if(actor instanceof Table){
			Table table = (Table) actor;
			for(Cell<?> tableCell : table.getCells()){
				onAdd(tableCell);
			}
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(closingStartedAt != 0){
			long timePassed = System.currentTimeMillis() - closingStartedAt;
			float closingProgress = ((float) timePassed) / CLOSING_DURATION;
			if(closingProgress < 1) setPosition(originalX - (getWidth() * closingProgress), getY());
			else resetInternal();
		}
		super.draw(batch, parentAlpha);
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		if(toInspect != null){
			Color oldColor = batch.getColor();
			batch.setColor(toInspect.getColor());
			
			float widthWithoutBorder = getWidth() - LINE_THICKNESS * 2;
			float drawY = y;
			float height = LINE_THICKNESS;
			
			//Draw bottom border (start with bottom cause LibGDX is bottom to top)
			batch.draw(borderBotLeft, x, drawY, LINE_THICKNESS, height);
			batch.draw(borderBot, x + LINE_THICKNESS, drawY, widthWithoutBorder, height);
			batch.draw(borderBotRight, x + LINE_THICKNESS + widthWithoutBorder, drawY, LINE_THICKNESS, height);
			drawY += height;
			
			//Draw background and horizontal rules
			horizonalRules.sort(hrComparator); //keeps the hrs sorted by y (ascending)
			for(Actor hr : horizonalRules){
				height = hr.getY() + y - drawY;
				batch.draw(borderLeft, x, drawY, LINE_THICKNESS, height);
				batch.draw(center, x + LINE_THICKNESS, drawY, widthWithoutBorder, height);
				batch.draw(borderRight, x + LINE_THICKNESS + widthWithoutBorder, drawY, LINE_THICKNESS, height);
				drawY += height;
				
				height = LINE_THICKNESS;
				batch.draw(hrLeft, x, drawY, LINE_THICKNESS, height);
				batch.draw(hrCenter, x + LINE_THICKNESS, drawY, widthWithoutBorder, height);
				batch.draw(hrRight, x + LINE_THICKNESS + widthWithoutBorder, drawY, LINE_THICKNESS, height);
				drawY += height;
			}
			
			//Draw from last actor to top
			height = y + getHeight() - LINE_THICKNESS - drawY;
			batch.draw(borderLeft, x, drawY, LINE_THICKNESS, height);
			batch.draw(center, x + LINE_THICKNESS, drawY, widthWithoutBorder, height);
			batch.draw(borderRight, x + LINE_THICKNESS + widthWithoutBorder, drawY, LINE_THICKNESS, height);
			drawY += height;

			//Draw top border
			height = LINE_THICKNESS;
			batch.draw(borderTopLeft, x, drawY, LINE_THICKNESS, height);
			batch.draw(borderTop, x + LINE_THICKNESS, drawY, widthWithoutBorder, height);
			batch.draw(borderTopRight, x + LINE_THICKNESS + widthWithoutBorder, drawY, LINE_THICKNESS, height);
			
			
			batch.setColor(oldColor);
		}
	}
	
	@Override
	public void clearChildren () {
		super.clearChildren();
		horizonalRules.clear();
	}
}
