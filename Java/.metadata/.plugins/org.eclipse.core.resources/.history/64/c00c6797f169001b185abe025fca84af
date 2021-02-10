package com.creditsuisse.util;

/**
 * An enum representing the 4 basic directions
 * @author A469627
 *
 */
public enum Direction {
	UP, DOWN, LEFT, RIGHT;
	
	/**
	 * @return the amount to modify the x by to move in that direction
	 */
	public int getXModifier(){
		switch(this){
		case LEFT:
			return -1;
		case RIGHT:
			return 1;
		default:
			return 0;
		}
	}
	
	/**
	 * @return the amount to modify the y by to move in that direction
	 */
	public int getYModifier(){
		switch(this){
		case UP:
			return -1;
		case DOWN:
			return 1;
		default:
			return 0;
		}
	}
}
