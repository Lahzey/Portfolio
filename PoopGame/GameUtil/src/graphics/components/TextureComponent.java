package graphics.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Created by barry on 12/8/15 @ 8:30 PM.
 */
public class TextureComponent implements Component, Poolable {
    public TextureRegion region = null;
	public final Color tint = new Color(Color.WHITE);
	public float opacity = 1f;
    public boolean isHidden = false;
    public final Vector2 size = new Vector2(0, 0);

	@Override
	public void reset() {
		region = null;
		tint.set(Color.WHITE);
		opacity = 1f;
		isHidden = false;
		size.set(0, 0);
	}
}
