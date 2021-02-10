package bankcity.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HorizontalRule extends Actor{

	private ShapeRenderer shapeRenderer;
	
	public HorizontalRule(){
		this(1);
	}
	
	public HorizontalRule(float thickness){
		this(thickness, Color.BLACK);
	}
	
	public HorizontalRule(float thickness, Color color){
		shapeRenderer = new ShapeRenderer();
		setThickness(thickness);
		setColor(color);
	}
	
	
	public void setThickness(float thickness){
		setHeight(thickness);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.begin(ShapeType.Filled);
		Color color = getColor().cpy();
		color.set(color.r, color.g, color.b, color.a * parentAlpha);
		shapeRenderer.setColor(color);
		shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
		shapeRenderer.end();
		batch.begin();
	}
	
	
}
