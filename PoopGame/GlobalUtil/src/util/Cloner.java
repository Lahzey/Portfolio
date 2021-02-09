package util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JFrame;

import org.objenesis.ObjenesisStd;

public class Cloner {
	
	private static ObjenesisStd OBJENESIS = new ObjenesisStd();
	
	public static <T> T clone(T object, boolean deep){
		return clone(object, deep, new ExactEqualityMap<Object, Object>());
	}

	@SuppressWarnings("unchecked")
	private static <T> T clone(T object, boolean deep, ExactEqualityMap<Object, Object> cloneCache){
		if(object == null){
			return null;
		}else{
			T clone = (T) cloneCache.get(object);
			if(clone != null){
				return clone;
			}else if(object.getClass().isPrimitive() || ReflectionUtil.isPrimitiveWrapper(object.getClass()) || object.getClass() == String.class || object.getClass() == Class.class){
				return object;
			}else{
				try {
					if(object.getClass().isArray()) {
				        int length = Array.getLength(object);
				        clone = (T) Array.newInstance(object.getClass().getComponentType(), length);
				        cloneCache.put(object, clone);
				        for (int i = 0; i < length; i++) {
				            Array.set(clone, i, clone(Array.get(object, i), true, cloneCache));
				        }
				        return clone;
				    }else{
						clone = (T) OBJENESIS.newInstance(object.getClass());
				        cloneCache.put(object, clone);
						List<Field> fields = ReflectionUtil.getAllFieldsInHierarchy(object.getClass());
						for(Field field : fields){
							boolean isAccessible = field.isAccessible();
							boolean isFinal = ReflectionUtil.isFinal(field);
							field.setAccessible(true);
							ReflectionUtil.setFinal(field, false);
							if(deep){
								field.set(clone, clone(field.get(object), true, cloneCache));
							}else{
								field.set(clone, field.get(object));
							}
							field.setAccessible(isAccessible);
							ReflectionUtil.setFinal(field, isFinal);
						}
						return clone;
				    }
				} catch(CloneException cloneException) {
					throw cloneException;
				} catch (Throwable e) {
					throw new CloneException(object, e);
				}
			}
		}
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method m = Cloner.class.getMethod("clone", Object.class, boolean.class);
		GetterSetterAccess access = new GetterSetterAccess(Method.class);
		for(String attr : access.getAttributes()){
			System.out.println(attr + " " + access.getValue(m, attr));
		}
		
		System.out.println("----------------------------------------------");
		Method m2 = clone(m, true);
		
		Method m3 = (Method) m2.invoke(m2, m2, true);
		
		
		for(String attr : access.getAttributes()){
			System.out.println(attr + " " + access.getValue(m2, attr));
		}
		
		
		for(String attr : access.getAttributes()){
			System.out.println(attr + " " + access.getValue(m3, attr));
		}
	}
	
	public static class CloneException extends RuntimeException {
		
		public CloneException(Object object, Throwable cause){
			super("Failed to clone object of type " + object.getClass(), cause);
		}
		
	}
	
}
