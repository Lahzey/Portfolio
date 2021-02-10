package bankcity.gamelogic.buildings;

import com.badlogic.gdx.graphics.Color;

public class BuildingSector {
	
	public static final BuildingSector RESIDENTIAL;
	public static final BuildingSector BUSINESS;
	public static final BuildingSector SERVICES;
	public static final BuildingSector LEISURE;

	public final String name;
	public final String description;
	public final Color color;
	
	static{
		RESIDENTIAL = new BuildingSector("Residential", "Provides homes for your habitants.", new Color(0f, 0.5f, 0f, 1f));
		BUSINESS = new BuildingSector("Business", "Provides jobs while also making profit.", new Color(0.8f, 0.7f, 0f, 1f));
		SERVICES = new BuildingSector("Services", "Provides some jobs but typically costs alot. Still a must for a big city.", new Color(0.3f, 0.3f, 01f, 1f));
		LEISURE = new BuildingSector("Leisure", "Makes your city attractive at relatively low costs. Optional but worth it most of the times.", new Color(0.5f, 0.5f, 0.5f, 1f));
	}
	
	
	private BuildingSector(String name, String description, Color color){
		this.name = name;
		this.description = description;
		this.color = color;
	}
	
}
