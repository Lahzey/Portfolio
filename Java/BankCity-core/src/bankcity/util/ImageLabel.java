package bankcity.util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import bankcity.BankCity;
import bankcity.ui.stages.InGameStage;

public class ImageLabel extends Table{
	
	private static final float DEFAULT_PADDING = InGameStage.SMALL_PADDING;

	private Label text;
	private Image image;
	private float padding;
	
	public ImageLabel(String text, TextureRegion image, ImagePosition position){
		this(text, image, DEFAULT_PADDING, position);
	}
	
	public ImageLabel(String text, TextureRegion image, float padding, ImagePosition position){
		this(new Label(text, BankCity.SKIN), new Image(new TextureRegionDrawable(image)), padding, position);
	}
	
	public ImageLabel(Label text, Image image, float padding, ImagePosition position){
		this.text = text;
		this.image = image;
		this.padding = padding;
		image.setScaling(Scaling.fit);
		switch(position){
			case BEFORE:
				add(image).left().prefHeight(Value.percentHeight(1f, text));
				add(text).left().padLeft(padding);
				break;
			case AFTER:
				add(text).left();
				add(image).left().padLeft(padding).prefHeight(Value.percentHeight(1f, text));
				break;
			default:
				add(text).left();
				setBackground(image.getDrawable());
				break;
		}
	}
	
	@Override
	public float getPrefHeight(){
		return text.getPrefHeight() + getPadTop() + getPadBottom();
	}
	
	@Override
	public float getPrefWidth(){
		return text.getPrefWidth() + getImageWidth() + padding + getPadLeft() + getPadRight();
	}
	
	private float getImageHeight(){
		return text.getPrefHeight();
	}
	
	private float getImageWidth(){
		return image.getWidth() / (image.getHeight() / getImageHeight());
	}
	
	public Image getImage(){
		return image;
	}
	
	public Label getText(){
		return text;
	}
	
	
	public static enum ImagePosition{
		BEFORE, AFTER, BACKGROUND;
	}

}
