package bankcity.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public abstract class ClickableImageButton extends ImageButton{

	public ClickableImageButton(TextureRegion image) {
		super(new TextureRegionDrawable(image));
		addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				onClick();
			}
			
		});
	}
	
	public void setImage(TextureRegion image){
		getStyle().imageUp = new TextureRegionDrawable(image);
	}
	
	public abstract void onClick();

}
