package com.creditsuisse.util;

import java.security.SecureRandom;

public class EnumUtil {
	
	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * Gets a random element from the given enum
	 * @param clazz the enums class
	 * @return a random element from it
	 */
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = RANDOM.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}
