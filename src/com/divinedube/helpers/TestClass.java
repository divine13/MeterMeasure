package com.divinedube.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Divine Dube on 2014/09/13.
 */
public class TestClass {

       public static void main(String args[]){
          // double number = 12345678901234567890;

           String emailValidator = "\\d+";

           String st = "sdhsdhs      ssdjkshdhks   dsdb    ".trim();

           Pattern pattern = Pattern.compile(emailValidator);

           Matcher m  = pattern.matcher("110");

          System.out.print(st + m.matches() );

           int i = 1;


           BigInteger integer = new BigInteger("12345678901234567890");

//          Long f  = 0L ;
//           Float l =  Float.MAX_VALUE;
//         try {
//             String s = "12div";
//             f =  Long.valueOf(s);
//             System.out.println(f + " and "  + l +"the bit dec is "+ integer.toString());
//         }catch (NumberFormatException nfe){
//            System.out.println("Not number");
//         }
//
//
//           System.out.println(f + " and "  + l +"the bit dec is "+ integer.toString());

       }
}
