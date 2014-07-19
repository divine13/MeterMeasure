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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.savagelook.android.UrlJsonAsyncTask;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Divine Dube on 2014/07/15.
 */
public class MeterMeasureClient extends IntentService{

    private static final String TAG="MeterMeasureClient";

    BagOfValuesArray bva = new BagOfValuesArray();
    Gson gson = new GsonBuilder().serializeNulls().create();
    String jsonOfDb;
    JsonElement jsonElement;

    public MeterMeasureClient() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //todo select only those field which have not been pushed to the server so have to have a boolean field in meter.
        Cursor c = getContentResolver().query(MeterReadingsContract.CONTENT_URI,null,null,null,MeterReadingsContract.DEFAULT_SORT);

        int numRows = c.getCount();
        Log.d(TAG, "there are number rows in meter currently is: " + numRows);

        for (int i = 0; i < numRows; i++){ //get the gson representative of the meter db
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

        jsonElement =  gson.toJsonTree(bva);
        jsonOfDb = gson.toJson(bva);
        Log.d(TAG, jsonOfDb);

        try {
            JSONObject jsObj = new JSONObject(jsonOfDb);
            int val = jsObj.length();
           JSONArray js =  jsObj.getJSONArray("meter");
            int leng = js.length();

            Log.d(TAG, "the id of the object at index 1 is " + js.getJSONObject(0).getString("id") + "the length of the array is " + leng);

            Log.d(TAG, "the number of object is " + val);
        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    private class PullOrPushReadings extends UrlJsonAsyncTask {

//
        public PullOrPushReadings(Context context) {
            super(context);
        }
//        @Override
//        protected JSONObject doInBackground(String... urls) {
//            DefaultHttpClient client = new DefaultHttpClient();
//            HttpPost post = new HttpPost(urls[0]);
//            JSONObject holder = new JSONObject();
//            JSONObject meterObj = new JSONObject();
//            String response = null;
//            JSONObject json = new JSONObject();
//
////            try{
////                try {
////                    json.put("success", true);
////                    json.put("info", "from android to server");
////
////
////                }
////            }
//        }
    }
}
