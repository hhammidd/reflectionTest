package com.java.reflection.services;

import com.java.reflection.MyTransition;
import com.java.reflection.model.Company;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.java.reflection.services.Utils.checkIsPrimitive;


public class CopyObjectService {
    /**
     * This method take the Object and Make an Copy of that
     *
     * @param obj
     * @return A copy of Object
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     */
    public Object copy(Object obj) throws NoSuchFieldException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, InstantiationException {

        Class clazz = obj.getClass();
        Object newObj = clazz.newInstance();

        Object value = new Object();
        for (Method method : clazz.getDeclaredMethods()) {
            int nOfArgs = method.getParameterCount();
            String mName = method.getName();

            if (!method.isAnnotationPresent(MyTransition.class)) {
                    if (mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) {
                        value = method.invoke(obj);
                        String mSetter = "s" + mName.substring(1);
                        Method methodSetter = clazz.getMethod(mSetter, method.getReturnType());
                        if (!(checkIsPrimitive(method))) {
                            Object newSubObj = copy(value);
                            methodSetter.invoke(newObj, newSubObj);
                        } else {
                            methodSetter.invoke(newObj, value);
                        }
                    }
            } else if (!method.getAnnotation(MyTransition.class).value().equals("noAllJob") &&
                    !method.getAnnotation(MyTransition.class).value().equals("noCopy")){
                    if (mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) {
                        value = method.invoke(obj);
                        String mSetter = "s" + mName.substring(1);
                        Method methodSetter = clazz.getMethod(mSetter, method.getReturnType());
                        if (!(checkIsPrimitive(method))) {
                            Object newSubObj = copy(value);
                            methodSetter.invoke(newObj, newSubObj);
                        } else {
                            methodSetter.invoke(newObj, value);
                        }
                    }
            }
        }
        return newObj;
    }
}
