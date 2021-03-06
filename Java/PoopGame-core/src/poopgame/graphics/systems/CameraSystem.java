package poopgame.graphics.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import poopgame.gamelogic.engine.VisualSystem;
import poopgame.graphics.components.TransformComponent;

public class CameraSystem extends IteratingSystem implements VisualSystem {
	private static boolean ENABLED = false;

	private static final float MIN_WIDTH = 10f;
	private static final float MIN_HEIGHT = 5f;
	private static final float PADDING = 4f;
	private static final float MAX_CHANGE_SPEED = 50f; // in meters per second
	
	private final RenderingSystem renderingSystem;
	private final Rectangle bounds = new Rectangle();
	
    private final ComponentMapper<TransformComponent> transformMapper;
    
    private final List<Vector3> mustRenderPositions = new ArrayList<>();

	private boolean enabled = true;
    
    public CameraSystem(RenderingSystem renderingSystem) {
        super(Family.all(TransformComponent.class).get());
        this.renderingSystem = renderingSystem;
        
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    public void update(float deltaTime) {
    	if (!enabled) {
    		return;
    	}
    	
    	mustRenderPositions.clear();
    	super.update(deltaTime);

    	float minX = Float.MAX_VALUE;
    	float minY = Float.MAX_VALUE;
    	float maxX = Float.MIN_VALUE;
    	float maxY = Float.MIN_VALUE;
    	
    	if(!mustRenderPositions.isEmpty() && ENABLED){
        	// get smallest and biggest coordinate of all players
        	for(Vector3 position : mustRenderPositions){
        		if(position.x < minX) minX = position.x;
        		if(position.y < minY) minY = position.y;
        		if(position.x > maxX) maxX = position.x;
        		if(position.y > maxY) maxY = position.y;
        	}
        	
        	// add padding
        	minX -= PADDING;
        	minY -= PADDING;
        	maxX += PADDING;
        	maxY += PADDING;
        	

        	// handle maximum size
        	float maxBoundsX = bounds.x + bounds.width;
        	float maxBoundsY = bounds.y + bounds.height;
        	minX = Math.max(bounds.x, Math.min(maxBoundsX, minX));
        	minY = Math.max(bounds.y, Math.min(maxBoundsY, minY));
        	maxX = Math.max(bounds.x, Math.min(maxBoundsX, maxX));
        	maxY = Math.max(bounds.y, Math.min(maxBoundsY, maxY));

        	// handle minimum on X-Axis
        	float deltaX = maxX - minX;
        	if (deltaX < MIN_WIDTH) {
        		float changePerSide = MIN_WIDTH / 2;
        		
    			minX -= changePerSide;
        		if (minX < bounds.x) {
        			minX = bounds.x;
        			changePerSide = MIN_WIDTH - (maxX - minX);
        		}

    			maxX += changePerSide;
        		if (maxX > maxBoundsX) {
        			maxX = maxBoundsX;
        			changePerSide = MIN_WIDTH - (maxX - minX);
        			
        			// try to change minX again with leftovers
        			minX -= changePerSide;
            		if (minX < bounds.x) {
            			minX = bounds.x;
            			changePerSide = MIN_WIDTH - (maxX - minX);
            		}
        		}
        	}

        	// handle minimum on Y-Axis
        	float deltaY = maxY - minY;
        	if (deltaY < MIN_HEIGHT) {
        		float changePerSide = MIN_HEIGHT / 2;
        		
    			minY -= changePerSide;
        		if (minY < bounds.y) {
        			minY = bounds.y;
        			changePerSide = MIN_HEIGHT - (maxY - minY);
        		}

    			maxY += changePerSide;
        		if (maxY > maxBoundsY) {
        			maxY = maxBoundsY;
        			changePerSide = MIN_HEIGHT - (maxY - minY);
        			
        			// try to change minY again with leftovers
        			minY -= changePerSide;
            		if (minY < bounds.y) {
            			minY = bounds.y;
            			changePerSide = MIN_HEIGHT - (maxY - minY);
            		}
        		}
        	}
    	} else {
    		minX = bounds.x;
    		minY = bounds.y;
    		maxX = minX + bounds.width;
    		maxY = minY + bounds.height;
    	}
    	
    	// smooth transition
    	progressTowards(renderingSystem.mustRender, minX, minY, maxX, maxY, deltaTime);
    }
    
    private static void progressTowards(Rectangle rect, float minX, float minY, float maxX, float maxY, float deltaTime) {
    	float maxTransition = MAX_CHANGE_SPEED * deltaTime;
    	float maxTransitionNegative = maxTransition * -1;
    	
    	float currentMinX = rect.x;
    	float currentMinY = rect.y;
    	float currentMaxX = currentMinX + rect.width;
    	float currentMaxY = currentMinY + rect.height;
    	float minXDif = minX - currentMinX;
    	float minYDif = minY - currentMinY;
    	float maxXDif = maxX - currentMaxX;
    	float maxYDif = maxY - currentMaxY;
    	
    	if (minXDif < 0) {
    		if (minXDif < maxTransitionNegative) minXDif = maxTransitionNegative;
    	} else {
    		if (minXDif > maxTransition) minXDif = maxTransition;
    	}

    	if (minYDif < 0) {
    		if (minYDif < maxTransitionNegative) minYDif = maxTransitionNegative;
    	} else {
    		if (minYDif > maxTransition) minYDif = maxTransition;
    	}

    	if (maxXDif < 0) {
    		if (maxXDif < maxTransitionNegative) maxXDif = maxTransitionNegative;
    	} else {
    		if (maxXDif > maxTransition) maxXDif = maxTransition;
    	}

    	if (maxYDif < 0) {
    		if (maxYDif < maxTransitionNegative) maxYDif = maxTransitionNegative;
    	} else {
    		if (maxYDif > maxTransition) maxYDif = maxTransition;
    	}
    	
    	rect.x += minXDif;
    	rect.y += minYDif;
    	rect.width = (currentMaxX + maxXDif) - rect.x;
    	rect.height = (currentMaxY + maxYDif) - rect.y;
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComp = transformMapper.get(entity);
        if (transformComp.mustBeInFrame) {
            mustRenderPositions.add(transformComp.position);
        }
    }

    public void setBounds(Rectangle bounds){
    	this.bounds.set(bounds);
    }

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
    
}
