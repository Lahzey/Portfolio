package bankcity.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import bankcity.BankCity;
import bankcity.data.FileLocations;
import bankcity.util.ColorDrawable;
import bankcity.util.DynamicLabel.DynamicString;
import bankcity.util.Scene2DUtil;

public class InternalBrowser extends Table implements Inspector{

	private static String FORWARD_ICON_NAME = FileLocations.BROWSER + "/forward";
	private static String BACKWARD_ICON_NAME = FileLocations.BROWSER + "/backward";
	private static TextureRegion HR_ICON = BankCity.getImage(FileLocations.BROWSER + "/hr.png");
	private static TextureRegion RELOAD_ICON = BankCity.getImage(FileLocations.BROWSER + "/reload.png");
	private static TextureRegion CLOSE_ICON = BankCity.getImage(FileLocations.UI + "/close_button.png");
	
	private static final float ICON_SIZE = 20;
	private static final float HR_SIZE = 5;
	private static final float PAD_SIZE = 5;
	private static final float BORDER_SIZE = 2;
	
	public static final InternalBrowser INSTANCE = new InternalBrowser();

	private List<Inspectable> history = new ArrayList<>();
	private int currentHistoryIndex = -1;
	private Inspectable current = null;
	
	private ImageButton forward;
	private ImageButton backward;
	private ImageButton reload;
	private ImageButton close;
	private Label title;
	private Image hr;
	
