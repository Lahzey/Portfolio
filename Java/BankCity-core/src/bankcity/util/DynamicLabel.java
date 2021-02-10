package bankcity.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import bankcity.BankCity;

public class DynamicLabel extends Label{
	
	private DynamicString text;

	public DynamicLabel(DynamicString text) {
		super(text.toString(), BankCity.SKIN);
		this.text = text;
	}
	
	
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		setText(text.toString());
		super.draw(batch, parentAlpha);
	}



	public static interface DynamicString{
		public String toString();
	}

}
