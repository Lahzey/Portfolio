package graphics.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import bankcity.graphics.components.AnimationComponent;
import bankcity.graphics.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {

    ComponentMapper<TextureComponent> tm;
    ComponentMapper<AnimationComponent> am;

    public AnimationSystem(){
        super(Family.all(TextureComponent.class, AnimationComponent.class).get());
        tm = ComponentMapper.getFor(TextureComponent.class);
        am = ComponentMapper.getFor(AnimationComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent ani = am.get(entity);
        TextureComponent tex = tm.get(entity);
        tex.region = ani.getKeyFrame(deltaTime);
    }
}
