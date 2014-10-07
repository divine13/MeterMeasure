package com.divinedube.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.divinedube.models.MeterReadingsContract;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Divine Dube on 2014/07/04.
 */

public class MeterUtils {

    //    public static final String ROOT_URL = "http://sleepy-scrubland-6302.herokuapp.com";
    public static final String ROOT_URL = "http://192.168.56.1:3000";

    //  public static final String CREATE_METER_END_POINT_URL = ROOT_URL + "/meters.json";

    private static String TAG = MeterUtils.class.getSimpleName();


    Calendar calendar = Calendar.getInstance(); //not perfect not chained good
    private int hour;
    private int minute;
    private int dayOfWeek;

    public static boolean emailValidate(String email) {
        String VALID_EMAIL_REGEX = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(VALID_EMAIL_REGEX);
        return pattern.matcher(email).matches();
    }

    public static boolean isMeterNumberAcceptable(String number){
        return isStringNumber(number) && (number.trim().length() == 0 || number.trim().length() == 11);
    }


    public static boolean isStringNumberLessThan(String number, int compareInt) throws NumberFormatException{ //no need for this but....
        if (isStringNumber(number)){
            int numberAsInt = Integer.valueOf(number);
            return numberAsInt < compareInt;
        }else
            return false;
    }

    public String getCurrentTime() {
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        return hour + ":" + minute;
    }

    public double getCurrentHour() {
        return hour;
    }

    public double getCurrentMinute() {
        return minute;
    }

    public int getDayOfWeek() {
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }

    public void setUUID(Context context) {
        String uuid = UUID.randomUUID().toString();
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = s.edit();
        e.putString("uuid", uuid);
        boolean yes = e.commit();
        Log.d("MeterUtils", yes ? "created uuid " + uuid : "could not add uuid");
        Log.e(TAG, s.getString("uuid", "no uuid set ") + " UUID must equal " + uuid);
    }

    public String getID(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String uuid = prefs.getString("uuid", "");
        Log.e(TAG, "##$$THE !!!%^uuid is " + uuid);
        return uuid;
    }

    public static String changeT0Json(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }


    //did this to avoid using gson to dig for the success value
    public static boolean easyForSuccessCheck(String response) {
        return response.contains("\"success\":true");
    }

    public boolean isSignedIn(SharedPreferences prefs) {
        String token = prefs.getString("rememberToken", "nothing");
        Log.d(TAG, "the token is " + token);
        String signedIn = prefs.getString("signedIn", "nothing");
        Log.d(TAG, "am i signed in? " + signedIn);
        return ((!(token.equals("nothing"))) && signedIn.equals("yes"));
    }

    public String getEmail(SharedPreferences prefs) {
        return prefs.getString("email", "nothing");
    }

    public String getPassword(SharedPreferences prefs) {
        return prefs.getString("password", "nothing");
    }

    public static boolean isStringNumber(String number) {

        if ((number == null)) {
            return false;
        } else {
            String validator = "\\d+";

            Pattern pattern = Pattern.compile(validator);

            Matcher m = pattern.matcher(number);

            return m.matches();
        }
    }

    public static void toast(final Context context, final CharSequence msg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String intMonthToString(int month){
        String[] months ={
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec",
        "UnDec"}; //for leap years

        return months[month];
    }

    static String[]  daysOfWeek = {
            "Sun","Mon","Tue","Wed","Thu","Fri", "Sat"
    };
    public static String intDayToString(int day){

        return daysOfWeek[day];
    }
    /**
      this would give the days in the range of 0-6 0 = Sun and 6= Sat*/
    public static int monthDaysToIntWeekDays(String dayOfMonth){
        int i;
        for (i=0; i < daysOfWeek.length; i++){
            if (dayOfMonth.equals(daysOfWeek[i])){
               break;
            }
        }
        return  i;
    }



}
