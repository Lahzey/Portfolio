package poopgame.graphics.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Created by barry on 12/8/15 @ 8:30 PM.
 */
public class TextureComponent implements Component, Poolable {
    public TextureRegion region = null;
	public Color tint;
	public float opacity = 1;
	
	public TextureComponent() {}

	@Override
	public void reset() {
		region = null;
	}
}
