package com.creditsuisse.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MathUtil {

	/**
	 * Levels the given double (toLevel) to the other double (level).
	 * <br/>Examples:
	 * <ul>
	 * <li><code>level(27, 5)</code> -> 25</li>
	 * <li><code>level(-347.62, 0.2)</code> -> -347.8</li>
	 * </ul>
	 * @param toLevel the double to be leveled.
	 * @param level the double that defines the level.
	 * @return toLevel reduced to the next lower number that divided by the level will give an integer (no decimals).
	 */
	public static double level(double toLevel, double level){
		double diff = toLevel % level;
		if(diff >= 0){
			return toLevel - diff;
		}else{
			return toLevel - level - diff;
		}
	}
	
	/**
	 * Generates a pseudo random integer from the given range.
	 * @param random the Random instance to use (allows seeded generation).
	 * @param min the minimal value (inclusive).
	 * @param max the maximum value (inclusive).
	 * @return a number between or at the given min and max.
	 */
	public static int randomIntFromRange(Random random, int min, int max){
		return random.nextInt(max - min + 1) + min;
	}
	
	/**
	 * Generates a pseudo random float from the given range.
	 * @param random the Random instance to use (allows seeded generation).
	 * @param min the minimal value (inclusive).
	 * @param max the maximum value (inclusive).
	 * @return a number between or at the given min and max.
	 */
	public static float randomFloatFromRange(Random random, float min, float max){
		float dif = max - min;
		return random.nextFloat() * dif + min;
	}
	
	/**
	 * Randomly selects an object from the given map.
	 * <br/>The chance of being selected is the value in the map, the object itself is the key.
	 * The chance is relative to the total chance of all objects in the map.
	 * <br/>Example: o1/0.2, o2/0.2, o3/0.6, o4/1.0 -> 1.0 will result in a 50% chance because the total is 2.0.
	 * @param random the Random instance to use (allows seeded generation).
	 * @param objectChances the mapping where the key is a possible returned object and the value is the chance of being returned.
	 * @return a key from the given map or null if the map is empty or all objects have a chance of 0 or lower.
	 */
	public static <T> T randomObjectByChance(Random random, Map<T, Float> objectChances){
		float totalChance = 0;
		Map<T, Float> selectionRanges = new HashMap<T, Float>();
		for(T object : objectChances.keySet()){
			float chance = objectChances.get(object);
			if(chance > 0){
				selectionRanges.put(object, totalChance);
				totalChance += chance;
			}
		}
		
		float selection = random.nextFloat() * totalChance;
		for(T object : selectionRanges.keySet()){
			float selectionRange = selectionRanges.get(object);
			if(selection > selectionRange && selection < selectionRange + objectChances.get(object)) return object;
		}
		return null;
	}
	
}
