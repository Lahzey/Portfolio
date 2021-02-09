package com.creditsuisse.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.map.MultiKeyMap;

public class GetterSetterAccess {
	
	private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is)(.*)");
	private static final Pattern SETTER_PATTERN = Pattern.compile("(set)(.*)");
	
	private final List<String> attributes = new ArrayList<String>();
	private final Map<String, Class<?>> attributeTypes = new HashMap<String, Class<?>>();
	private final Map<String, Method> getters = new HashMap<String, Method>();
	private final MultiKeyMap<Object, Method> setters = new MultiKeyMap<Object, Method>();
	
	public GetterSetterAccess(Class<?> clazz){
		this(clazz, false);
	}
	
	public GetterSetterAccess(Class<?> clazz, boolean fieldMustExist){
		for(Method method : ReflectionUtil.getAllMethodsInHierarchy(clazz)){
		    if(Modifier.isPublic(method.getModifiers())) {
				String name = method.getName();
				
				Matcher getterMatcher = GETTER_PATTERN.matcher(name);
				if(getterMatcher.matches() && method.getParameterTypes().length == 0 && method.getReturnType() != null){
					String attributeName = getterMatcher.group(2);
					if(!attributeName.isEmpty()){
						attributeName = StringUtil.setCapitalizedAt(attributeName, 0, false);
						getters.put(attributeName, method);
						attributeTypes.put(attributeName, method.getReturnType());
					}
					continue;
				}
				
				Matcher setterMatcher = SETTER_PATTERN.matcher(name);
				if(setterMatcher.matches() && method.getParameterTypes().length == 1){
					String attributeName = setterMatcher.group(2);
					if(!attributeName.isEmpty()){
						attributeName = StringUtil.setCapitalizedAt(attributeName, 0, false);
						setters.put(attributeName, method.getParameterTypes()[0], method);
					}
				}
		    }
		}
		
		List<String> allFields = new ArrayList<String>();
		if(fieldMustExist){
			for(Field field : ReflectionUtil.getAllFieldsInHierarchy(clazz)){
				allFields.add(StringUtil.setCapitalizedAt(field.getName(), 0, false));
			}
		}
		
		for(String attributeName : getters.keySet()){
			if(setters.containsKey(attributeName, attributeTypes.get(attributeName))){
				if(!fieldMustExist || allFields.contains(attributeName)){
					attributes.add(attributeName);
					getters.get(attributeName).setAccessible(true);
					setters.get(attributeName, attributeTypes.get(attributeName)).setAccessible(true);
				}
			}
		}
	}
	
	public String[] getAttributes(){
		return attributes.toArray(new String[attributes.size()]);
	}
	
	public Class<?> getAttributeType(String attribute){
		return attributeTypes.get(attribute);
	}
	
	public Object getValue(Object object, String attribute){
		try {
			return getters.get(attribute).invoke(object);
		} catch (Exception e) {
			// should not happen
			e.printStackTrace();
			return null;
		}
	}
	
	public void setValue(Object object, String attribute, Object value){
		try {
			setters.get(attribute, attributeTypes.get(attribute)).invoke(object, value);
		} catch (Exception e) {
			// should not happen
			e.printStackTrace();
		}
		
	}
	
}
