package com.java.reflection;

import com.java.reflection.model.Users;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainReflection {


    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        Users users = new Users();
        users.setUserId(1);
        users.setUserName("hamid");

        //Question 1
        Object objNew = copy(users);

        //Question 3
        String JsonStringForObje = toJson(users);
        System.out.println(JsonStringForObje);

        //Question 2 public <T> T toObj(String json,Class<T> cls){
        Users companyClass = new Users();
        Class clazzToObj = companyClass.getClass();
        String jsonObj = "{ \"userId\": 5, \"userName\": \"hamid\" }";

        Object stringToObject = toObj(jsonObj,clazzToObj);
        System.out.println(stringToObject);
    }

    /**
     * input the JSOn and Output the Object
     * @param jsonObj
     * @param clazzToObj
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private static Object toObj(String jsonObj, Class clazzToObj) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object clazzObj = clazzToObj.newInstance();

        Method[] m = clazzToObj.getDeclaredMethods();
        Map<String, Object> objectInfo = new HashMap<>();
        objectInfo = parseJson(jsonObj);

        for (Method e : m) {
            int nOfArgs = e.getParameterCount();
            String mName = e.getName();
            if (mName.startsWith("set") && Character.isUpperCase(mName.charAt(3))) {

                String fieldNameWithUpper = mName.substring(3);
                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);
                System.out.println(fieldName);

                // take the arg
                Object argFieldNew =  objectInfo.entrySet().stream().filter(eKey->eKey.getKey().equals("userId")).findFirst()
                        .get().getValue();

                System.out.println(e.getName());
                Method methodSetter = clazzToObj.getDeclaredMethod(e.getName(), e.getReturnType());
                e.invoke(clazzObj, argFieldNew);
            }
        }
        return clazzObj;
    }


    private static Map<String, Object> parseJson(String jsonObj) {

        jsonObj = jsonObj.substring(jsonObj.indexOf("{") +1, jsonObj.indexOf("}")).trim();


        String[] splitVir = jsonObj.split(",");

        String fieldNameRow = "";
        Map<String, Object> infoJson = new HashMap<>();

        for (int i = 0; i < splitVir.length ; i++){
            Object[] splitSubVir = splitVir[i].split(":");
            fieldNameRow = (String) splitSubVir[0];
            //System.out.println("before: " + fieldNameRow);
            String fieldName = fieldNameRow.replace("\"", "").trim().replaceAll("\\s+","");

            infoJson.put(fieldName,splitSubVir[1].toString().trim());
        }
        return  infoJson;
    }

    /**
     *  Q3
     * @param obj
     * @return Json String of the Object
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private static String toJson(Object obj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class clazz = obj.getClass();
        Method[] m = clazz.getDeclaredMethods();
        String toJsoned = "{\n";
        String toJsonedFor = "";

        for (Method e : m) {
            int nOfArgs = e.getParameterCount();
            String mName = e.getName();
            if (mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) {
                String fieldNameWithUpper = mName.substring(3);
                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);

                Method methodCall = clazz.getMethod(mName);

                Object argFieldNew = methodCall.invoke(obj);
                // make the Json
                toJsonedFor += "\"" + fieldName + "\": " + argFieldNew + ",\n";
            }
        }
        toJsoned += toJsonedFor.substring(0, toJsonedFor.length() - 2);
        toJsoned = toJsoned + "\n}";
        return toJsoned;
    }

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
    private static Object copy(Object obj) throws NoSuchFieldException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, InstantiationException {

        Class clazz = obj.getClass();
        Object clazzObj = obj.getClass().newInstance();

        Method[] m = clazz.getDeclaredMethods();

        String mSetter = "";
        Object argFieldNew = new Object();
        for (Method e : m) {
            int nOfArgs = e.getParameterCount();
            String mName = e.getName();

            if (mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) {
                Method methodCall = clazz.getMethod(mName);
                argFieldNew = methodCall.invoke(obj);

                mSetter = "s" + mName.substring(1);
                Method methodSetter = clazz.getDeclaredMethod(mSetter, e.getReturnType());
                methodSetter.invoke(clazzObj, argFieldNew);
            }
        }
        return clazzObj;
    }
}
