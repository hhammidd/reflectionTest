package com.java.reflection;

import com.java.reflection.model.AdminUser;
import com.java.reflection.model.City;
import com.java.reflection.model.Company;
import com.java.reflection.model.Users;
import com.java.reflection.services.ReflectionServices;

import java.lang.reflect.InvocationTargetException;

public class MainReflection {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        //TODO check for boolean--> . toObj
        Users users = new Users();
        users.setUserId(100);
        users.setUserName("HAMID");
        users.setRegister(true);
        users.setUserAmount(1.02);
        //users.setExpirationDate(java.sql.Date.valueOf(LocalDate.now()));
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
        ReflectionServices reflectionServices = new ReflectionServices();
        Object objNew = reflectionServices.copy(users);
        //System.out.println(objNew);

        //Question 3
        String jsonStringForObje = reflectionServices.toJson(users);
        System.out.println("Json is:---> " + jsonStringForObje);

        //Question 2 public <T> T toObj(String json,Class<T> cls){
        Users companyClass = new Users();
        //jsonStringForObje = "{\"register\": false,\"userName\": \"hamid\",\"userId\": 100,\"userAmount\": 1.02,\"company\": {\"country\": \"IT\",\"id_company\": 1,\"name_company\": \"be\",\"city\": {\"city_Name\": \"MILANO\",\"city_id\": 1001}}}";
        Class clazzToObj = companyClass.getClass();
        Object strToObj2 = reflectionServices.toObj(jsonStringForObje, clazzToObj);
        System.out.println(strToObj2);

        //TODO new Example
        AdminUser adminUser = new AdminUser();

        adminUser.setAdminLevel(2);
        adminUser.setFirstName("hamid");
        adminUser.setLastName("shafie");

        AdminUser ac = (AdminUser) reflectionServices.copy(adminUser);
        System.out.println(reflectionServices.toJson(adminUser));

        AdminUser aj = (AdminUser) ReflectionServices.toObj("{\"firstName\":\"hamid\",\"lastName\":\"shafie\",\"adminLevel\":3}", AdminUser.class);
        System.out.println(aj);
    }
}
