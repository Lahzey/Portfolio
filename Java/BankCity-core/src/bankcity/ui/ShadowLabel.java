package bankcity.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ShadowLabel extends Label{
	
	public float offset = 1;
	public Color shadowColor;
	private Color tempColor = new Color();

	public ShadowLabel(CharSequence text, LabelStyle style) {
		super(text, style);
	}

	public ShadowLabel(CharSequence text, Skin skin, String fontName, Color color) {
		super(text, skin, fontName, color);
	}

	public ShadowLabel(CharSequence text, Skin skin, String fontName, String colorName) {
		super(text, skin, fontName, colorName);
	}

	public ShadowLabel(CharSequence text, Skin skin, String styleName) {
		super(text, skin, styleName);
	}

	public ShadowLabel(CharSequence text, Skin skin) {
		super(text, skin);
	}
	
	@Override
	public void setStyle(LabelStyle style) {
		super.setStyle(style);
		setColor(style.fontColor);
	};

	@Override
	public void draw(Batch batch, float parentAlpha) {
		BitmapFontCache cache = getBitmapFontCache();
		float x = cache.getX();
		float y = cache.getY();
		cache.setPosition(x + offset, y - offset);
		Color shadowColor = this.shadowColor;
		if(shadowColor == null){
			Color foreground = getColor();
			shadowColor = new Color(foreground.r * -1 + 1, foreground.g * -1 + 1, foreground.b * -1 + 1, foreground.a);
		}
		cache.tint(shadowColor);
		cache.draw(batch);
		cache.setPosition(x, y);
		
		validate();
		Color color = tempColor.set(getColor());
		color.a *= parentAlpha;
		if (getStyle().background != null) {
			batch.setColor(color.r, color.g, color.b, color.a);
			getStyle().background.draw(batch, getX(), getY(), getWidth(), getHeight());
		}
		cache.tint(getColor());
		cache.setPosition(getX(), getY());
		cache.draw(batch);
	}

	
	
}
