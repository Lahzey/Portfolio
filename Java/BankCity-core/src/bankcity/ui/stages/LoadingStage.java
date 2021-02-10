package bankcity.ui.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import bankcity.BankCity;
import bankcity.data.FileLocations;
import bankcity.util.AnimatedDrawable;
import bankcity.util.GifDecoder;

public class LoadingStage extends Stage{

	
	
	public LoadingStage(BankCity bankCity){
		super(new ScreenViewport());
		Table table = new Table();
		table.center();
		table.setFillParent(true);
		table.setBackground(new TextureRegionDrawable(new TextureRegion(BankCity.TEXTURE_MANAGER.getTexture(FileLocations.UI + "/white.png"))));
		
		Label label = new Label("LOADING GAME", BankCity.SKIN, "title");
		table.add(label);
		table.row();
		
		Image loadingImage = new Image(new AnimatedDrawable(GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal(FileLocations.UI + "/loading.gif").read())));
		table.add(loadingImage).width(50).height(50);
		
		addActor(table);
	}

	@Override
	public void draw() {
		
		super.draw();
	}

	@Override
	public void act() {
		super.act();
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}
	
	
}
