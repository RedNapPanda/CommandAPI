package com.not2excel.api.reflection;

import java.lang.reflect.Field;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class ReflectionUtils
{
    public static Object getField(Object object, String field)
            throws SecurityException, NoSuchFieldException,
                   IllegalArgumentException, IllegalAccessException
    {
        Class<?> c = object.getClass();
        Field objectField = c.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }
    
     public static Method copyMethod(Method toCopy){
		try{
			Method copyMethod = Method.class.getDeclaredMethod("copy", (Class<?>[])null);
			copyMethod.setAccessible(true);
			return (Method) copyMethod.invoke(toCopy, (Object[]) null);
		}catch(Exception e){
			System.out.println("Exception while copying Method '" + toCopy.getName() + "'. Returning the method passed in arguments.");
			return toCopy;
		}
	}
}
