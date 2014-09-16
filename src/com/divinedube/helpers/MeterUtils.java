package com.divinedube.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.UUID;

/**
 * Created by Divine Dube on 2014/07/04.
 */

public class MeterUtils{

//    public static final String ROOT_URL = "http://sleepy-scrubland-6302.herokuapp.com";
    public static final String ROOT_URL = "http://192.168.56.1:3000";
  //  public static final String CREATE_METER_END_POINT_URL = ROOT_URL + "/meters.json";

    private static String TAG = MeterUtils.class.getSimpleName();

    Calendar calendar = Calendar.getInstance();
    private int hour;
    private int minute;
    private int dayOfWeek;

    public String getCurrentTime(){
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        return hour + ":" + minute;
    }
    public double getCurrentHour(){
      return hour;
    }

    public double getCurrentMinute(){
        return minute;
    }

    public  int getDayOfWeek(){
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }
    public void setUUID(Context context){
        String uuid =  UUID.randomUUID().toString();
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e =  s.edit();
        e.putString("uuid", uuid);
        boolean yes = e.commit();
        Log.d("MeterUtils",yes ? "created uuid " + uuid : "could not add uuid");
        Log.e(TAG, s.getString("uuid", "no uuid set ") + " UUID must equal " + uuid);
    }

    public String getID(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String uuid = prefs.getString("uuid", "");
        Log.e(TAG, "##$$THE !!!%^uuid is " + uuid);
        return uuid;
    }
   public static String changeT0Json(Object obj){
        Gson gson = new Gson();
      return gson.toJson(obj);
   }


   //did this to avoid using gson to dig for the success value
   public static boolean easyForSuccessCheck(String response){
       return response.contains("\"success\":true");
   }

    public boolean isSignedIn(SharedPreferences prefs){
        String token = prefs.getString("rememberToken", "nothing");
        Log.d(TAG, "the token is " + token);
        String signedIn = prefs.getString("signedIn", "nothing");
        Log.d(TAG, "am i signed in? " + signedIn );
        return ((!(token.equals("nothing"))) &&  signedIn.equals("yes"));
    }

    public  String getEmail(SharedPreferences prefs){
        return  prefs.getString("email", "nothing");
    }

    public  String getPassword(SharedPreferences prefs){
        return  prefs.getString("password", "nothing");
    }

}
