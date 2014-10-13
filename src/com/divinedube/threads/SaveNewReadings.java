package com.divinedube.threads;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.divinedube.helpers.MeterUtils;
import com.divinedube.models.MeterReadingsContract;

/**
 * Created by Divine Dube on 2014/10/04.
 */
public class SaveNewReadings extends Thread{ //todo change to threadSaving

    private static final String TAG = SaveNewReadings.class.getSimpleName();
    private String[]  theReadingColumn = {MeterReadingsContract.Column.READING};
    Context context;
    double inputReading;
    String day;
    String time;
    String note;


    public SaveNewReadings(Context context, double inputReading, String day, String time, String note){
       this.context = context;
       this.inputReading = inputReading;
       this.day = day;
       this.time = time;
       this.note = note;
    }

    @Override

    //todo might even notify a person that the readings are getting low
    public void run() {
           ContentResolver contentResolver =  context.getContentResolver();
           Cursor readingColumn = contentResolver.query(MeterReadingsContract.CONTENT_URI,
                   theReadingColumn , null, null, MeterReadingsContract.DEFAULT_SORT);

        ContentValues values = new ContentValues();

           if (readingColumn.moveToFirst()){   //todo you can do better here
               double lastReading = readingColumn.getDouble(0);
               //this means that if a person puts in a value that is
               // more than or equal to the current one in the database then
               // this means that they have recharged
               if (inputReading >= lastReading){
                   values.put(MeterReadingsContract.Column.DAY, day);
                   values.put(MeterReadingsContract.Column.TIME, time);
                   values.put(MeterReadingsContract.Column.READING, inputReading);
                   values.put(MeterReadingsContract.Column.NOTE, note);
                   values.put(MeterReadingsContract.Column.CREATED_AT, System.currentTimeMillis());
                   values.put(MeterReadingsContract.Column.RECHARGED, inputReading);

                   Log.d(TAG, "the last reading in meter is  " + lastReading + "the given reading is " +
                           inputReading) ;
                   Log.d(TAG, "You have just recharged with " +  inputReading);

                   MeterUtils.toast(context, "You have just recharged with " + inputReading);
               }else {
                   values.put(MeterReadingsContract.Column.DAY, day);
                   values.put(MeterReadingsContract.Column.TIME, time);
                   values.put(MeterReadingsContract.Column.READING, inputReading);
                   values.put(MeterReadingsContract.Column.NOTE, note);
                   values.put(MeterReadingsContract.Column.CREATED_AT, System.currentTimeMillis());
               }
           }else {
               values.put(MeterReadingsContract.Column.DAY, day);
               values.put(MeterReadingsContract.Column.TIME, time);
               values.put(MeterReadingsContract.Column.READING, inputReading);
               values.put(MeterReadingsContract.Column.NOTE, note);
               values.put(MeterReadingsContract.Column.CREATED_AT, System.currentTimeMillis());
           }
        Log.d(TAG, "inserting "  + time + inputReading + note + " into db " +
                MeterReadingsContract.TABLE + " at "+ System.currentTimeMillis());
        Uri uri =  contentResolver.insert(MeterReadingsContract.CONTENT_URI,values);
        Log.d(TAG, "and the data is inserted at  " +  uri);
    }
}

