package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StringFormatter {

	/**
	 * Formats the given number by rounding it to the number closest to it that can be divided by the given step.
	 * @param number the number to format
	 * @param smallestStep the step (5.78, step: 0.2 -> 5.8) as string to avoid floating point errors
	 * @return the formatted number as String
	 */
	public static String formatNumber(double number, String smallestStep){
		if(Double.isInfinite(number)) return "Infinite";
		else if(Double.isNaN(number)) return "NaN";
		return formatNumber(new BigDecimal(number), new BigDecimal(smallestStep));
	}

	/**
	 * Formats the given number by rounding it to the number closest to it that can be divided by the given step.
	 * @param number the number to format
	 * @param smallestStep the step (5.78, step: 0.2 -> 5.8)
	 * @return the formatted number as String
	 */
	public static String formatNumber(BigDecimal number, BigDecimal smallestStep){
		boolean negative = number.doubleValue() < 0;
		number = number.abs();
		BigDecimal stepCount = number.divide(smallestStep).setScale(0, RoundingMode.HALF_UP);
		BigDecimal rounded = stepCount.multiply(smallestStep);
		if(negative) rounded = rounded.multiply(new BigDecimal(-1));
		String formattedString = rounded + "";
		return formattedString;
	}
	
	/**
	 * Formats the given number by rounding it to the number closest to it that can be divided by the given step.
	 * <br/>This gives it suffixes if it is big (like 1K for 1000)
	 * @param number the number to format
	 * @param smallestStep the step (5.78, step: 0.2 -> 5.8)
	 * @param numberAbbreviations the suffixes to append
	 * @return the formatted number as String
	 */
	public static String formatNumber(double number, String smallestStep, NumberAbbreviations numberAbbreviations){
		return formatNumber(number, smallestStep, numberAbbreviations, 1);
	}
	
	/**
	 * Formats the given number by rounding it to the number closest to it that can be divided by the given step.
	 * <br/>This gives it suffixes if it is big (like 1K for 1000)
	 * @param number the number to format
	 * @param smallestStep the step (5.78, step: 0.2 -> 5.8)
	 * @param numberAbbreviations the suffixes to append
	 * @param minDigitsBeforeComma the minimum amount of digits before the comma when choosing an abbreviation to make a large number smaller
	 * @return the formatted number as String
	 */
	public static String formatNumber(double number, String smallestStep, NumberAbbreviations numberAbbreviations, int minDigitsBeforeComma){
		boolean negative = number < 0;
		number = Math.abs(number);
		if(minDigitsBeforeComma < 1) throw new IllegalArgumentException("minDigitsBeforeComma is " + minDigitsBeforeComma + " but cannot be smaller than 1");
		double minVal = Math.pow(10, minDigitsBeforeComma - 1);
		double shortedNr = number;
		int powerIndex = 0;
		String abbreviation = numberAbbreviations.get(powerIndex);
		while(shortedNr >= minVal * 1000 && numberAbbreviations.get(powerIndex + 1) != null){
			shortedNr /= 1000;
			powerIndex++;
			abbreviation = numberAbbreviations.get(powerIndex);
		}
		if(negative) shortedNr *= -1;
		return formatNumber(shortedNr, smallestStep) + " " + abbreviation;
	}
	
	public static String formatByteCount(long bytes){
		return formatByteCount(bytes, true);
	}
	
	public static String formatByteCount(long bytes, boolean binary) {
	    int unit = binary ? 1024 : 1000;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = "KMGTPE".charAt(exp-1) + "";
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	/**
	 * Defines abbreviations for big numbers like (1K for 1000, 1M for 1000K etc);
	 * @author A469627
	 *
	 */
	public static abstract class NumberAbbreviations{
		
		private Language defaultLang = Language.EN;
		private Language currentLang = defaultLang;
		
		private Map<Language, String[]> abbreviations;
		protected Language backupLanguage = defaultLang;
		
		/**
		 * Sets the language for the abbreviations
		 * @param lang the language
		 */
		public void setLanguage(Language lang){
			currentLang = lang;
		}
		
		/**
		 * Gets the abbreviation for the given number.
		 * @param intPowerOfThousand a number that is a result of 1000 ^ int
		 * @return the abbreviation
		 */
		public String get(double intPowerOfThousand){
			int power = (int)(Math.log(intPowerOfThousand) / Math.log(1000));
			return get(power);
		}
		
		/**
		 * Returns the abbreviation at the given index
		 * @param powerIndex 1000 ^ powerIndex = number the abbreviation stands for
		 * @return the abbreviation
		 */
		public String get(int powerIndex){
			if(abbreviations == null) abbreviations = getAbbreviations();
			Language lang = getLanguage();
			String[] thisLang = abbreviations.get(lang);
			if(thisLang.length > powerIndex) return thisLang[powerIndex];
			else return null;
		}
		
		/**
		 * Gets the language used for abbreviations.
		 * Will return the set language if available (Default: {@link Language#EN}).
		 * <br/>If the language is not available it will try {@link Language#getFallback()} until it finds a available language.
		 * @return the language used for abbreviations or null if no language is available.
		 */
		public Language getLanguage(){
			Language lang = currentLang;
			while(!abbreviations.containsKey(lang)){
				Language fallback = lang.getFallback();
				if(fallback == null){
					Iterator<Language> itr = abbreviations.keySet().iterator();
					if(itr.hasNext()) lang = itr.next();
					else break;
				}else lang = fallback;
			}
			return lang;
		}
		
		protected abstract Map<Language, String[]> getAbbreviations();
		
	}
	
	
	/**
	 * Contains the English abbreviations K, M, B etc and the German translations Tsd, Mio, Mrd etc;
	 * @author A469627
	 *
	 */
	public static class DefaultNumberAbbreviations extends NumberAbbreviations{
		
		/**
		 * Creates it with the default language (English).
		 */
		public DefaultNumberAbbreviations(){
			
		}
		
		/**
		 * Creates it with the given language
		 * @param language the language to get abbreviations in.
		 */
		public DefaultNumberAbbreviations(Language language){
			setLanguage(language);
		}

		@Override
		protected Map<Language, String[]> getAbbreviations() {
			Map<Language, String[]> abbreviations = new HashMap<Language, String[]>();
			abbreviations.put(Language.EN, new String[]{"", "K", "M", "B", "t", "q", "Q", "s", "S", "o", "n", "d", "U", "D", "T", "Qt", "Qd", "Sd", "St", "O", "N", "v"});
			abbreviations.put(Language.DE, new String[]{"", "Tsd", "Mio", "Mrd", "Bio", "Brd"});
			return abbreviations;
		}
		
	}
}
