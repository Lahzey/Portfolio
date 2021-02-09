package poopgame.graphics.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import poopgame.gamelogic.engine.VisualSystem;
import poopgame.graphics.components.AnimationComponent;
import poopgame.graphics.components.TextureComponent;

public class AnimationSystem extends IteratingSystem implements VisualSystem {

    private ComponentMapper<TextureComponent> tm;
    private ComponentMapper<AnimationComponent> am;
    
    private boolean enabled = true;

    public AnimationSystem(){
        super(Family.all(TextureComponent.class, AnimationComponent.class).get());
        tm = ComponentMapper.getFor(TextureComponent.class);
        am = ComponentMapper.getFor(AnimationComponent.class);
    }
    
    @Override
    public void update(float deltaTime) {
    	if (!enabled) {
    		return;
    	}
    	
    	super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent ani = am.get(entity);
        TextureComponent tex = tm.get(entity);
        TextureRegion frame = ani.getKeyFrame(deltaTime);
        if (frame != null) tex.region = frame;
    }

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
