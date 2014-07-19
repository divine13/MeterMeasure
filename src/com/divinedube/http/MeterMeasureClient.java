package com.divinedube.http;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.divinedube.metermeasure.BagOfValuesArray;
import com.divinedube.metermeasure.MeterReadingsContract;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.savagelook.android.UrlJsonAsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Divine Dube on 2014/07/15.
 */
public class MeterMeasureClient extends IntentService{

    private static final String TAG="MeterMeasureClient";

    BagOfValuesArray bva = new BagOfValuesArray();
    Gson json = new GsonBuilder().serializeNulls().create();
    String jsonOfDb;
    public MeterMeasureClient() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //todo select only those field which have not been pushed to the server so have to have a boolean field in meter.
        Cursor c = getContentResolver().query(MeterReadingsContract.CONTENT_URI,null,null,null,MeterReadingsContract.DEFAULT_SORT);

        int numRows = c.getCount();
        Log.d(TAG, "there are number rows in meter currently is: " + numRows);

        for (int i = 0; i < numRows; i++){ //get the json representative of the meter db
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

        jsonOfDb = json.toJson(bva);
        Log.d(TAG, jsonOfDb);



        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.56.1/meters.json");
        try {
            Log.d(TAG, "Trying to push data to the server");
            StringEntity stringEntity = new StringEntity(jsonOfDb);
            Log.d(TAG, "NEEDED DATA*****  now here 1");
            stringEntity.setContentType("application/json;charset=UTF-8");
            Log.d(TAG, "NEEDED DATA*****  now here 2");
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            Log.d(TAG, "NEEDED DATA*****  now here 3");
            post.setHeader("Accept", "application/json");
            Log.d(TAG, "NEEDED DATA*****  now here 4");
            post.setEntity(stringEntity);
            Log.d(TAG, "NEEDED DATA*****  now here 5");
            HttpResponse response = client.execute(post);
            Log.d(TAG, "NEEDED DATA***** " + response.toString());
        }catch (UnsupportedEncodingException ese){
            ese.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private class PullOrPushReadings extends UrlJsonAsyncTask {

        public PullOrPushReadings(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            return super.doInBackground(urls);
        }
    }
}
