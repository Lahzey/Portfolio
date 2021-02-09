package poopgame.data;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import poopgame.util.GifDecoder;

public class TextureManager extends AssetManager {
	
	public Map<String, Animation<TextureRegion>> loadedGifs = new HashMap<>();
	
	public Animation<TextureRegion> getAnimation(String fileName, Animation.PlayMode playMode){
		if(!loadedGifs.containsKey(fileName)){
			loadedGifs.put(fileName, GifDecoder.loadGIFAnimation(playMode, Gdx.files.internal(fileName).read()));
		}
		return loadedGifs.get(fileName);
	}
	
	public Texture getTexture(String fileName){
		if(!isLoaded(fileName)){
			load(fileName, Texture.class);
			finishLoadingAsset(fileName);
		}
		return get(fileName, Texture.class);
	}
}
