package com.java.reflection;

import com.java.reflection.model.Users;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainReflection {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Users users =  new Users();
        users.setUserId(1);
        users.setUserName("hamid");
        //Map<String, Object> properties = BeanUtils.describe(users);
        Object objNew = copy(users);
        System.out.println(objNew.getClass());
    }

    private static Object copy(Object obj) throws NoSuchFieldException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, InstantiationException {

        Class clazz = obj.getClass();
        Object clazzNewObj =  obj.getClass().newInstance();
        Class clazzNew = clazzNewObj.getClass();

        Method[] m = clazz.getDeclaredMethods();
        Method[] methodsNew = clazzNew.getDeclaredMethods();

        for (Method e : m){
            int nOfArgs = e.getParameterCount();
            String mName = e.getName();
            boolean isUpperCase = false;
            isUpperCase = isUpper(mName);

            if (mName.startsWith("get") && nOfArgs == 0)
                if (isUpperCase){
                    String fieldNameWithUpper = mName.substring(3);
                    String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);

                    Method methodCall = clazz.getDeclaredMethod(mName);

                    String mSetter = "s" + mName.substring(1);
                    Object argFieldNew = methodCall.invoke(obj);

                    // set got value from obj1
                    for (Method eNew : methodsNew){
                        String mNameNew = eNew.getName();
                        if (mNameNew.contains(mSetter)){
                            Class<?>[] paramTypeNew = eNew.getParameterTypes();
                            System.out.println(paramTypeNew[0]);
                            Method methodCallNew = clazz.getDeclaredMethod(mNameNew ,paramTypeNew[0]);
                            methodCallNew.invoke(clazzNewObj, argFieldNew);
                        }
                    }
                }
        }
        return clazzNewObj;
    }

    private static boolean isUpper(String mName) {
        boolean isUpper = false;
        if (Character.isUpperCase(mName.charAt(3)))
            isUpper = true;
        return isUpper;
    }
}
