package bankcity.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class ColorDrawable extends BaseDrawable{
	
	public Color fillColor;
	public Color borderColor;
	public float borderThickness;
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	
	public ColorDrawable(Color fillColor){
		this(fillColor, null);
	}
	
	public ColorDrawable(Color fillColor, Color borderColor){
		this(fillColor, borderColor, 1);
	}
	
	public ColorDrawable(Color fillColor, Color borderColor, float borderThickness){
		this.fillColor = fillColor;
		this.borderColor = borderColor;
		this.borderThickness = borderThickness;
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		batch.end();
		if(fillColor != null){
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(fillColor);
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
			shapeRenderer.rect(x, y, width, height);
			shapeRenderer.end();
		}
		if(borderColor != null){
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(borderColor);
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
			float halfThickness = borderThickness / 2;
			float x2 = x + width;
			float y2 = y + height;
			shapeRenderer.rectLine(x + halfThickness, y, x + halfThickness, y2, borderThickness);
			shapeRenderer.rectLine(x, y2 - halfThickness, x2, y2 - halfThickness, borderThickness);
			shapeRenderer.rectLine(x2 - halfThickness, y2, x2 - halfThickness, y, borderThickness);
			shapeRenderer.rectLine(x2, y + halfThickness, x, y + halfThickness, borderThickness);
			shapeRenderer.end();
		}
		batch.begin();
	}

}
