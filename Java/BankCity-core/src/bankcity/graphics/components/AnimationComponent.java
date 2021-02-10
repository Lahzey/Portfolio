package bankcity.graphics.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

public class AnimationComponent implements Component, Poolable{

	/** Animation to be drawn */
	public Animation<TextureRegion> animation;
	/** Defines what is drawn first. The lower this value, the earlier it is drawn. Graphics at the same location with a higher z will draw over this one. */
	
	
	//For how long the animation is playing (used to determine current frame)
	protected float elapsedTime;

	@Override
	public void reset() {
		animation = null;
		elapsedTime = 0;
	}
	
	/**
	 * Advances the animation by the given deltaTime and then returns the current key frame
	 * @param deltaTime the time to advance the animation by
	 * @return the current frame
	 */
	public TextureRegion getKeyFrame(float deltaTime){
		if(animation != null){
			elapsedTime += deltaTime;
			return animation.getKeyFrame(elapsedTime);
		}else return null;
	}
}
