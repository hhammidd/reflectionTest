package com.java.reflection;

import com.java.reflection.model.Users;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

        System.out.println(jsonObj);
        Object stringToObject = toObj(jsonObj,clazzToObj);
    }

    private static <T> T toObj(String jsonObj, Class<T> clazzToObj) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object clazzObj = clazzToObj.newInstance();

        T objFromString = null;
        Method[] m = clazzToObj.getDeclaredMethods();

        List<Object> objectInfo = new ArrayList<>();

        objectInfo = parseJson(jsonObj);

        //TODO should take the value from above
        Object argFieldNew = objectInfo.get(0);
        String fieldName = (String) objectInfo.get(1);

        for (Method e : m) {
            int nOfArgs = e.getParameterCount();
            String mName = e.getName();

            if (mName.startsWith("set") && Character.isUpperCase(mName.charAt(3))) {
                Class<?>[] paramTypeNew = e.getParameterTypes();
                Method methodSetter = clazzToObj.getDeclaredMethod(mName,paramTypeNew[0]);
                methodSetter.invoke(clazzObj, argFieldNew);
            }
        }
        return (T) clazzObj;
    }

    private static List<Object> parseJson(String jsonObj) {
        List<Object> objectInfo = new ArrayList<>();
        Object argFileName = null;
        String fieldName = "here";

        // do the parser
        int startIndexVal = 0;
        int startIndexKey = 0;

        if (startIndexKey == -1 ){
            return null;
        }

        if (startIndexVal == -1){
            return null;
        }

        //TODO find all values here from JSON
        List<String> findValue = fineValue(jsonObj, startIndexVal);
        List<String> findKey = findKey(jsonObj, startIndexKey);

        findValue.stream().forEach(System.out::println);
        findKey.stream().forEach(System.out::println);

        objectInfo.add(0,argFileName);
        objectInfo.add(1, fieldName);
        return objectInfo;
    }

    private static List<String> findKey(String jsonObj, int where) {
        List<String> keyList = new ArrayList<>();

        int startIndex = jsonObj.indexOf("\"", where);
        int startComma = jsonObj.indexOf(",",where);
        if (startIndex == -1){
            return null;
        }
        while(true){
            String currentValue = findKeyJson(jsonObj, startIndex);
            if (currentValue.isEmpty()) {
                break;
            }
            keyList.add(currentValue);
            startIndex = startIndex + startComma ;

        }
        return keyList;
    }

    private static String findKeyJson(String jsonObj, int where) {
        int startIndex = jsonObj.indexOf("\"" ,where);
        if (startIndex == -1){
            return "";
        }

        int endIndex = findCommaIndex(jsonObj, startIndex, "\":");
        int accuIndex = findCommaIndex(jsonObj,startIndex,"}");
        int commaValue = findCommaIndex(jsonObj,startIndex,",");

        int minIndex = 0;
        if (endIndex !=-1) {
            minIndex = endIndex;
        }  else if (endIndex == -1){
            minIndex = accuIndex - endIndex;
        }
        if (minIndex == -1){
            return "";
        }
        System.out.println(jsonObj.substring(startIndex + 1, minIndex));
        return jsonObj.substring(startIndex + 1, minIndex);

    }

    private static List<String>  fineValue(String jsonObj, int where) {

        List<String> valuesList = new ArrayList<>();

        int startIndex = jsonObj.indexOf(":", where);
        if (startIndex == -1){
            return null;
        }

        while(true){
            String currentValue = findValueJson(jsonObj, startIndex);
            if (currentValue.isEmpty()) {
                break;
            }
            valuesList.add(currentValue);
            startIndex = jsonObj.indexOf(currentValue, startIndex) + currentValue.length();
        }
        return valuesList;
    }

    private static String findValueJson(String jsonObj, int where) {
        int startIndex = jsonObj.indexOf(":" ,where);
        if (startIndex == -1){
            return "";
        }

        int commaIndex = findCommaIndex(jsonObj, startIndex, ",");
        int accuIndex = findCommaIndex(jsonObj,startIndex,"}");

        int minIndex = 0;
        if (commaIndex !=-1) {
            minIndex = commaIndex;
        } else if (commaIndex == -1){
            minIndex = accuIndex;
        }
        if (minIndex == -1){
            return "";
        }
        System.out.println(jsonObj.substring(startIndex + 1, minIndex));
        return jsonObj.substring(startIndex + 1, minIndex);


    }

    private static int findCommaIndex(String jsonObj, int startIndex, String stopOnComma) {
        int currentIndex = jsonObj.indexOf(stopOnComma, startIndex+1);
        while (currentIndex !=-1){
            return currentIndex;
        }
        return -1;
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
