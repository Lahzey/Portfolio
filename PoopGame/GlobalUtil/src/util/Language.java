package util;

public enum Language {
	EN, DE, CH(DE), IT, FR;
	
	private Language fallback;
	
	private Language(){
		this(null);
	}
	
	private Language(Language fallback){
		
	}
	
	public Language getFallback(){
		if(fallback != null) return fallback;
		else if(this != EN) return EN;
		else return null;
	}
}
