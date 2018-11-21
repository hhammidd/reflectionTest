package com.java.reflection.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToObject {
    public static Object toObj(String jsonStringForObje, Class clazzToObj) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object clazzObj = clazzToObj.newInstance();
        Map<String, Object> jsonKeyValueSplitted = new HashMap<>();
        jsonKeyValueSplitted = parseJsonWithoutNew(jsonStringForObje);
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
        for (Method method : clazzToObj.getDeclaredMethods()) {
            String mName = method.getName();
            if (mName.startsWith("set") && Character.isUpperCase(mName.charAt(3))) {

                String fieldNameWithUpper = mName.substring(3);
                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);
                Object setValue = new Object();

                Object jsonValueMatched = jsonKeyValueSplitted.entrySet().stream().filter(eKey -> eKey.getKey().equals(fieldName)).findFirst()
                        .get().getValue();

                if (jsonValueMatched.toString().contains("{") && jsonValueMatched.toString().contains(",")) {

                    String mGetter = "g" + mName.substring(1);
                    Method method1 = clazzToObj.getMethod(mGetter);

                    Object subClazzObj = toObj((String) jsonValueMatched, method1.getReturnType());

                    method.invoke(clazzObj, subClazzObj);
                } else {
                    if (!jsonValueMatched.toString().contains("\"")) {
                        if (jsonValueMatched.toString().equals("true") || jsonValueMatched.toString().equals("false")) {
                            //System.out.println("it is boolean and and ERROR is in Type");
                        } else if (jsonValueMatched.toString().equals("null")) {
                            //System.out.println("It is null and ERROR is in Type");
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
                        setValue = jsonValueMatched;
                        //System.out.println("String ");
                    } // if JSON is Object
                    else if (jsonValueMatched.toString().contains("{") && jsonValueMatched.toString().contains("}")) {
                        //System.out.println("This is JSON OBJECT");
                    } else {
                        //System.out.println("Not defined Format");
                    }
                    //System.out.println(method.getName());
                    method.invoke(clazzObj, setValue);
                }
            }
        }

        return clazzObj;
    }

    private static Map<String, Object> parseJsonWithoutNew(String jsonStringForObje) {
        Map<String, Object> jsonKeyValueSplitedMap = new HashMap<>();

        List<String> keyValueSplited = keyValueSpliter(jsonStringForObje);
        jsonKeyValueSplitedMap = doPutJsonKeyValue(keyValueSplited);

        return jsonKeyValueSplitedMap;
    }

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
                String firstJsonStringAnalysis = jsObj.substring(0, jsObj.indexOf(","));
                String beforePrantesi = jsObj.substring(0, jsObj.indexOf("{") + 1);
                if (firstJsonStringAnalysis.contains("{")) {

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
                            finalJson = beforePrantesi + jsonStringRest.substring(0, k+1);
                            jsonList.add(finalJson);
                            // TODO sometimes go to the error
                            System.out.println("The Error is parsing this tojson Is : " + jsObj);
                            System.out.println("The finalJson is : " + finalJson);
                            if ((jsObj.indexOf(finalJson.length()+1)) != -1){
                                jsObj = jsObj.substring(finalJson.length() + 1);
                            } else {
                                break;
                            }
                            if (jsObj.indexOf(",") == -1) {
                                break;
                            }
                            break;
                        }
                    }
                } else if (!(firstJsonStringAnalysis).contains("{") && jsObj.contains(",")) {
                    jsonList.add(firstJsonStringAnalysis);
                    jsObj = jsObj.substring(firstJsonStringAnalysis.length() + 1);
                }

                if (!(jsObj.contains(","))) {
                    jsonList.add(jsObj.substring(0, jsObj.length() - 1));
                    return jsonList;
                }
            }
        }


    }


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

                keyValues = keyValues.replaceAll(":\\s+\\{", ":{");
                keyValues = keyValues.replaceAll("\"\\s+\\:", "\":");

                String keyOfObjectProperty = keyValues.substring(1, keyValues.indexOf(":") - 1).trim();
                String valueObjectProperty = keyValues.substring(keyValues.indexOf(":") + 1);
                valueObjectProperty = valueObjectProperty + "}";


                keyValueSplitedObj.put(keyOfObjectProperty, valueObjectProperty);

            }
        }
        infoJson.putAll(keyValueSplitedObj);
        return infoJson;
    }

}
