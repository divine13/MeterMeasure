package com.divinedube.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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

   public double stringToDouble(String string){
       if(isStringNumber(string)){
           return  Double.valueOf(string);
       }else{
           return 0;
       }
   }

  public static double tariff(){ //in the future might get it from some place server?
      return 0.838; //in rands this equal 1kw
  }

  public static double calculateRechargeAmountInRandsFor(double reading){
         double amount = tariff() * reading;
         double vat = amount / 14;


      return amount + (vat*2);
  }

    public static String formatDoubleToTwoDecimalString(double number){
        double theNumber = calculateRechargeAmountInRandsFor(number);

        return twoDecimalDoubleFormatter(theNumber);
    }

    public static String twoDecimalDoubleFormatter(double number){
        return  String.format("%.2f", number);
    }


    /*currently this method only works with 1 previous months
     * i need a way to let the method know how many months are they apart */
    public static int getDaysAgo(String fromDay, int toOlderDay, int toOlderDayMonth){  //a bit dom
        int daysAgo = 0;
        char charAt0 = fromDay.charAt(0);
        char charAt1 = fromDay.charAt(1);
        String sDay = "" +  charAt0 + charAt1;
        sDay = sDay.trim();
        int fromDayInt = Integer.valueOf(sDay);

        if (fromDayInt >= toOlderDay){ //this means that both dates are still in the same month
            return fromDayInt - toOlderDay; // return 0 or number not less more than from
        }else{ // deal with feb some other day
            for (int i = 0; i < 13; i++ ){  //13 is the number of month.
                int toMonthEndDaysLeft;
                switch (toOlderDayMonth){
                    case 0: //jan ends with 31
                        toMonthEndDaysLeft = 31 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                    break;
                    case 1: //feb
                        toMonthEndDaysLeft = 28 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 2: //march
                        toMonthEndDaysLeft = 31 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 3:
                        toMonthEndDaysLeft = 31 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 4:
                        toMonthEndDaysLeft = 30 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 5: //may
                        toMonthEndDaysLeft = 31 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 6: //june
                        toMonthEndDaysLeft = 30 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 7:
                        toMonthEndDaysLeft = 31 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 8: //august
                        toMonthEndDaysLeft = 31 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 9:
                        toMonthEndDaysLeft = 30 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 10:
                        toMonthEndDaysLeft = 31 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 11:
                        toMonthEndDaysLeft = 30 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                    case 12:
                        toMonthEndDaysLeft = 31 - toOlderDay;
                        daysAgo = fromDayInt + toMonthEndDaysLeft;
                        break;
                }
            }
        }
        return daysAgo;
    }
}
