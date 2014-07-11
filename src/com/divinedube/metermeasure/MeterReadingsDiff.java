package com.divinedube.metermeasure;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
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


        //Todo fix the STATES_* to STATS_*
        Cursor cursor = getContentResolver().query(MeterReadingStatsContract.STATES_CONTENT_URI,null,null,null,null);
        int noC = mCursor.getCount();
        ContentValues values = new ContentValues();
        while (mCursor.moveToNext()){
            int i = mCursor.getPosition();
            Log.d(TAG,"now At " + i );
            reading = mCursor.getDouble(3);

            day = mCursor.getString(1);
//            double reading2 = 0;
//            String day2 ="";
//            if (mCursor.moveToNext()){
//                reading2 = mCursor.getDouble(3);
//                day2 = mCursor.getString(1);
//            }
            values.put(MeterReadingStatsContract.Column.ID, i);
            values.put(MeterReadingStatsContract.Column.READING_FOR_DAY_1,reading);
//            values.put(MeterReadingStatsContract.Column.READING_FOR_DAY_2, reading2);
            values.put(MeterReadingStatsContract.Column.NAME_FOR_DAY_1, day);

//            values.put(MeterReadingStatsContract.Column.READING_FOR_DAY_2, day2);

               Log.d(TAG," got  " + reading + " for  " + day);
//            Log.d(TAG," got  " + reading2 + " for  " + day2);

            if(values.size() == 0){
                Toast.makeText(this,"please record your meter reading first before checking your meter stats", Toast.LENGTH_LONG);
                finish();
            }else {
                getContentResolver().insert(MeterReadingStatsContract.STATES_CONTENT_URI, values);
                Toast.makeText(this, "number of columns returned: " + noC, Toast.LENGTH_LONG).show(); //Todo remove this in production
            }
//            else{
//                getContentResolver().update(MeterReadingStatsContract.STATES_CONTENT_URI,values, null, null);
//                Log.d(TAG, "in Else");
//            }
        }
    }
    
}

