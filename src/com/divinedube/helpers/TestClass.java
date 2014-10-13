package com.divinedube.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Divine Dube on 2014/09/13.
 */
public class TestClass {

       public static void main(String args[]){

           Formatter formatter = new Formatter();
           System.out.println(formatter.format("%.2s", "12333.5566"));


       }
}
