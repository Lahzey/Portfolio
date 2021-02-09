package util;

import java.util.HashMap;
import java.util.Map;

public class IntegerUtil {
	
	private static final Map<Integer, String> TUPLE_PREFIXES = new HashMap<Integer, String>();
	
	static{
		TUPLE_PREFIXES.put(0, "");
		TUPLE_PREFIXES.put(1, "un");
		TUPLE_PREFIXES.put(2, "duo");
		TUPLE_PREFIXES.put(3, "tre");
		TUPLE_PREFIXES.put(4, "quatturo");
		TUPLE_PREFIXES.put(5, "quin");
		TUPLE_PREFIXES.put(6, "sex");
		TUPLE_PREFIXES.put(7, "septen");
		TUPLE_PREFIXES.put(8, "octo");
		TUPLE_PREFIXES.put(9, "novem");
	}

	/**
	 * Converts a number to its tuple name.
	 * <br/>1 -> single
	 * <br/>2 -> double
	 * <br/>3 -> triple
	 * <br/>4 -> quadruple
	 * <br/>...
	 * <br/>Any numbers without a mapping will return null. Mappings are from 1 to 100.
	 * @param number the number to convert
	 * @return the matching tuple name or null if there is no tuple name saved for this number
	 */
	public static String toTupleName(int number){
		if(number == 1) return "single";
		else if(number == 2) return "double";
		else if(number == 3) return "triple";
		else if(number == 4) return "quadruple";
		else if(number == 5) return "quintuple";
		else if(number == 6) return "sextuple";
		else if(number == 7) return "septuple";
		else if(number == 8) return "octuple";
		else if(number == 9) return "nontuple";
		else if(number > 20) return TUPLE_PREFIXES.get(number - 10) + "decuple";
		else if(number > 30) return TUPLE_PREFIXES.get(number - 20) + "vigintuple";
		else if(number > 40) return TUPLE_PREFIXES.get(number - 30) + "trigintuple";
		else if(number > 50) return TUPLE_PREFIXES.get(number - 40) + "quadragintuple";
		else if(number > 60) return TUPLE_PREFIXES.get(number - 50) + "quinquagintuple";
		else if(number > 70) return TUPLE_PREFIXES.get(number - 60) + "sexagintuple";
		else if(number > 80) return TUPLE_PREFIXES.get(number - 70) + "septuagintuple";
		else if(number > 90) return TUPLE_PREFIXES.get(number - 80) + "octogintuple";
		else if(number > 100) return TUPLE_PREFIXES.get(number - 90) + "nongentuple";
		else if(number == 100) return "centuple";
		else return null;
	}
}
