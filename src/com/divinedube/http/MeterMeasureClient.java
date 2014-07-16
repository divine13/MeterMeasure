package com.divinedube.http;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.divinedube.metermeasure.BagOfValuesArray;
import com.divinedube.metermeasure.MeterReadingsContract;
import com.google.gson.Gson;

/**
 * Created by Divine Dube on 2014/07/15.
 */
public class MeterMeasureClient extends Activity{

    private static final String TAG="MeterMeasureClient";

    BagOfValuesArray bva = new BagOfValuesArray();
    Gson json = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Cursor c = getContentResolver().query(MeterReadingsContract.CONTENT_URI,null,null,null,MeterReadingsContract.DEFAULT_SORT);

        while (c.moveToNext()){

            int _id = c.getInt(0);
            String day = c.getString(1);
            String time = c.getString(2);
            int reading = c.getInt(3);
            String note = c.getString(4);
            long created_at = c.getLong(5);

            bva.putValuesPairs("id", _id);
            bva.putValuesPairs("day", day);
            bva.putValuesPairs("time", time);
            bva.putValuesPairs("reading", reading);
            bva.putValuesPairs("note", note);
            bva.putValuesPairs("created_at", created_at);

            bva.addValuePairs();


                String jsonOfDb = json.toJson(bva);
                Log.d(TAG, jsonOfDb);

        }

    }
}
