package com.java.reflection;

import com.java.reflection.model.City;
import com.java.reflection.model.Company;
import com.java.reflection.model.Users;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MainReflection {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        Users users = new Users();
        users.setUserId(100);
        users.setUserName("hamid");
        Company company = new Company();
        company.setId_company(1);
        company.setName_company("be");
        company.setCountry("IT");
        City city = new City();
        city.setCity_id(1001);
        city.setCity_Name("MILANO");
        company.setCity(city);

        users.setCompany(company);

        //Question 1
        Object objNew = copy(users);

        //Question 3
        String jsonStringForObje = toJson(users);
        System.out.println(jsonStringForObje);

        //Question 2 public <T> T toObj(String json,Class<T> cls){
        Users companyClass = new Users();
        Class clazzToObj = companyClass.getClass();
        //String jsonObj = {"userName": hamid,"userId": 1,"company": {"name_company": be,"id_company": }"";
        Object strToObj2 = toObj2(jsonStringForObje, clazzToObj);
        Object stringToObject = toObj(jsonStringForObje, clazzToObj);
        System.out.println(stringToObject);
    }

    private static Object toObj2(String jsonStringForObje, Class clazzToObj) throws IllegalAccessException, InstantiationException {
        Object clazzObj = clazzToObj.newInstance();
        Map<String, Object> modelPropertyWithout = new HashMap<>();
        modelPropertyWithout = parseJsonWithoutNew(jsonStringForObje);


        return clazzObj;
    }

    private static Map<String, Object> parseJsonWithoutNew(String jsonStringForObje) {
        Map<String, Object> infoJson = new HashMap<>();

        Object valueOfJson = new Object();
        String keyOfJson = "";

        List<String> keyValueSplited = keyValueSpliterNew(jsonStringForObje);

        System.out.println(keyValueSplited);
        infoJson.put(keyOfJson, valueOfJson);
        return infoJson;
    }

    private static List<String> keyValueSpliterNew(String jsonStringForObje) {
        List<String> keyValues = new ArrayList<>();

        if (jsonStringForObje.trim().startsWith("{")) {
            jsonStringForObje = jsonStringForObje.substring(jsonStringForObje.indexOf("{") + 1, jsonStringForObje.lastIndexOf("}")).trim();
        }

        jsonStringForObje = jsonStringForObje.replaceAll(":\\s+\\{", ":{");
        jsonStringForObje = jsonStringForObje.replaceAll("\"\\s+\\:", "\":");

        List<String> subStr = new ArrayList<>();
        String maybeJson = jsonStringForObje.substring(jsonStringForObje.indexOf(",")+1);
        String jsoni = jsonStringForObje.substring(0,jsonStringForObje.indexOf(","));

        while (true){
            maybeJson = maybeJson.substring(maybeJson.indexOf(",")+1);
            jsoni = maybeJson.substring(0,maybeJson.indexOf(","));
            if (!(jsoni).contains("{")){
                keyValues.add(jsoni);
            } else {
                subStr = keyValueSpliterNew(jsoni);
            }

            if(!(maybeJson.contains(","))){
                break;
            }

        }
        keyValues.addAll(subStr);
        return keyValues;
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

        Map<String, Object> modelPropertyWithout = new HashMap<>();
        modelPropertyWithout = parseJsonWithout(jsonObj);

        List<String> keyValueSplitedTotal = keyValueSpliter(jsonObj);

        for (Method method : clazzToObj.getDeclaredMethods()) {
            String mName = method.getName();
            if (mName.startsWith("set") && Character.isUpperCase(mName.charAt(3))) {

                String fieldNameWithUpper = mName.substring(3);
                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);
                System.out.println(fieldName);
                Object setValue = new Object();

                Object argFieldNew = modelPropertyWithout.entrySet().stream().filter(eKey -> eKey.getKey().equals(fieldName)).findFirst()
                        .get().getValue();

                if (argFieldNew.toString().contains("{") && argFieldNew.toString().contains(",")) {

                    String mGetter = "g" + mName.substring(1);
                    Method method1 = clazzToObj.getMethod(mGetter);

                    // argfield is an map which should give us a copy
                    Object subClazzObj = toObj((String) argFieldNew, method1.getReturnType());

                    method.invoke(clazzObj, subClazzObj);

                } else {
                    if (!argFieldNew.toString().contains("\"")) {
                        if (argFieldNew.toString().equals("true") || argFieldNew.toString().equals("false")) {
                            System.out.println("it is boolean and and ERROR is in Type");
                        } else if (argFieldNew.toString().equals("null")) {
                            System.out.println("It is null and ERROR is in Type");
                        } else if (argFieldNew.toString().contains("[") && argFieldNew.toString().contains("]")) {
                            System.out.println("It is Array and ERROR is in Type");
                        } else if (argFieldNew.toString().contains("{")) {
                            System.out.println("Json Type ");
                        } else {
                            if (argFieldNew.toString().contains(".") || argFieldNew.toString().contains(",")) {
                                System.out.println("number is float");
                                setValue = Float.parseFloat((String) argFieldNew);
                            } else {
                                System.out.println("It is Integer or number (int or float)");
                                setValue = Integer.parseInt((String) argFieldNew);
                            }
                        }
                    } else if (argFieldNew.toString().contains("\"")) {
                        setValue = argFieldNew;
                        System.out.println("String ");
                    } // if JSON is Object
                    else if (argFieldNew.toString().contains("{") && argFieldNew.toString().contains("}")) {
                        System.out.println("This is JSON OBJECT");
                    } else {
                        System.out.println("Not defined Format");
                    }

                    // you have to cast Object to the value is comming
                    System.out.println(method.getName());
                    Method methodSetter = clazzToObj.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    method.invoke(clazzObj, setValue);
                }
            }
        }

        System.out.println("here");
        return clazzObj;
    }

    private static Map<String, Object> parseJsonWithout(String jsonObj) {

        Map<String, Object> infoJson = new HashMap<>();
        Map<String, Object> keyValueSplited1 = new HashMap<>();
        Map<String, Object> keyValueSplitedObj = new HashMap<>();

        // TODO here id_company should be inside company
        List<String> keyValueSplited = keyValueSpliter(jsonObj);

        String fieldNameRow = "";

        for (String keyValues : keyValueSplited) {
            Object[] splitSubVir = null;
            if (!keyValues.contains("{") && !keyValues.contains(",")) {
                splitSubVir = keyValues.split(":");
                fieldNameRow = (String) splitSubVir[0];
                String fieldName = fieldNameRow.replace("\"", "").trim().replaceAll("\\s+", "");
                infoJson.put(fieldName, splitSubVir[1].toString().trim());
                System.out.println(splitSubVir[1].toString().trim());
            } else {

                keyValues = keyValues.replaceAll(":\\s+\\{", ":{");
                keyValues = keyValues.replaceAll("\"\\s+\\:", "\":");

                String keyOfObjectProperty = keyValues.substring(1, keyValues.indexOf(":") - 1).trim();
                String valueObjectProperty = keyValues.substring(keyValues.indexOf(":") + 1);
                valueObjectProperty = valueObjectProperty + "}";


                keyValueSplitedObj.put(keyOfObjectProperty, valueObjectProperty);

                System.out.println(keyValueSplited1);
            }
        }
        infoJson.putAll(keyValueSplitedObj);
        return infoJson;
    }

    private static Map<String, Object> parseJson(String jsonObj) {

        Map<String, Object> infoJson = new HashMap<>();
        Map<String, Object> keyValueSplited1 = new HashMap<>();
        Map<String, Object> keyValueSplitedObj = new HashMap<>();

        List<String> keyValueSplited = keyValueSpliter(jsonObj);
        System.out.println(keyValueSplited);

        String fieldNameRow = "";

        for (String keyValues : keyValueSplited) {
            Object[] splitSubVir = null;
            if (!keyValues.contains("{") && !keyValues.contains(",")) {
                splitSubVir = keyValues.split(":");
                fieldNameRow = (String) splitSubVir[0];
                String fieldName = fieldNameRow.replace("\"", "").trim().replaceAll("\\s+", "");
                infoJson.put(fieldName, splitSubVir[1].toString().trim());
                System.out.println(splitSubVir[1].toString().trim());
            } else {

                keyValues = keyValues.replaceAll(":\\s+\\{", ":{");
                keyValues = keyValues.replaceAll("\"\\s+\\:", "\":");

                String keyOfObjectProperty = keyValues.substring(1, keyValues.indexOf(":") - 1).trim();
                String valueObjectProperty = keyValues.substring(keyValues.indexOf(":") + 1);

                keyValueSplited1 = parseJson(valueObjectProperty);

                keyValueSplitedObj.put(keyOfObjectProperty, keyValueSplited1);

                System.out.println(keyValueSplited1);
            }
        }
        infoJson.putAll(keyValueSplitedObj);
        return infoJson;
    }

    private static List<String> keyValueSpliter(String jsonObj) {

        List<String> keyValues = new ArrayList<>();
        String[] splitVir = null;
        if (jsonObj.trim().startsWith("{")) {
            jsonObj = jsonObj.substring(jsonObj.indexOf("{") + 1, jsonObj.lastIndexOf("}")).trim();
        }

        if (!(jsonObj.contains("{"))) {
            splitVir = jsonObj.split(",");
            keyValues.addAll(Arrays.asList(splitVir));
        } else {
            //TODO problem
            String[] jsonObjGetter = jsonKeyValueSpliter(jsonObj);
            keyValues.add(jsonObjGetter[0]);
            if (jsonObjGetter[1].contains("{")) {
                while (true) {
                    jsonObjGetter = jsonKeyValueSpliter(jsonObjGetter[1]);
                    keyValues.add(jsonObjGetter[0]);
                    if (!(jsonObjGetter[1]).contains("{")) {
                        jsonObj = jsonObjGetter[1];
                        break;
                    }
                }
            }

            if (!(jsonObjGetter[1].contains("{"))) {
                splitVir = jsonObjGetter[1].split(",");
                keyValues.addAll(Arrays.asList(splitVir));
            }
        }
        return keyValues;
    }

    private static String[] jsonKeyValueSpliter(String jsonObj) {

        jsonObj = jsonObj.replaceAll("}\\s+\\,", "},");
        jsonObj = jsonObj.replaceAll(":\\s+\\{", ":{");

        int indexOfPrantezClose = 0;
        int indexofParantezOpen = jsonObj.indexOf(":{");
        String cancelPra = jsonObj.substring(indexofParantezOpen+1);
        String btweenStr = "";
        String strObjectPropertyValue = "";
        if (!((indexOfPrantezClose) == -1)) {
            indexOfPrantezClose = jsonObj.indexOf("}");
             strObjectPropertyValue = jsonObj.substring(indexofParantezOpen+1, indexOfPrantezClose + 1);

             if (strObjectPropertyValue.substring(1).contains("{")){
                 strObjectPropertyValue = strObjectPropertyValue + "}";
                 List<String> jsKry = keyValueSpliter(strObjectPropertyValue);
                 System.out.println(jsKry);
             }

             strObjectPropertyValue = strObjectPropertyValue + "}";


        } else {
            indexofParantezOpen = jsonObj.indexOf("}");
            strObjectPropertyValue = jsonObj.substring(indexofParantezOpen, indexOfPrantezClose + 1);

        }

        String strAllJsonWithoutObjValue = jsonObj.substring(0, indexofParantezOpen);

        int startIndexOfObjProp = strAllJsonWithoutObjValue.lastIndexOf(",");
        String propertyObjeSplited = "";
        String restString = "";
        if (startIndexOfObjProp != -1) {
            propertyObjeSplited = strAllJsonWithoutObjValue.substring(startIndexOfObjProp + 1) + strObjectPropertyValue;
            restString = jsonObj.substring(0, startIndexOfObjProp) + jsonObj.substring(indexOfPrantezClose + 1);

        } else {
            propertyObjeSplited = jsonObj.substring(0, indexOfPrantezClose);
            jsonObj = jsonObj.replaceAll("}\\s+\\,", "},");
            restString = jsonObj.trim().substring(jsonObj.indexOf("},") + 2);
        }
        String[] restAndJson = new String[2];
        if (propertyObjeSplited.contains("{") && !(propertyObjeSplited.contains("}"))) {
            propertyObjeSplited += "}";
        }
        restAndJson[0] = propertyObjeSplited;
        restAndJson[1] = restString;
        return restAndJson;
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
                    System.out.println("is integer type");
                } else if (method.getReturnType().equals(Boolean.TYPE)) {
                    System.out.println("is Boolean Type");
                } else if (method.getReturnType().equals(Double.TYPE)) {
                    System.out.println("is double type");
                } else if (method.getReturnType().equals(Float.TYPE)) {
                    System.out.println("it is float");
                } else {
                    argFieldNew = "\"" + argFieldNew + "\"";
                    System.out.println("it is string");
                }

                // make the Json
                toJsonedFor += "\"" + fieldName + "\": " + argFieldNew + ",";
            }
        }
        toJsoned += toJsonedFor.substring(0, toJsonedFor.length() - 1);
        toJsoned = toJsoned + "}";
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
