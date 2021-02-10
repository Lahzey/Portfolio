package logic;

import java.awt.Color;

public enum Status {

	STOPPED(new Color(206, 206, 206), 0), RUNNING(new Color(0, 109, 3), 1), STARTING(new Color(232, 188, 30), 2), ERROR(new Color(175, 1, 1), 3);
	
	private final Color color;
	private final int priority;
	
	private Status(Color color, int priority){
		this.color = color;
		this.priority = priority;
	}
	
	public Color getColor(){
		return color;
	}
	
	public int getPriority(){
		return priority;
	}
}
