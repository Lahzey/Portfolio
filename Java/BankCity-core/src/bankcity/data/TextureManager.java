package bankcity.data;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureManager extends AssetManager{
	
	public Animation<TextureRegion> getAnimation(String fileName, int rows, int columns, float frameDuration){
		//Load texture
		Texture texture = getTexture(fileName);
		
		//Split into 2D array
		TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / columns, texture.getHeight() / rows);
		
		//Convert to 1D array
		TextureRegion[] frames = new TextureRegion[rows * columns];
		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				frames[index++] = tmp[i][j];
			}
		}
		
		//Create animation and return it
		Animation<TextureRegion> animation = new Animation<TextureRegion>(frameDuration, frames);
		animation.setPlayMode(PlayMode.LOOP);
		return animation;
	}
	
	public Texture getTexture(String fileName){
		if(!isLoaded(fileName)){
			load(fileName, Texture.class);
			finishLoadingAsset(fileName);
		}
		return get(fileName, Texture.class);
	}
}
