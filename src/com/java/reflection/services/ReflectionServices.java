package com.java.reflection.services;

import com.java.reflection.MyTransition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectionServices {

    public Object copy(Object obj) throws NoSuchFieldException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, InstantiationException {

        Class clazz = obj.getClass();
        Object newObj = clazz.newInstance();

        Object value = new Object();
        for (Method method : clazz.getMethods()) {
            int nOfArgs = method.getParameterCount();
            String mName = method.getName();

            if (!method.isAnnotationPresent(MyTransition.class)) {
                if (mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3)) &&
                        !mName.equals("getClass")) {
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
                    !method.getAnnotation(MyTransition.class).value().equals("noCopy")) {
                if (!mName.equals("getClass") && mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) {
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

    private boolean checkIsPrimitive(Method method) {
        Class<?> methodType = method.getReturnType();

        if (!(methodType.isPrimitive() || methodType == Double.class || methodType == Float.class || methodType == Long.class ||
                methodType == Integer.class || methodType == Short.class || methodType == Character.class ||
                methodType == Byte.class || methodType == Boolean.class || methodType == String.class || methodType == Date.class)) {
            //System.out.println("is Object");
            return false;
        }
        return true;
    }


    public String toJson(Object obj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class clazz = obj.getClass();
        String toJsoned = "{";
        String toJsonedFor = "";

        for (Method method : clazz.getMethods()) {
            int nOfArgs = method.getParameterCount();
            String mName = method.getName();

            if (!mName.equals("getClass") && !method.isAnnotationPresent(MyTransition.class)) {
                if (((mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) ||
                        mName.startsWith("is") && Character.isUpperCase(mName.charAt(2)))) {

                    String fieldNameWithUpper = "";
                    if (mName.startsWith("get")) {
                        fieldNameWithUpper = mName.substring(3);
                    } else if (mName.startsWith("is")) {
                        fieldNameWithUpper = mName.substring(2);
                    }

                    //todo
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
                        if (argFieldNew != null)
                            argFieldNew = "\"" + argFieldNew + "\"";
                    }
                    // make the Json
                    toJsonedFor += "\"" + fieldName + "\": " + argFieldNew + ",";
                }
            } else if (!mName.equals("getClass") && !method.getAnnotation(MyTransition.class).value().equals("noAllJob") &&
                    !method.getAnnotation(MyTransition.class).value().equals("noJson")) {
                if (((mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) ||
                        mName.startsWith("is") && Character.isUpperCase(mName.charAt(2)))) {
                    String fieldNameWithUpper = "";
                    if (mName.startsWith("get")) {
                        fieldNameWithUpper = mName.substring(3);
                    } else if (mName.startsWith("is")) {
                        fieldNameWithUpper = mName.substring(2);
                    }

                    //todo
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
                        if (argFieldNew != null)
                            argFieldNew = "\"" + argFieldNew + "\"";
                    }
                    // make the Json
                    toJsonedFor += "\"" + fieldName + "\": " + argFieldNew + ",";
                }
            }

        }
        toJsoned += toJsonedFor.substring(0, toJsonedFor.length() - 1);
        toJsoned = toJsoned + "}";
        return toJsoned;
    }


    /**
     * Take the json String and set in Obj
     *
     * @param jsonStringForObje
     * @param clazzToObj
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static Object toObj(String jsonStringForObje, Class clazzToObj) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object clazzObj = clazzToObj.newInstance();
        //jsonKeyValueSplitted --> is a map of keys and values of json... value can be a json also.
        Map<String, Object> jsonKeyValueSplitted = new HashMap<>();
        jsonKeyValueSplitted = parseJsonWithoutNew(jsonStringForObje);
        //TODO here there is error
        clazzObj = objectValueInvoker(jsonKeyValueSplitted, clazzToObj);
        return clazzObj;
    }

    /**
     * @param jsonKeyValueSplitted : take the property or jsons (sperated by ,)
     * @param clazzToObj           : class of object
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static Object objectValueInvoker(Map<String, Object> jsonKeyValueSplitted, Class clazzToObj) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        Object clazzObj = clazzToObj.newInstance();
        for (Method method : clazzToObj.getMethods()) {
            int nOfArgs = method.getParameterCount();
            String mName = method.getName();
            //TODO do with get
            if (!mName.equals("getClass") && !method.isAnnotationPresent(MyTransition.class)) {
                if (((mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) ||
                        mName.startsWith("is") && Character.isUpperCase(mName.charAt(2)))) {
                    doObjInvok(mName, jsonKeyValueSplitted, method, clazzObj, clazzToObj);
                }

            } else if (!mName.equals("getClass") && !method.getAnnotation(MyTransition.class).value().equals("noAllJob") &&
                    !method.getAnnotation(MyTransition.class).value().equals("noToObj")) {
                if (((mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))) ||
                        mName.startsWith("is") && Character.isUpperCase(mName.charAt(2)))) {
                    doObjInvok(mName, jsonKeyValueSplitted, method, clazzObj, clazzToObj);

                }
            }
        }

        return clazzObj;
    }

    private static void doObjInvok(String mName, Map<String, Object> jsonKeyValueSplitted, Method method, Object clazzObj, Class clazzToObj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String fieldNameWithUpper = "";
        if (mName.startsWith("get")) {
            fieldNameWithUpper = mName.substring(3);
        } else if (mName.startsWith("is")) {
            fieldNameWithUpper = mName.substring(2);
        }

        String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);
        Object setValue = new Object();

        Object jsonValueMatched = jsonKeyValueSplitted.entrySet().stream().filter(eKey -> eKey.getKey().equals(fieldName)).findFirst()
                .get().getValue();

        if (jsonValueMatched.toString().contains("{") && jsonValueMatched.toString().contains(",")) {

            Method method1 = clazzToObj.getMethod(mName);

            Object subClazzObj = toObj((String) jsonValueMatched, method1.getReturnType());
            String mSetterSub = "s" + mName.substring(1);
            Method methodSetterSub = clazzToObj.getMethod(mSetterSub, method.getReturnType());
            methodSetterSub.invoke(clazzObj, subClazzObj);
        } else {
            if (!jsonValueMatched.toString().contains("\"")) {
                if (jsonValueMatched.toString().equals("true") || jsonValueMatched.toString().equals("false")) {
                    setValue = Boolean.valueOf((String) jsonValueMatched);
                    //System.out.println("it is boolean and and ERROR is in Type");
                } else if (jsonValueMatched.toString().equals("null")) {
                    setValue = null;
                } else if (jsonValueMatched.toString().contains("[") && jsonValueMatched.toString().contains("]")) {
                    //System.out.println("It is Array and ERROR is in Type");
                } else if (jsonValueMatched.toString().contains("{")) {
                    //System.out.println("Json Type ");
                } else {
                    if (jsonValueMatched.toString().contains(".") || jsonValueMatched.toString().contains(",")) {
                        //System.out.println("number is float");
                        setValue = Float.parseFloat((String) jsonValueMatched);
                    } else {
                        //System.out.println("It is Integer or number (int or float)");
                        setValue = Integer.parseInt((String) jsonValueMatched);
                    }
                }
            } else if (jsonValueMatched.toString().contains("\"")) {
                jsonValueMatched = jsonValueMatched.toString().replaceAll("\"", "");
                setValue = jsonValueMatched;
                //System.out.println("String ");
            } // if JSON is Object
            else if (jsonValueMatched.toString().contains("{") && jsonValueMatched.toString().contains("}")) {
                //System.out.println("This is JSON OBJECT");
            } else {
                //System.out.println("Not defined Format");
            }
            String mSetter = "set" + fieldNameWithUpper;
            System.out.println("the last method name: " + method.getName());

            Method methodSetter = clazzToObj.getMethod(mSetter, method.getReturnType());
            methodSetter.invoke(clazzObj, setValue);
        }

    }

    private static Map<String, Object> parseJsonWithoutNew(String jsonStringForObje) {
        Map<String, Object> jsonKeyValueSplitedMap = new HashMap<>();

        //will give list of key and value of json ()
        List<String> keyValueSplited = keyValueSpliter(jsonStringForObje);

        jsonKeyValueSplitedMap = doPutJsonKeyValue(keyValueSplited);

        return jsonKeyValueSplitedMap;
    }

    /**
     * will give list of key and value of json ()
     *
     * @param jsonInformation
     * @return
     */
    private static List<String> keyValueSpliter(String jsonInformation) {

        if (jsonInformation.trim().startsWith("{")) {
            jsonInformation = jsonInformation.substring(jsonInformation.indexOf("{") + 1, jsonInformation.lastIndexOf("}")).trim();
        }

        jsonInformation = jsonInformation.replaceAll(":\\s+\\{", ":{");
        jsonInformation = jsonInformation.replaceAll("\"\\s+\\:", "\":");

        List<String> jsonList = new ArrayList<>();
        jsonList = doParsJson(jsonInformation);

        return jsonList;
    }

    private static List<String> doParsJson(String jsObj) {
        List<String> jsonList = new ArrayList<>();
        //jsObj = "\"company\":{\"country\": \"IT\",\"id_company\": 1,\"name_company\": \"be\",\"city\":{\"city_id\": 1001,\"city_Name\": \"MILANO\"}}";
        while (true) {
            if (!(jsObj.contains(","))) {
                jsonList.add(jsObj);
                return jsonList;
            } else {
                // Cut from first index to , ss:ee,dd --> ss:ee
                String firstJsonStringAnalysis = jsObj.substring(0, jsObj.indexOf(","));
                // cut from index 0 to prantezi { --> TODO it should have prantezi
                String beforePrantesi = jsObj.substring(0, jsObj.indexOf("{") + 1);

                if (firstJsonStringAnalysis.contains("{")) {

                    // if first include { --> ss:{c:d},hh:ff --> cut the before prantezi
                    // result --> c:d},hh:ff
                    String jsonStringRest = jsObj.substring(jsObj.indexOf("{") + 1);
                    int equalPrantezi = 1;
                    String finalJson = "";
                    for (int k = 0; k < jsonStringRest.length(); k++) {
                        char ch = jsonStringRest.charAt(k);
                        if (ch == '{') {
                            equalPrantezi += 1;
                        } else if (ch == '}') {
                            equalPrantezi -= 1;
                        }
                        if (equalPrantezi == 0) {
                            //jsObj = "\"company\":{\"country\": \"IT\",\"city\":{\"city_Name\": \"MILANO\",\"city_id\": 1001},\"id_company\": 1,\"name_company\": \"be\"}";
                            // k is position of closed prantezi --> jsonRest: -->c:d},hh:ff --> for cutting it from rest
                            // finalJson is total json include value
                            finalJson = beforePrantesi + jsonStringRest.substring(0, k + 1);
                            jsonList.add(finalJson);
                            // TODO sometimes go to the error
                            // TODO you add { } object json , You will have after that 1. Nothing or ,
                            System.out.println("The Error is parsing this jsObj Is : " + jsObj);
                            System.out.println("The finalJson is : " + finalJson);
                            System.out.println("===Compare length ====jsonObj> " + jsObj.length() + " ====final length: " + finalJson.length());
                            //jsObj = "\"city\":{\"city_id\": 1001,\"city_Name\": \"MILANO\"}}";
                            //finalJson = "\"city\":{\"city_id\": 1001,\"city_Name\": \"MILANO\"}";
                            if (jsObj.length() - 1 > finalJson.length()) {
                                System.out.println("finalJson is Cutted Here: --> should not");
                                jsObj = jsObj.substring(finalJson.length() + 1);
                            } else {
                                return jsonList;
                            }
                            break;
                        }
                    }
                } else if (!(firstJsonStringAnalysis).contains("{") && jsObj.contains(",")) {
                    // jsObj--> ss:dd
                    jsonList.add(firstJsonStringAnalysis);
                    // this is put rest of json here
                    // TODO you have to consider if there will be or not
                    if (jsObj.length() > firstJsonStringAnalysis.length()) {
                        jsObj = jsObj.substring(firstJsonStringAnalysis.length() + 1);
                    } else
                        return jsonList;
                }

                if (!(jsObj.contains(","))) {
                    // TODO check here
                    if (jsObj.charAt(jsObj.length() - 1) == '}') {
                        jsObj = jsObj.substring(0, jsObj.length() - 1);
                    } else {
                        jsonList.add(jsObj);
                    }
                    return jsonList;
                }
            }
        }


    }


    /**
     * put key of Json in key Map and value in value of Hash map
     *
     * @param keyValueSplited
     * @return
     */
    private static Map<String, Object> doPutJsonKeyValue(List<String> keyValueSplited) {
        Map<String, Object> infoJson = new HashMap<>();
        Map<String, Object> keyValueSplitedObj = new HashMap<>();

        String fieldNameRow = "";

        for (String keyValues : keyValueSplited) {
            Object[] splitSubVir = null;
            if (!keyValues.contains("{") && !keyValues.contains(",")) {
                splitSubVir = keyValues.split(":");
                fieldNameRow = (String) splitSubVir[0];
                String fieldName = fieldNameRow.replace("\"", "").trim().replaceAll("\\s+", "");
                infoJson.put(fieldName, splitSubVir[1].toString().trim());
            } else {
                // Not Obj Json
                keyValues = keyValues.replaceAll(":\\s+\\{", ":{");
                keyValues = keyValues.replaceAll("\"\\s+\\:", "\":");

                String keyOfObjectProperty = keyValues.substring(1, keyValues.indexOf(":") - 1).trim();
                String valueObjectProperty = keyValues.substring(keyValues.indexOf(":") + 1);
                //valueObjectProperty = valueObjectProperty + "}";


                keyValueSplitedObj.put(keyOfObjectProperty, valueObjectProperty);

            }
        }
        infoJson.putAll(keyValueSplitedObj);
        return infoJson;
    }


}
