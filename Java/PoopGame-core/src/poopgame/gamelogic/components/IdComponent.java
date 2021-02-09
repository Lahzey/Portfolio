package poopgame.gamelogic.components;

import com.badlogic.ashley.core.Component;

public class IdComponent implements Component {
	
	public String id;
	
	public IdComponent() {}
	
	public IdComponent(String id) {
		this.id = id;
	}

}
