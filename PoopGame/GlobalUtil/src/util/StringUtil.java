package util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {

	public static String join(CharSequence delimiter, CharSequence... elements) {
		return join(delimiter, Arrays.asList(elements));
	}
	
	public static String join(CharSequence delimiter, Collection<CharSequence> elements) {
		if(elements != null && delimiter != null){
			StringBuilder builder = null;
			for(CharSequence element : elements){
				if(element == null) element = "null";
				if(builder == null){
					builder = new StringBuilder(element);
				}else{
					builder.append(delimiter);
					builder.append(element);
				}
			}
			return builder.toString();
		}else{
			throw new IllegalArgumentException("delimiter and elements may not be null");
		}
	}
	
	public static String capitalizeAt(String string, int index){
		return setCapitalizedAt(string, index, true);
	}
	
	public static String setCapitalizedAt(String string, int index, boolean capitalized){
		return string.substring(0, index) + (capitalized ? string.substring(index, index + 1).toUpperCase() : string.substring(index, index + 1).toLowerCase()) + string.substring(index + 1);
	}
	
	public static String insertAt(String insertInto, String toInsert, int index){
		return insertInto.substring(0, index) + toInsert + insertInto.substring(index);
	}
	
	public static String insertMultiple(String insertInto, List<Insertion> insertions){
		Collections.sort(insertions);
		int indexPlus = 0;
		for(Insertion insertion : insertions){
			insertInto = insertAt(insertInto, insertion.toInsert, insertion.index + indexPlus);
			indexPlus += insertion.toInsert.length();
		}
		return insertInto;
	}
	
	public static String insertMultiple(String insertInto, Insertion... insertions){
		return insertMultiple(insertInto, Arrays.asList(insertions));
	}
	
	public static long toBytes(String filesize) {
	    long returnValue = -1;
	    Pattern patt = Pattern.compile("([\\d.]+)([GMK]?B)", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = patt.matcher(filesize);
	    Map<String, Integer> powerMap = new HashMap<String, Integer>();
	    powerMap.put("GB", 3);
	    powerMap.put("MB", 2);
	    powerMap.put("KB", 1);
	    powerMap.put("B", 0);
	    if (matcher.find()) {
	      String number = matcher.group(1);
	      int pow = powerMap.get(matcher.group(2).toUpperCase());
	      BigDecimal bytes = new BigDecimal(number);
	      bytes = bytes.multiply(BigDecimal.valueOf(1024).pow(pow));
	      returnValue = bytes.longValue();
	    }
	    return returnValue;
	}
	
	public static class Insertion implements Comparable<Insertion>{
		public final String toInsert;
		public final int index;
		public Insertion(String toInsert, int index){
			this.toInsert = toInsert;
			this.index = index;
		}
		
		@Override
		public int compareTo(Insertion o) {
			return index - o.index;
		}
	}
}
