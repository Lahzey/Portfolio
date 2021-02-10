package bankcity.ui;

import com.badlogic.gdx.graphics.Color;

public interface Inspectable {

	public void createInspectionUI(Inspector inspector);
	
	public String getTitle();
	
	public Color getColor();
	
}
