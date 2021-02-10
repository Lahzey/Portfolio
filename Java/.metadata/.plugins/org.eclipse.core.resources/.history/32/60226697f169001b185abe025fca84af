package com.creditsuisse.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ReflectionUtil {

	public static void setFinal(Field field, boolean isFinal) {
		try {
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			if (isFinal){
				modifiersField.setInt(field, field.getModifiers() | Modifier.FINAL);
			}else{
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			}
			modifiersField.setAccessible(false);
		} catch (Throwable e) {
			throw new RuntimeException("Failed to set field modifiers", e);
		}
	}

	public static boolean isFinal(Field field) {
		return Modifier.isFinal(field.getModifiers());
	}
	
	public static List<Field> getAllFieldsInHierarchy(Class<?> type) {
		return getAllFieldsInHierarchy(type, false);
	}

	public static List<Field> getAllFieldsInHierarchy(Class<?> type, boolean includeStatic) {
		List<Field> allFields = new ArrayList<Field>();

		if (type.getSuperclass() != null) {
			allFields.addAll(getAllFieldsInHierarchy(type.getSuperclass(), true));
		}

		allFields.addAll(Arrays.asList(type.getDeclaredFields()));
		
		if(!includeStatic){
			Iterator<Field> itr = allFields.iterator();
			while(itr.hasNext()){
				Field field = itr.next();
				if(Modifier.isStatic(field.getModifiers())){
					itr.remove();
				}
			}
		}

		return allFields;
	}
	
	public static List<Method> getAllMethodsInHierarchy(Class<?> type) {
		return getAllMethodsInHierarchy(type, false);
	}

	public static List<Method> getAllMethodsInHierarchy(Class<?> type, boolean includeStatic) {
		List<Method> allMethods = new ArrayList<Method>();
		Method[] declaredMethods = type.getDeclaredMethods();
		if (type.getSuperclass() != null) {
			Class<?> superClass = type.getSuperclass();
			allMethods.addAll(getAllMethodsInHierarchy(superClass, true));
		}
		allMethods.addAll(Arrays.asList(declaredMethods));

		
		if(!includeStatic){
			Iterator<Method> itr = allMethods.iterator();
			while(itr.hasNext()){
				Method method = itr.next();
				if(Modifier.isStatic(method.getModifiers())){
					itr.remove();
				}
			}
		}

		return allMethods;
	}
	
	public static boolean isPrimitiveWrapper(Class<?> type){
		return type == Double.class || type == Float.class ||
				type == Long.class || type == Integer.class ||
			    type == Short.class || type == Character.class ||
			    type == Byte.class ||type == Boolean.class;
	}
	
	public static Object convertToWrapper(Object primitive){
		Class<?> clazz = primitive.getClass();
		if(clazz.isPrimitive()){
			if(clazz == double.class){
				return (Double) primitive;
			}else if(clazz == float.class){
				return (Float) primitive;
			}else if(clazz == long.class){
				return (Long) primitive;
			}else if(clazz == int.class){
				return (Integer) primitive;
			}else if(clazz == short.class){
				return (Short) primitive;
			}else if(clazz == char.class){
				return (Character) primitive;
			}else if(clazz == byte.class){
				return (Byte) primitive;
			}else if(clazz == boolean.class){
				return (Boolean) primitive;
			}
		}
		throw new IllegalArgumentException(clazz.getName() + " is not a primitive type.");
	}

}
