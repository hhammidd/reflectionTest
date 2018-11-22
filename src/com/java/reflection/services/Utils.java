package com.java.reflection.services;

import java.lang.reflect.Method;

public class Utils {
    public static boolean checkIsPrimitive(Method method) {

        Class<?> methodType = method.getReturnType();

        if (!(methodType.isPrimitive() || methodType == Double.class || methodType == Float.class || methodType == Long.class ||
                methodType == Integer.class || methodType == Short.class || methodType == Character.class ||
                methodType == Byte.class || methodType == Boolean.class || methodType == String.class)) {
            //System.out.println("is Object");
            return false;
        }
        return true;
    }
}
