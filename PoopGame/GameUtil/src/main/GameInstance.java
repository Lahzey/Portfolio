package main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import data.TextureManager;
import graphics.systems.AnimationSystem;
import graphics.systems.RenderingSystem;
import graphics.ui.LoadingStage;
import physics.systems.PhysicsDebugSystem;
import physics.systems.PhysicsSystem;

public abstract class GameInstance extends Game {
	
	private static GameInstance CURRENT_INSTANCE;
	
	// Main Info
	private String name;
	private Skin skin;
	private TextureManager textureManager;
	private Stage stage;
	private World world;
	private PooledEngine engine;
	
	// Systems
	private RenderingSystem renderingSystem;
	private AnimationSystem animationSystem;
	private PhysicsSystem physicsSystem;
	private PhysicsDebugSystem physicsDebugSystem;

	// Scheduled operations
	private final List<Runnable> invokeLater = new ArrayList<>();
	private final List<Runnable> invokationQueue = new ArrayList<>();
	
	public GameInstance(String name, Skin skin){
		this.name = name;
		setSkin(skin);
		setTextureManager(new TextureManager());
		CURRENT_INSTANCE = this;
	}
	
	protected abstract void loadAssets(TextureManager textureManager);
	protected abstract Stage createInGameStage();
	protected abstract World createWorld();

	@Override
	public void create () {
		// display loading stage
		LoadingStage loadingStage = new LoadingStage(this);
		setStage(loadingStage);
		
		// do the loading
		new Thread(){
			public void run(){
				loadAssets(textureManager);
				
				world = createWorld();
				
				// create engine and systems
				engine = new PooledEngine();
				renderingSystem = new RenderingSystem(new SpriteBatch());
				
				// switch to in-game stage
				setStage(createInGameStage());
			}
		}.start();
	}
	

	@Override
	public void render () {
		super.render();
		float deltaTime = Gdx.graphics.getDeltaTime();
		if(getStage() != null){
			getStage().act(deltaTime);
			getStage().draw();
		}
		synchronized (invokeLater) {
			invokationQueue.addAll(invokeLater);
			invokeLater.clear();
		}
		for(Runnable runnable : invokationQueue) runnable.run();
		invokationQueue.clear();
		if(engine != null){
			engine.update(deltaTime);
		}
	}
	
	@Override
	public void resize(int width, int height) {
		if(renderingSystem != null){
			renderingSystem.resize(width, height);
		}
		if(getStage() != null){
			getStage().getViewport().update(width, height, true);
		}
	}

	public void invokeLater(Runnable runnable){
		invokeLater.add(runnable);
	}
	
	public void step(float deltaTime){
		synchronized (invokeLater) {
			invokationQueue.addAll(invokeLater);
			invokeLater.clear();
		}
		engine.update(deltaTime);
		for(Runnable runnable : invokationQueue) runnable.run();
		invokationQueue.clear();
	}
	
	public TextureRegion getImage(String internalPath){
		return new TextureRegion(getTextureManager().getTexture(internalPath));
	}
	
	public TextureRegion getIcon(String name){
		return new TextureRegion(getTextureManager().getTexture(FileLocations.ICONS + "/" + name));
	}
	
	public static GameInstance current(){
		return CURRENT_INSTANCE;
	}
	
	public Skin getSkin(){
		return skin;
	}
	public void setSkin(Skin skin){
		this.skin = skin;
	}
	public TextureManager getTextureManager(){
		return textureManager;
	}
	public void setTextureManager(TextureManager textureManager){
		this.textureManager = textureManager;
	}
	public Stage getStage(){
		return stage;
	}
	public void setStage(Stage stage){
		this.stage = stage;
	}
	public String getName(){
		return name;
	}
}
