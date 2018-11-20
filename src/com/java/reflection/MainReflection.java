package com.java.reflection;

import com.java.reflection.model.City;
import com.java.reflection.model.Company;
import com.java.reflection.model.Users;
import com.java.reflection.services.CopyObjectService;
import com.java.reflection.services.ToJsonService;
import com.java.reflection.services.ToObject;

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
        CopyObjectService copyObjectService = new CopyObjectService();
        Object objNew = copyObjectService.copy(users);

        //Question 3
        ToJsonService toJsonService = new ToJsonService();
        String jsonStringForObje = toJsonService.toJson(users);
        //System.out.println(jsonStringForObje);

        //Question 2 public <T> T toObj(String json,Class<T> cls){
        Users companyClass = new Users();
        Class clazzToObj = companyClass.getClass();
        ToObject toObject = new ToObject();
        Object strToObj2 = toObject.toObj(jsonStringForObje, clazzToObj);
        System.out.println(strToObj2);
    }
}
