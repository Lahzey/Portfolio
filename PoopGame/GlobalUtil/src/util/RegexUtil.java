package util;

public class RegexUtil {
	

	
	/**
	 * Copied from {@link https://www.rgagnon.com/javadetails/java-0515.html} and adjusted by myself
	 * <br/>Converts a string with * and ? wildcards to a regex string.
	 * <br/>Escapes any other special characters that are relevant for regex.
	 * @param wildcardString the wildcard string to convert
	 * @return a regex-ready string representation of the given wildcard string.
	 */
	public static String wildcardToRegex(String wildcardString){
        StringBuffer s = new StringBuffer(wildcardString.length());
        boolean escapeNext = false;
        for (int i = 0, is = wildcardString.length(); i < is; i++) {
            char c = wildcardString.charAt(i);
            switch(c) {
                case '*':
                    if(escapeNext){
                        s.append("\\*");
                        s.append(c);
                		escapeNext = false;
                    }else s.append(".*");
                    break;
                case '?':
                	if(escapeNext){
                        s.append("\\?");
                		escapeNext = false;
                    }else s.append(".");
                    break;
                    // escape special regexp-characters
                case '(': case ')': case '[': case ']': case '$':
                case '^': case '.': case '{': case '}': case '|':
                    s.append("\\");
                    s.append(c);
            		escapeNext = false;
                    break;
                case '\\':
                	if(escapeNext){
                        s.append("\\\\");
                		escapeNext = false;
                	}
                	else escapeNext = true;
                default:
                    s.append(c);
            		escapeNext = false;
                    break;
            }
        }
        if(escapeNext){
        	s.append("\\\\");
        }
        return(s.toString());
    }
	
	/**
	 * Combines multiple patterns (with or).
	 * @param patterns the patterns to combine
	 * @return a pattern which will match if any of the given patterns would
	 */
	public static String combinePatterns(String[] patterns){
		String combined = null;
		for(String pattern : patterns){
			if(combined == null){
				combined = "(?:" + pattern + ")";
			}else{
				combined += "|(?:" + pattern + ")";
			}
		}
		return combined;
	}

}
