package bankcity.ui.stages;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import bankcity.BankCity;
import bankcity.gamelogic.Game;
import bankcity.ui.BuildMenu;
import bankcity.ui.BuildingInspectionMenu;
import bankcity.ui.InternalBrowser;
import bankcity.ui.ShadowLabel;
import bankcity.ui.StatMenu;
import bankcity.ui.control.BuildingCreator;
import bankcity.ui.control.BuildingInspector;
import bankcity.ui.control.CameraController;

public class InGameStage extends Stage{
	
	public static final float SMALL_PADDING = 5;
	public static final float MEDIUM_PADDING = 10;
	public static final float LARGE_PADDING = 20;

	protected BankCity bankCity;
	public Game game;
	
	public CameraController camController;
	public BuildingCreator buildingCreator;
	public BuildingInspector buildingInspector;
	
	private InputProcessor inputProcessor;

	private Table root;
	
	//Normal UI
	public Table normalUI;
	public StatMenu topInterface;
	public BuildMenu bottomInterface;
	public Table centerInterface;
	public BuildingInspectionMenu inspectionInterface;
	
	//Build mode UI
	private Table buildModeUI;
	private Label buildModeMessage;
	private boolean buildModeEnabled = false;
	
	
	
	public InGameStage(BankCity bankCity){
		super(new ScreenViewport());
		this.bankCity = bankCity;
		initGame();
		initInterface();
	}
	
	public InputProcessor getInputProcessor(){
		return inputProcessor;
	}
	
	private void initGame(){
		game = new Game(new SpriteBatch());
		
		buildingCreator = new BuildingCreator(this);
		
		camController = new CameraController(game.renderingSystem, game.tiledMapRenderable);
		game.inputSystem.listeners.add(camController);
		buildingInspector = new BuildingInspector(this);
		game.inputSystem.listeners.add(buildingInspector);
		
		inputProcessor = new InputMultiplexer(this, buildingCreator, game.inputSystem);
	}
	
	
	private void initInterface(){
		
		
		root = new Table();
		root.setFillParent(true);
		root.top();
		
		//Normal UI
		normalUI = new Table();
		topInterface = new StatMenu(this);
		normalUI.add(topInterface).width(StatMenu.WIDTH).height(StatMenu.HEIGHT);
		normalUI.row();
		
		centerInterface = new Table();
		inspectionInterface = new BuildingInspectionMenu();
		centerInterface.add(inspectionInterface).expand().top().left();
		normalUI.add(centerInterface).padTop(LARGE_PADDING).padBottom(LARGE_PADDING).grow();
		normalUI.row();
		
		bottomInterface = new BuildMenu(this);
		bottomInterface.bottom();
		normalUI.add(bottomInterface).width(BuildMenu.WIDTH).height(BuildMenu.HEIGHT);
		
		
		//Build Mode UI
		buildModeUI = new Table();
		buildModeMessage = new ShadowLabel("BUILD MODE\n" + Keys.toString(buildingCreator.EXIT_KEY) + " to exit", BankCity.SKIN, "title");
		buildModeMessage.setColor(Color.WHITE);
		buildModeUI.add(buildModeMessage).padTop(MEDIUM_PADDING);

		root.add(normalUI).grow();

		Stack overlayStack = new Stack();
		overlayStack.setFillParent(true);
		overlayStack.add(root);
		Table browserContainer = new Table();
		browserContainer.setFillParent(true);
		browserContainer.add(InternalBrowser.INSTANCE).width(Value.percentWidth(0.5f, browserContainer)).height(Value.percentHeight(0.5f, browserContainer));
		overlayStack.add(browserContainer);
		
		addActor(overlayStack);
	}
	
	public void setBuildModeEnabled(boolean enabled){
		if(enabled){
			//toggle on
			root.clearChildren();
			root.add(buildModeUI);
		}else{
			//toggle off
			root.clearChildren();
			root.add(normalUI).grow();
		}
		buildModeEnabled  = enabled;
	}
	
	public boolean isBuildModeEnabled(){
		return buildModeEnabled;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		camController.update(delta);
		buildingCreator.update(delta);
		game.step(delta);
	}
	
	public void dispose(){
		game.dispose();
	}
	
}
