package bankcity.data;

import java.util.HashMap;
import java.util.Map;

/**
 * This class  contains all the text used in this game in all supported languages.
 * <br/>The translations are intentionally defined within the Java code and not in a separate file to make use of type safety and renaming safely with an IDE.
 * @author A469627
 *
 */
public class Text {
	
	public static Language LANG = Language.CH;
	public static final Language EN = Language.EN;
	public static final Language DE = Language.DE;
	public static final Language CH = Language.CH;
	public static final Language FR = Language.FR;
	public static final Language IT = Language.IT;
	
	public static final Text UNEDUCATED = new Text(EN, "uneducated", DE, "ungebildet", CH, "unbildet");
	public static final Text POORLY_EDUCATED = new Text(EN, "poorly educated", DE, "schlecht ausgebildet", CH, "schlecht usbildet");
	public static final Text EDUCATED = new Text(EN, "educated", DE, "ausgebildet", CH, "usbildet");
	public static final Text WELL_EDUCATED = new Text(EN, "well-educated", DE, "gut ausgebildet", CH, "guet usbildet");
	public static final Text EDUCATION = new Text(EN, "Education", DE, "Ausbildung", CH, "Usbildig");
	
	public static final Text UNOCCUPIED = new Text(EN, "unoccupied", DE, "unbeschäftigt");
	public static final Text WORK = new Text(EN, "Work", DE, "Arbeit");
	public static final Text RETIRED = new Text(EN, "retired", DE, "pensioniert");
	
	public static final Text MAIN_BUILDING = new Text(EN, "Main Building", DE, "Hauptgebäude");
	public static final Text POLICE = new Text(EN, "Police", DE, "Polizei");
	public static final Text FACTORY = new Text(EN, "Factory", DE, "Fabrik");
	public static final Text POOL_HOUSE = new Text(EN, "Pool House", DE, "Pool Haus", CH, "Pool Huus");
	public static final Text SCHOOL = new Text(EN, "School", DE, "Schule", CH, "Schuel");
	
	
	private Map<Language, String> translations;
	
	/**
	 * Creates a new text with the given language - translation mapping.
	 * @param translations
	 */
	public Text(Map<Language, String> translations){
		this.translations = translations;
	}
	
	/**
	 * Creates a new Text with the given translations.
	 * @param translations an array of Language, String, Language, String etc.
	 * @throws IllegalArgumentException if the translations array is not in the format mentioned above.
	 */
	public Text(Object... translations){
		this.translations = new HashMap<>();
		for(int i = 0; i < translations.length - 1; i += 2){
			Object lang = translations[i];
			Object text = translations[i + 1];
			if(lang instanceof Language && text instanceof String){
				this.translations.put((Language) lang, (String) text);
			}else throw new IllegalArgumentException("Object parameters need to be Language, String, Language, String etc...\nGiven object " + lang + " is not a Language or " + text + " is not a String");
		}
	}
	
	/**
	 * Gets the translation of this text in the given language.
	 * <br/>If the given language is not available, the text will be returned in English. If English is also not available, it will pick any translation (no guarantee on which will be picked).
	 * <br/>(For Swiss German it will first try German before falling back to English).
	 * @param lang the language to get this text in.
	 * @return a String representing this text preferably in the given language or null if there are no translations at all.
	 */
	public String get(Language lang){
		if(translations.containsKey(lang)){
			return translations.get(lang);
		}else if(lang == CH){
			return get(DE);
		}else if(lang != EN){
			return get(EN);
		}else if(!translations.isEmpty()){
			return translations.values().iterator().next();
		}else return null;
	}

	/**
	 * Gets the translation of this text in the given current language.
	 * <br/>If the current language is not available, the text will be returned in English. If English is also not available, it will pick any translation (no guarantee on which will be picked).
	 * <br/>(For Swiss German it will first try German before falling back to English).
	 * @return a String representing this text preferably in the current language or null if there are no translations at all.
	 */
	public String get() {
		return get(LANG);
	}
	
	/**
	 * Associates the given translation with the given language.
	 * @param lang the language of the translation.
	 * @param translation the text in the given language.
	 * @return the translation previously associated with the given language.
	 */
	public String put(Language lang, String translation){
		return translations.put(lang, translation);
	}
	
	/**
	 * A language (<a href="https://www.google.ch/search?q=what+is+a+language">What is a language?</a>).
	 * <ul>
	 * <li>EN: English</li>
	 * <li>DE: German</li>
	 * <li>CH: Swiss German</li>
	 * <li>FR: French</li>
	 * <li>IT: Italian</li>
	 * </ul>
	 * @author A469627
	 *
	 */
	public static enum Language{
		EN, DE, CH, FR, IT;
	}

}
