package com.java.reflection;

import com.java.reflection.model.Users;
import com.sun.xml.internal.ws.util.StringUtils;

import javax.jws.soap.SOAPBinding;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

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

        List<Object> fieldNameList = new ArrayList<>();
        List<Object> argFieldNewList = new ArrayList<>();
        Map<String, Object> fieldInfoMap = new HashMap<>();

        List<Map> objectInfo = new ArrayList<>();

        objectInfo = parseJson(jsonObj);
        fieldInfoMap = objectInfo.get(0);


        System.out.println("start");

        //fieldInfoMap = objectInfo.stream().map(x->x.keySet());
        //Map<String, Object> resultField = objectInfo.stream().collect(Collectors.toMap(s->(List<String>) s.get("key"), s-> (List<Object>) s.get()));
        //System.out.println(resultField);


        System.out.println(argFieldNewList);
        System.out.println(fieldNameList);
        Object argFieldNew = new Object();


        for (Method e : m) {
            int nOfArgs = e.getParameterCount();
            String mName = e.getName();


            if (mName.startsWith("set") && Character.isUpperCase(mName.charAt(3))) {
                // key is setUserName val is : hamid
                //String getValOfSpecialKey = objectInfo.stream().map();
                //String fieldNameOfMname = objectInfo.stream().map(x->x.keySet().contains(mName.substring(3)))
                String fieldNameWithUpper = mName.substring(3);
                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);
                System.out.println(fieldName);
                Object argFielFromMap = new Object();

                argFielFromMap =  objectInfo.stream().filter(entry-> entry.containsKey(fieldName));
                argFielFromMap =  objectInfo.stream().filter(entry-> entry.containsKey(fieldName))
                ;
                System.out.println(argFielFromMap);

                Class<?>[] paramTypeNew = e.getParameterTypes();
                Method methodSetter = clazzToObj.getDeclaredMethod(mName,paramTypeNew[0]);

                methodSetter.invoke(clazzObj, argFieldNew);
            }
        }
        return (T) clazzObj;
    }

    private static List<Map> parseJson(String jsonObj) {

        // { "A" :a, "B" : "b"} start test
        //String jsonStr = "{ \"Aaad\" :a, \"B\" : \"b\" , \"C\" : \"c\"}";
        // split ,
        //TODO find all values here from JSON
        List<Map> findValue = fineValue(jsonObj);
        return findValue;
    }

    private static List<Map> fineValue(String jsonObj) {

        jsonObj = jsonObj.substring(jsonObj.indexOf("{") +1, jsonObj.indexOf("}")).trim();

        String[] splitVir = jsonObj.split(",");

        String fieldNameRow = "";
        Object valueRow = new Object();
        List<String> fieldList = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();

        Map<String, Object> infoJson = new HashMap<>();
        List<Map> listInfoJsonMap = new ArrayList<>();

        for (int i = 0; i < splitVir.length ; i++){
            System.out.println(splitVir[i]);
            Object[] splitSubVir = splitVir[i].split(":");
            fieldNameRow = (String) splitSubVir[0];
            //System.out.println("before: " + fieldNameRow);
            String fieldName = fieldNameRow.replace("\"", "");
            fieldName.trim();
            fieldName.replaceAll("\\s+","");

            infoJson.put(fieldName,splitSubVir[1]);

            fieldList.add(fieldName);
            valueList.add(splitSubVir[1]);
        }
        listInfoJsonMap.add(infoJson);
        return listInfoJsonMap;
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
