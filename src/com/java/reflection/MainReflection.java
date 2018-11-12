

import com.java.reflection.model.Users;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainReflection {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        Users users = new Users();
        users.setUserId(1);
        users.setUserName("hamid");
        //Map<String, Object> properties = BeanUtils.describe(users);
        Object objNew = copy(users);

        // Q3 public String toJson(Object obj){
        //// return json format of obj
        String JsonStringForObje = toJson(users);
        System.out.println(JsonStringForObje);
    }

    private static String toJson(Object obj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class clazz = obj.getClass();
        Method[] m = clazz.getDeclaredMethods();
        String toJsoned = "{\n";
        String toJsonedFor = "";

        for (Method e : m){
            int nOfArgs = e.getParameterCount();
            String mName = e.getName();
            if (mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))){
                String fieldNameWithUpper = mName.substring(3);
                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);

                Method methodCall = clazz.getDeclaredMethod(mName);

                String mSetter = "s" + mName.substring(1);
                Object argFieldNew = methodCall.invoke(obj);

                // make the Json
                toJsonedFor += "\"" + fieldName + "\": " + argFieldNew + ",\n"  ;
            }
        }
        toJsoned += toJsonedFor.substring(0, toJsonedFor.length() -2);
        toJsoned = toJsoned + "\n}";
        return toJsoned;
    }

    private static Object copy(Object obj) throws NoSuchFieldException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, InstantiationException {

        Class clazz = obj.getClass();
        Object clazzNewObj = obj.getClass().newInstance();
        Class clazzNew = clazzNewObj.getClass();

        Method[] m = clazz.getDeclaredMethods();
        Method[] methodsNew = clazzNew.getDeclaredMethods();

        for (Method e : m){
            int nOfArgs = e.getParameterCount();
            String mName = e.getName();
            if (mName.startsWith("get") && nOfArgs == 0 && Character.isUpperCase(mName.charAt(3))){
                String fieldNameWithUpper = mName.substring(3);
                String fieldName = fieldNameWithUpper.toLowerCase().charAt(0) + fieldNameWithUpper.substring(1);

                Method methodCall = clazz.getDeclaredMethod(mName);

                String mSetter = "s" + mName.substring(1);
                Object argFieldNew = methodCall.invoke(obj);
                // set got value from obj1
                for (Method setterMethod : m){
                    String mNameNew = setterMethod.getName();
                    if (mNameNew.contains(mSetter)){
                        Class<?>[] paramTypeNew = setterMethod.getParameterTypes();
                        System.out.println(paramTypeNew[0]);
                        Method methodCallNew = clazz.getDeclaredMethod(mNameNew ,paramTypeNew[0]);
                        methodCallNew.invoke(clazzNewObj, argFieldNew);
                    }
                }
            }
        }
        return clazzNewObj;
    }
}
