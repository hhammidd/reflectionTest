package com.java.reflection.model;

import com.java.reflection.MyTransition;

import java.util.Date;

public class Users {


    private int userId;
    private double userAmount;
    private String userName;
    private Company company;
    private boolean register;
    //TODO date in toObj will not work
    //private Date expirationDate;

    public Users() {
    }


    @MyTransition("noToObj")
    public double getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(double userAmount) {
        this.userAmount = userAmount;
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @MyTransition("noToObj")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
