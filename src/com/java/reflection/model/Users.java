package com.java.reflection.model;

import com.java.reflection.MyTransition;

public class Users {


    private int userId;
    private String userName;
    private Company company;

    public Users() {
    }

    @MyTransition()
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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