	private Table content;;
	private ScrollPane scroll;
	
	
	private InternalBrowser(){
		super(BankCity.SKIN);
		setBackground(new ColorDrawable(Color.LIGHT_GRAY, Color.DARK_GRAY, BORDER_SIZE));
		pad(BORDER_SIZE);
		
		Table header = new Table();
		
//		addListener(new DragListener() {
//		    public void drag(InputEvent event, float x, float y, int pointer) {
//		        moveBy(x - getWidth() / 2, y - getHeight() / 2);
//		    }
//		});
		
		backward = Scene2DUtil.createImageButton(BankCity.TEXTURE_MANAGER, BACKWARD_ICON_NAME);
		backward.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				backward();
			}
		});
		header.add(backward).pad(PAD_SIZE).size(ICON_SIZE).left();
		
		forward = Scene2DUtil.createImageButton(BankCity.TEXTURE_MANAGER, FORWARD_ICON_NAME);
		forward.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				forward();
			}
		});
		header.add(forward).pad(PAD_SIZE).size(ICON_SIZE).left();
		
		reload = new ImageButton(new TextureRegionDrawable(RELOAD_ICON));
		reload.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				load();
			}
		});
		header.add(reload).pad(PAD_SIZE).size(ICON_SIZE).left();
		
		title = new Label("", BankCity.SKIN, "title");
		title.setWrap(true);
		header.add(title).pad(PAD_SIZE).growX().left();
		
		close = new ImageButton(new TextureRegionDrawable(CLOSE_ICON));
		close.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				close();
			}
		});
		header.add(close).pad(PAD_SIZE).size(ICON_SIZE).right();
		
		add(header).growX();
		
		row();
		
		hr = new Image(HR_ICON);
		hr.setScaling(Scaling.stretch);
		add(hr).growX().height(HR_SIZE).top();
		
		row();
		
		content = new Table(BankCity.SKIN){
			
			@Override
			public <T extends Actor> Cell<T> add(T actor) {
				Cell<T> cell = super.add(actor);
				onAdd(cell);
				return cell;
			}
			
			public <T extends Actor> void onAdd(Cell<T> cell){
				Actor actor = cell.getActor();
				if(actor instanceof Image) cell.maxWidth(Value.percentWidth(0.8f, InternalBrowser.this)).maxHeight(Value.percentHeight(0.4f, InternalBrowser.this));
				else if(actor instanceof Table){
					Table table = (Table) actor;
					for(Cell<?> tableCell : table.getCells()){
						onAdd(tableCell);
					}
				}
			}
		};
		
		scroll = new ScrollPane(content, BankCity.SKIN);
		scroll.getStyle().background = null;
		scroll.setOverscroll(false, false);
		scroll.setFadeScrollBars(false);
		add(scroll).growX().top();
		content.pad(PAD_SIZE);
		
		row();

		//footer
		Image hr = new Image(HR_ICON);
		hr.setScaling(Scaling.stretch);
		add(hr).fillX().expand().bottom().height(HR_SIZE);
		row();
		add("Internal Browser - BankCity\u00a9").right();
		
		load();
	}
	
	private void load(){
		content.clear();
		if(currentHistoryIndex >= 0 && history.size() > currentHistoryIndex){
			current = history.get(currentHistoryIndex);
			current.createInspectionUI(this);
			title.setText(current.getTitle());
			setVisible(true);
		}else{
			current = null;
			setVisible(false);
		}
		forward.setDisabled(!canForward());
		backward.setDisabled(!canBackward());
	}
	
	@SuppressWarnings({ "unchecked" })
	public void open(Inspectable toOpen){
		if(toOpen instanceof InspectableList){
			InspectableList<Inspectable> list = (InspectableList<Inspectable>) toOpen;
			if(list.size() == 1) toOpen = list.get(0);
		}
		if(toOpen != current){
			if(currentHistoryIndex >= 0 && currentHistoryIndex < history.size() - 1){
				history.subList(currentHistoryIndex + 1, history.size()).clear();
			}
			history.add(toOpen);
			currentHistoryIndex = history.size() - 1;
		}
		load();
	}
	
	public void close(){
		history.clear();
		currentHistoryIndex = -1;
		load();
	}
	
	public void forward(){
		if(canForward()){
			currentHistoryIndex ++;
			load();
		}
	}
	
	public boolean canForward(){
		return currentHistoryIndex < history.size() - 1;
	}
	
	public void backward(){
		if(canBackward()){
			currentHistoryIndex --;
			load();
		}
	}
	
	public boolean canBackward(){
		return currentHistoryIndex > 0;
	}

	@Override
	public void hr() {
		content.row();
		Image hr = new Image(HR_ICON);
		hr.setScaling(Scaling.stretch);
		content.add(hr).colspan(content.getColumns()).growX().height(HR_SIZE / 2);
		content.row();
	}

	@Override
	public Table getInspectionTable() {
		return content;
	}


	public static class Link extends Table{
		
		public Inspectable linkTo;
		
		public Link(Inspectable linkTo){
			this.linkTo = linkTo;
			addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					InternalBrowser.INSTANCE.open(linkTo);
				}
				
			});
		}
		
		public Link(Inspectable linkTo, String text){
			this(linkTo, new TextButton(text, BankCity.SKIN, "link"));
		}
		
		public Link(Inspectable linkTo, DynamicString text){
			this(linkTo, new TextButton(text.toString(), BankCity.SKIN, "link"){
				@Override
				public String getText(){
					return text.toString();
				}
			});
		}
		
		public Link(Inspectable linkTo, Actor content){
			this(linkTo);
			add(content).grow();
		}
		
		@Override
		public void setColor(Color color){
			for(Actor actor : getChildren()){
				if(actor instanceof TextButton){
					TextButton button = (TextButton) actor;
					TextButtonStyle style = new TextButtonStyle(button.getStyle());
					style.fontColor = new Color(color).mul(Color.GRAY);
					style.overFontColor = new Color(color).mul(Color.LIGHT_GRAY);
					style.downFontColor = new Color(color).mul(Color.DARK_GRAY);
					button.setStyle(style);
				}
				else actor.setColor(color);
			}
		}
		
	}
	
	public static class InspectableList<T extends Inspectable> extends ArrayList<T> implements Inspectable{
		private static final long serialVersionUID = 1L;
		
		public String title;
		
		public InspectableList(String title){
			this.title = title;
		}

		@Override
		public void createInspectionUI(Inspector inspector) {
			Table table = inspector.getInspectionTable();
			
			if(size() == 0){
				Label label = new Label("No Elements", BankCity.SKIN);
				table.add(label).expandX().center();
			}
			
			boolean first = true;
			for(T element : this){
				if(!first) table.row();
				Link link = new Link(element, element.getTitle());
				table.add(link).growX();
				first = false;
			}
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public Color getColor() {
			return Color.WHITE;
		}
		
	}
	
}
