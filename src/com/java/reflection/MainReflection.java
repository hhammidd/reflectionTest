package com.java.reflection;

import com.java.reflection.model.Users;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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
        String jsonObj = "{ \"userId\": 4 , \"userName\": \"hamid\" }";

        Object stringToObject = toObj(jsonObj, clazzToObj);
        System.out.println(stringToObject);
    }

    /**
     * input the JSOn and Output the Object
     *
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

        Map<String, Object> objectInfo = new HashMap<>();
        objectInfo = parseJson(jsonObj);

        for (Method method : clazzToObj.getDeclaredMethods()) {
            int nOfArgs = method.getParameterCount();
            String mName = method.getName();
            if (mName.startsWith("set") && Character.isUpperCase(mName.charAt(3))) {

                String fieldNameWithUpper = mName.substring(3);
                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);
                System.out.println(fieldName);
                Object setValue = new Object();

                // take the arg TODO delete userId
                Object argFieldNew = objectInfo.entrySet().stream().filter(eKey -> eKey.getKey().equals(fieldName)).findFirst()
                        .get().getValue();
                //TODO Update the boolean, Array, Number
                if (!argFieldNew.toString().contains("\"") ){
                    if (argFieldNew.toString().equals("true") || argFieldNew.toString().equals("false")){
                        System.out.println("it is boolean and and ERROR is in Type");
                    } else if (argFieldNew.toString().equals("null")){
                        System.out.println("It is null and ERROR is in Type");
                    } else if (argFieldNew.toString().contains("[") && argFieldNew.toString().contains("]")){
                        System.out.println("It is Array and ERROR is in Type");
                    }
                    else {
                        //TODO 12,03 not working as float because of parser split the input JSON
                        if (argFieldNew.toString().contains(".") || argFieldNew.toString().contains(",")){
                            System.out.println("number is float");
                            setValue = Float.parseFloat((String) argFieldNew);
                        } else {
                            System.out.println("It is Integer or number (int or float)");
                            setValue = Integer.parseInt((String) argFieldNew);
                        }


                    }
                } else if (argFieldNew.toString().contains("\"")){
                    setValue = argFieldNew;
                    System.out.println("String ");
                } // if JSON is Object
                else if (argFieldNew.toString().contains("{") && argFieldNew.toString().contains("}")){
                    System.out.println("This is JSON OBJECT");
                } else {
                    System.out.println("Not defined Format");
                }
                // you have to cast Object to the value is comming
                System.out.println(method.getName());
                Method methodSetter = clazzToObj.getDeclaredMethod(method.getName(), method.getParameterTypes());
                methodSetter.invoke(clazzObj, setValue);
            }
        }
        return clazzObj;
    }


    private static Map<String, Object> parseJson(String jsonObj) {

        jsonObj = jsonObj.substring(jsonObj.indexOf("{") + 1, jsonObj.indexOf("}")).trim();


        String[] splitVir = jsonObj.split(",");

        String fieldNameRow = "";
        Map<String, Object> infoJson = new HashMap<>();

        for (int i = 0; i < splitVir.length; i++) {
            Object[] splitSubVir = splitVir[i].split(":");
            fieldNameRow = (String) splitSubVir[0];
            //System.out.println("before: " + fieldNameRow);
            String fieldName = fieldNameRow.replace("\"", "").trim().replaceAll("\\s+", "");

            infoJson.put(fieldName, splitSubVir[1].toString().trim());
            System.out.println(splitSubVir[1].toString().trim());
        }
        return infoJson;
    }

    /**
     * Q3
     *
     * @param obj
     * @return Json String of the Object
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private static String toJson(Object obj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class clazz = obj.getClass();
        String toJsoned = "{\n";
        String toJsonedFor = "";

        for (Method method : clazz.getDeclaredMethods()) {
            int nOfArgs = method.getParameterCount();
            String mName = method.getName();
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
        Object newObj = clazz.newInstance();

        Object value = new Object();
        for (Method method : clazz.getDeclaredMethods()) {
            int nOfArgs = method.getParameterCount();
            String mName = method.getName();

            if (mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) {
                value = method.invoke(obj);

                String mSetter = "s" + mName.substring(1);
                Method methodSetter = clazz.getDeclaredMethod(mSetter, method.getReturnType());
                methodSetter.invoke(newObj, value);
            }
        }
        return newObj;
    }
}
