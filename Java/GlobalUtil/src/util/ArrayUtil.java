package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ArrayUtil {

	/**
	 * @param array the array to get an element from
	 * @return a random element from the given array
	 */
	public static <T> T randomElementFrom(T... array){
		if(array.length == 0) return null;
		int randomIndex = new Random().nextInt(array.length);
		return array[randomIndex];
	}

	/**
	 * Converts the given 2 dimensional array to a 2 dimensional list (list containing lists).
	 * @param array2D the array to convert
	 * @return a 2 dimensional list with the same elements as given array
	 */
	public static <T> List<T> array2DToList(T[][] array2D) {
		List<T> result = new ArrayList<T>();
		for (int i = 0; i < array2D.length; i++) {
			for (int ii = 0; ii < array2D[i].length; ii++) {
				result.add(array2D[i][ii]);
			}
		}
		return result;
	}
	
	/**
	 * Concatenates the given arrays to one array.
	 * <br/><b>Note:</b> Duplicates will not be eliminated!
	 * @param arrays the arrays to concatenate
	 * @return an array containing all elements
	 */
	public static <T> T[] concat(T[]... arrays){
		List<T> result = new ArrayList<T>();
		for(T[] array : arrays) result.addAll(Arrays.asList(array));
		return result.toArray(arrays[0]);
	}

}
