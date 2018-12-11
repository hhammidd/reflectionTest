package com.java.reflection;

import com.java.reflection.model.City;
import com.java.reflection.model.Company;
import com.java.reflection.model.Users;
import com.java.reflection.services.CopyObjectService;
import com.java.reflection.services.ToJsonService;
import com.java.reflection.services.ToObject;

import java.lang.reflect.InvocationTargetException;

public class MainReflection {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        //TODO check for boolean--> . toObj
        Users users = new Users();
        users.setUserId(100);
        users.setUserName("hamid");
        users.setRegister(false);
        Company company = new Company();
        company.setId_company(1);
        company.setName_company("be");
        company.setCountry("IT");
        company.setCity(null);
        City city = new City();
        city.setCity_id(1001);
        city.setCity_Name("MILANO");
        //company.setCity(city);

        users.setCompany(company);

        //Question 1
        CopyObjectService copyObjectService = new CopyObjectService();
        Object objNew = copyObjectService.copy(users);
        System.out.println(objNew);

        //Question 3
        ToJsonService toJsonService = new ToJsonService();
        String jsonStringForObje = toJsonService.toJson(users);
        System.out.println("Json is:---> " + jsonStringForObje);

        //Question 2 public <T> T toObj(String json,Class<T> cls){
        Users companyClass = new Users();
        Class clazzToObj = companyClass.getClass();
        ToObject toObject = new ToObject();
        Object strToObj2 = toObject.toObj(jsonStringForObje, clazzToObj);
        System.out.println(strToObj2);
    }
}
