package com.divinedube.http;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.divinedube.metermeasure.BagOfValuesArray;
import com.divinedube.metermeasure.MeterReadingsContract;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Divine Dube on 2014/07/15.
 */
public class MeterMeasureClient extends Activity{

    private static final String TAG="MeterMeasureClient";

    BagOfValuesArray bva = new BagOfValuesArray();
    Gson json = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Cursor c = getContentResolver().query(MeterReadingsContract.CONTENT_URI,null,null,null,MeterReadingsContract.DEFAULT_SORT);

        int numRows = c.getCount() -1;
        Log.d(TAG, "there are number rows in meter currently is: " + numRows + " minus 1");

        for (int i = 0; i < numRows; i++){
            c.moveToNext();
            int _id = c.getInt(0);
            String day = c.getString(1);
            String time = c.getString(2);
            int reading = c.getInt(3);
            String note = c.getString(4);
            long createdAt = c.getLong(5);

            bva.setMap(_id, day, time, reading, note,createdAt);
            bva.putArr();
        }

        String jsonOfDb = json.toJson(bva);
        Log.d(TAG, jsonOfDb);
    }
}
