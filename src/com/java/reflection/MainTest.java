package com.java.reflection;

public class MainTest {
    public static void main(String[] args) {
        String jsonStringForObje = "ss{cc}bb},ss";

        int equalPrantezi = 1;
        int stopPrantesi = 0;
        while (true){
            for (int k = 0; k < jsonStringForObje.length(); k++){
                char ch = jsonStringForObje.charAt(k);
                if (ch == '{'){
                    equalPrantezi +=1;
                }else if (ch == '}'){
                    equalPrantezi -=1;
                }
                if (equalPrantezi == 0){
                    stopPrantesi = k;
                    break;
                }
            }
            if (equalPrantezi == 0){
                jsonStringForObje.substring(0,stopPrantesi);
                System.out.println(jsonStringForObje.substring(0,stopPrantesi));
                break;
            }
        }

    }

}
