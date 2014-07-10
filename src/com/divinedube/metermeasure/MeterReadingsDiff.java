package com.divinedube.metermeasure;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Divine Dube on 2014/07/04.
 */

//Todo Strap this class
public class MeterReadingsDiff extends FragmentActivity {

    SharedPreferences prefs;

    double reading;
    String defaultTime;
    String day;
    String currentTime;
    Cursor mCursor;
    ArrayMap<String, Double> arrayMap;

    //select all rows with the the default time and then just get the reading
    //
    private final String TAG = MeterReadingsDiff.class.getSimpleName();
    String[] projection =
            {
                    MeterReadingsContract.Column.ID,
                    MeterReadingsContract.Column.READING,
                    MeterReadingsContract.Column.TIME,
                    MeterReadingsContract.Column.DAY
            };


    @Override
    public void onCreate(Bundle saveInstance){
        super.onCreate(saveInstance);
        MeterUtils tool = new MeterUtils();
        currentTime = tool.getCurrentTime();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        defaultTime = prefs.getString("time", currentTime);
        if (defaultTime.equals(currentTime) || TextUtils.isEmpty(defaultTime)){
           Toast.makeText(this,"please set the default time that you always check your meter reading  ",
                   Toast.LENGTH_LONG).show();
            finish();
        }else {
            Toast.makeText(this, "Generating your meter reading stats using  this " + defaultTime + "  time",
                    Toast.LENGTH_LONG).show();
        }
        String[] selectionArgs = {defaultTime};
       mCursor = getContentResolver().query //TODO This should be done using a Loader leave it for now
               (
                       MeterReadingsContract.CONTENT_URI, null,
                       MeterReadingsContract.THE_DIFF_SELECTION_STATEMENT,
                       selectionArgs,
                       MeterReadingsContract.NORMAL_SORT_ORDER
               );

        int noC = mCursor.getCount();
        ContentValues values = new ContentValues();
        while (mCursor.moveToNext()){
            int i = mCursor.getPosition();
            Log.d(TAG,"now At " + i );
            reading = mCursor.getDouble(3);
            day = mCursor.getString(1);
            values.put(day,reading);
            arrayMap = new ArrayMap<String, Double>(); //stupid
            arrayMap.put(day, reading);
               Log.d(TAG," got  " + reading + " for  " + day);
               double dayReading = arrayMap.get(day);
               Log.d(TAG, " reading: " + dayReading + " for: " + arrayMap.keyAt(i));
        }

        getContentResolver().insert(MeterReadingStatsContract.STATES_CONTENT_URI,values);
        Toast.makeText(this, "number of columns returned: " + noC, Toast.LENGTH_LONG).show();


    }
}

