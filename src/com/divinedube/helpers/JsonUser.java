package com.divinedube.helpers;

import com.google.gson.Gson;

import java.util.Hashtable;

/**
 * Created by Divine Dube on 2014/09/12.
 */
public class JsonUser {

    private transient String email;
    private transient String password;
    private transient String meterNumber;
    private transient String peopleInHouse;
//    private transient String passwordConfirmation;

    public JsonUser(String email, String password, String meterNumber, String peopleInHouse){
        this.email = email;
        this.password = password;
        this.peopleInHouse = peopleInHouse;
        this.meterNumber = meterNumber;
       // this.passwordConfirmation = passwordConfirmation;
    }

    Hashtable<String, Object> user = new Hashtable<String, Object>();

    public void addValues(){
        user.put("email", email);
        user.put("password", password);
        user.put("meter_number", meterNumber);
        user.put("people_in_house", peopleInHouse);
       // user.put("password_confirmation", passwordConfirmation);
    }

//    public static void main(String args[]){
//        Gson gson = new Gson();
//        JsonUser user = new JsonUser("dubedivine@gmail.com", "password");
//        user.addValues();
//
//        System.out.println( gson.toJson(user));
//
//    }
}
