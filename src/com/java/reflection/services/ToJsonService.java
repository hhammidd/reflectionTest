package com.java.reflection.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ToJsonService {
    /**
     * Q3
     *
     * @param obj
     * @return Json String of the Object
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public String toJson(Object obj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class clazz = obj.getClass();
        String toJsoned = "{";
        String toJsonedFor = "";

        for (Method method : clazz.getDeclaredMethods()) {
            int nOfArgs = method.getParameterCount();
            String mName = method.getName();
            if (mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) {
                String fieldNameWithUpper = mName.substring(3);
                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);

                Method methodCall = clazz.getMethod(mName);
                Object argFieldNew = methodCall.invoke(obj);


                System.out.println(method.getReturnType());
                if (!(checkIsPrimitive(method))) {
                    argFieldNew = toJson(argFieldNew);
                    System.out.println(toJsonedFor);
                } else if (method.getReturnType().equals(Integer.TYPE)) {

                } else if (method.getReturnType().equals(Boolean.TYPE)) {

                } else if (method.getReturnType().equals(Double.TYPE)) {

                } else if (method.getReturnType().equals(Float.TYPE)) {

                } else {
                    argFieldNew = "\"" + argFieldNew + "\"";
                }

                // make the Json
                toJsonedFor += "\"" + fieldName + "\": " + argFieldNew + ",";
            }
        }
        toJsoned += toJsonedFor.substring(0, toJsonedFor.length() - 1);
        toJsoned = toJsoned + "}";
        return toJsoned;
    }
    private static boolean checkIsPrimitive(Method method) {

        Class<?> methodType = method.getReturnType();

        if (!(methodType.isPrimitive() || methodType == Double.class || methodType == Float.class || methodType == Long.class ||
                methodType == Integer.class || methodType == Short.class || methodType == Character.class ||
                methodType == Byte.class || methodType == Boolean.class || methodType == String.class)) {
            System.out.println("is Object");
            return false;
        }
        return true;
    }
}
