package poopgame.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import poopgame.gamelogic.Champion;

public class InternalAssetLoader {

	// TODO: fix asset mapping, as currently when getting asset it results in a black box

	private static final Map<String, Sound> SOUNDS = new HashMap<>();
	private static final Map<String, TextureRegion> TEXTURES = new HashMap<>();
	private static final Map<String, Animation<TextureRegion>> ANIMATIONS = new HashMap<>();
	
	private static boolean initialised = false;


	public static void initAssets() {
		if (initialised) {
			return;
		}
		
		for (Champion champ : Champion.values()) {
			String folderName = champ.getFolderName();
			InternalAssetLoader.getAnimation(folderName + "walk_left.gif");
			InternalAssetLoader.getAnimation(folderName + "walk_right.gif");
			InternalAssetLoader.getAnimation(folderName + "stand_left.gif");
			InternalAssetLoader.getAnimation(folderName + "stand_right.gif");
		}
		initialised = true;
	}
	
	public static boolean isInitialised() {
		return initialised;
	}

	public static Sound getSound(String path) {
		if (SOUNDS.containsKey(path)) {
			return SOUNDS.get(path);
		} else {
			Sound sound;
			try {
				sound = Gdx.audio.newSound(Gdx.files.internal(path));
			} catch (Throwable e) {
				sound = new EmptySound();
				System.err.println("Failed to load sound at " + path);
			}
			SOUNDS.put(path, sound);
			return sound;
		}
	}

	public static TextureRegion getTexture(String path) {
		System.out.println("getting texture " + path);
		if (TEXTURES.containsKey(path)) {
			return TEXTURES.get(path);
		} else {
			TextureRegion texture;
			try {
				texture = new TextureRegion(new Texture(path));
			} catch (Throwable e) {
				texture = new TextureRegion(new Texture(generateFailureImage()));
				System.err.println("Failed to load texture at " + path);
			}
			TEXTURES.put(path, texture);
			return texture;
		}
	}
	
	public static Animation<TextureRegion> getAnimation(String path) {
		return getAnimation(path, Animation.PlayMode.LOOP);
	}

	public static Animation<TextureRegion> getAnimation(String path, Animation.PlayMode playMode){
		String key = path + ":" + playMode;
		if (ANIMATIONS.containsKey(key)) {
			System.out.println("loading animation " + path + " from ram");
			return ANIMATIONS.get(key);
		} else {
			System.out.println("loading animation " + path + " from disk");
			Animation<TextureRegion> animation;
			try {
				animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal(path).read());
			} catch(Throwable e) {
				animation = new Animation<TextureRegion>(1, new TextureRegion(new Texture(generateFailureImage())));
				System.err.println("Failed to load animation at " + path);
			}
			ANIMATIONS.put(key, animation);
			return animation;
		}
	}

	private static Pixmap generateFailureImage() {
		Pixmap failureImage = generateColoredImage(Color.WHITE, 100, 100);
		failureImage.setColor(Color.RED);
		failureImage.drawRectangle(0, 0, 100, 100);
		failureImage.drawLine(0, 0, 100, 100);
		failureImage.drawLine(0, 100, 100, 0);
		return failureImage;
	}

	public static Pixmap generateColoredImage(Color color, int width, int height) {
		Pixmap image = new Pixmap(width, height, Format.RGB888);
		image.setColor(color);
		image.fill();
		return image;
	}

	private static class EmptySound implements Sound {

		@Override
		public long play() {
			// empty implementation
			return 0;
		}

		@Override
		public long play(float volume) {
			// empty implementation
			return 0;
		}

		@Override
		public long play(float volume, float pitch, float pan) {
			// empty implementation
			return 0;
		}

		@Override
		public long loop() {
			// empty implementation
			return 0;
		}

		@Override
		public long loop(float volume) {
			// empty implementation
			return 0;
		}

		@Override
		public long loop(float volume, float pitch, float pan) {
			// empty implementation
			return 0;
		}

		@Override
		public void stop() {
			// empty implementation

		}

		@Override
		public void pause() {
			// empty implementation

		}

		@Override
		public void resume() {
			// empty implementation

		}

		@Override
		public void dispose() {
			// empty implementation

		}

		@Override
		public void stop(long soundId) {
			// empty implementation

		}

		@Override
		public void pause(long soundId) {
			// empty implementation

		}

		@Override
		public void resume(long soundId) {
			// empty implementation

		}

		@Override
		public void setLooping(long soundId, boolean looping) {
			// empty implementation

		}

		@Override
		public void setPitch(long soundId, float pitch) {
			// empty implementation

		}

		@Override
		public void setVolume(long soundId, float volume) {
			// empty implementation

		}

		@Override
		public void setPan(long soundId, float pan, float volume) {
			// empty implementation

		}

	}

}
