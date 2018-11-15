package com.java.reflection;

import com.java.reflection.model.Company;
import com.java.reflection.model.Users;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MainReflection {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        Users users = new Users();
        users.setUserId(1);
        users.setUserName("hamid");
        Company company = new Company();
        company.setId_company(1);
        company.setName_company("be");
        users.setCompany(company);

        //Question 1
        Object objNew = copy(users);

        //Question 3
        String JsonStringForObje = toJson(users);
        System.out.println(JsonStringForObje);

        //Question 2 public <T> T toObj(String json,Class<T> cls){
        Users companyClass = new Users();
        Class clazzToObj = companyClass.getClass();
        String jsonObj = "{ \"userId\": { \"vv\":\" fivrfi\", \"budeebu\":330}  , \"userName\": \"hamid\", \"Nme\": \"be\" ,\"SecondJsonObj\": { \"secondJson\":\" fifi\", \"bubu\":30} ,  \"bcbc\": \"hacsmid\" ,\"userId\": { \"vv\":\" fivrfi\", \"budeebu\":330} }";
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
                if (!argFieldNew.toString().contains("\"")) {
                    if (argFieldNew.toString().equals("true") || argFieldNew.toString().equals("false")) {
                        System.out.println("it is boolean and and ERROR is in Type");
                    } else if (argFieldNew.toString().equals("null")) {
                        System.out.println("It is null and ERROR is in Type");
                    } else if (argFieldNew.toString().contains("[") && argFieldNew.toString().contains("]")) {
                        System.out.println("It is Array and ERROR is in Type");
                    } else {
                        //TODO 12,03 not working as float because of parser split the input JSON
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
                methodSetter.invoke(clazzObj, setValue);
            }
        }
        return clazzObj;
    }


    private static Map<String, Object> parseJson(String jsonObj) {

        Map<String, Object> infoJson = new HashMap<>();
        String[] keyValueSplited = keyValueSpliter(jsonObj).toArray(new String[0]);
        System.out.println(keyValueSplited);

        String fieldNameRow = "";

        for (int i = 0; i < keyValueSplited.length; i++) {
            Object[] splitSubVir = null;
            if (!keyValueSplited[i].contains("{") && !keyValueSplited[i].contains(",")) {
                splitSubVir = keyValueSplited[i].split(":");
                fieldNameRow = (String) splitSubVir[0];
                String fieldName = fieldNameRow.replace("\"", "").trim().replaceAll("\\s+", "");
                infoJson.put(fieldName, splitSubVir[1].toString().trim());
                System.out.println(splitSubVir[1].toString().trim());
            } else {
                String objJsonValue = keyValueSplited[i];
                objJsonValue = objJsonValue.replaceAll(":\\s+\\{", ":{");
                objJsonValue = objJsonValue.replaceAll("\"\\s+\\:", "\":");


                objJsonValue = objJsonValue.trim();
                String value = objJsonValue.substring(objJsonValue.indexOf(":") + 1).trim();
                //splitSubVir = objJsonValue.split("[:{]");
                String fieldSub = objJsonValue.substring(1, objJsonValue.indexOf(":") - 1);
                Map<String, Object> infoJsonSub = parseJson(value);

                infoJson.put(fieldSub, infoJsonSub);
                System.out.println("The Object Json and should consider it");
            }
        }
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
            String[] jsonObjGetter = jsObjGetter(jsonObj);
            keyValues.add(jsonObjGetter[0]);
            if (jsonObjGetter[1].contains("{")) {
                while (true) {
                    jsonObjGetter = jsObjGetter(jsonObjGetter[1]);
                    keyValues.add(jsonObjGetter[0]);
                    if (!(jsonObjGetter[1]).contains("{")) {
                        jsonObj = jsonObjGetter[1];
                        break;
                    }

                }
            }

            if (!(jsonObj.contains("{"))) {
                splitVir = jsonObj.split(",");
                keyValues.addAll(Arrays.asList(splitVir));
            }

        }
        return keyValues;

    }

    private static String[] jsObjGetter(String jsonObj) {
        String jsonObjectGot = "";
        int indexofParantezOpen = jsonObj.indexOf("{");
        int indexOfPrantezClose = jsonObj.indexOf("}");

        String ValueOfJson = jsonObj.substring(indexofParantezOpen + 1, indexOfPrantezClose);

        String fieldOfJsonTemp = jsonObj.substring(indexofParantezOpen, indexOfPrantezClose + 1);
        String restTesxt = jsonObj.substring(0, indexofParantezOpen);

        int lastJsonVirgula = restTesxt.lastIndexOf(",");
        String fieldNameWithJson = "";
        String restString = "";
        if (lastJsonVirgula != -1) {
            fieldNameWithJson = restTesxt.substring(lastJsonVirgula + 1) + fieldOfJsonTemp;
            restString = jsonObj.substring(0, lastJsonVirgula) + jsonObj.substring(indexOfPrantezClose + 1);

        } else {
            fieldNameWithJson = jsonObj.substring(0, indexOfPrantezClose);
            jsonObj = jsonObj.replaceAll("}\\s+\\,", "},");
            restString = jsonObj.trim().substring(jsonObj.indexOf("},") + 2);
        }
        String[] restAndJson = new String[2];
        if (fieldNameWithJson.contains("{") && !(fieldNameWithJson.contains("}"))) {
            fieldNameWithJson += "}";
        }
        restAndJson[0] = fieldNameWithJson;
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
                if (!(checkIsPrimitive(method))) {
                    argFieldNew = toJson(argFieldNew);
                    System.out.println(toJsonedFor);
                }
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

                //if (checkIsPrimitive(method)) {
                value = method.invoke(obj);
                String mSetter = "s" + mName.substring(1);
                Method methodSetter = clazz.getDeclaredMethod(mSetter, method.getReturnType());
                methodSetter.invoke(newObj, value);
                //}
                //else {
                //System.out.println("is Not primitive getCompany will give other fields ");
                /*
                value = method.invoke(obj);
                    //TODO it does not need copy again
                    //Object objJson = copy(value);

                    String mSetter = "s" + mName.substring(1);
                    Method methodSetter = clazz.getDeclaredMethod(mSetter, method.getReturnType());
                    methodSetter.invoke(newObj, value);
                  */
                //}
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
