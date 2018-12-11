package com.java.reflection.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.java.reflection.services.Utils.checkIsPrimitive;

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
            if ((mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3)) ||
                    mName.startsWith("is") && Character.isUpperCase(mName.charAt(2)))) {
                String fieldNameWithUpper = "";
                if (mName.startsWith("get")) {
                    fieldNameWithUpper = mName.substring(3);
                } else if (mName.startsWith("is")){
                    fieldNameWithUpper = mName.substring(2);
                }

                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);
                Method methodCall = clazz.getMethod(mName);
                Object argFieldNew = methodCall.invoke(obj);


                //System.out.println(method.getReturnType());
                if (!(checkIsPrimitive(method))) {
                    argFieldNew = toJson(argFieldNew);
                    //System.out.println(toJsonedFor);
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

}
