package com.divinedube.metermeasure;

import java.util.Calendar;

/**
 * Created by Divine Dube on 2014/07/04.
 */
public class MeterUtils {
Calendar calendar = Calendar.getInstance();
    private int hour;
    private int minute;

    public String getCurrentTime(){
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        return hour + ":" + minute;
    }
    public int getCurrentHour(){
      return hour;
    }

    public int getCurrentMinute(){
        return minute;
    }
}
