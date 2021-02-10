package bankcity.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import bankcity.data.TextureManager;

public class Scene2DUtil {

	
	
	public static ImageButton createImageButton(Texture imageNormal, Texture imageHover, Texture imagePressed, Texture imageDisabled){
		ImageButtonStyle style = new ImageButtonStyle();
		style.imageUp = textureToDrawable(imageNormal);
		if(imagePressed != null) style.imageDown = textureToDrawable(imagePressed);
		if(imageHover != null) style.imageOver = textureToDrawable(imageHover);
		if(imageDisabled != null) style.imageDisabled = textureToDrawable(imageDisabled);
		return new ImageButton(style);
	}
	
	public static ImageButton createImageButton(TextureManager textureManager, String imageNormal, String imageHover, String imagePressed, String imageDisabled){
		return createImageButton(textureManager.getTexture(imageNormal), textureManager.getTexture(imageHover), textureManager.getTexture(imagePressed), textureManager.getTexture(imageDisabled));
	}
	
	public static ImageButton createImageButton(TextureManager textureManager, String imagePath){
		return createImageButton(textureManager, imagePath, "png");
	}
	
	public static ImageButton createImageButton(TextureManager textureManager, String imagePath, String type){
		String suffix = "." + type;
		return createImageButton(textureManager.getTexture(imagePath + suffix), getIfExists(textureManager, imagePath + "_hover" + suffix), getIfExists(textureManager, imagePath + "_pressed" + suffix), getIfExists(textureManager, imagePath + "_disabled" + suffix));
	}
	
	private static Texture getIfExists(TextureManager textureManager, String internal){
		if(Gdx.files.internal(internal).exists()){
			return textureManager.getTexture(internal);
		}else{
			return null;
		}
	}
	
	public static TextureRegionDrawable textureToDrawable(Texture texture){
		return new TextureRegionDrawable(new TextureRegion(texture));
	}
	
	public static boolean insideActor(Actor actor, float x, float y){
		x -= actor.getX();
		y -= actor.getY();
		return x >= 0 && x < actor.getWidth() && y >= 0 && y < actor.getHeight();
	}
}
