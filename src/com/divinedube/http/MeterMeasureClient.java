package com.divinedube.http;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.divinedube.metermeasure.BagOfValuesArray;
import com.divinedube.metermeasure.MeterReadingsContract;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import com.savagelook.android.UrlJsonAsyncTask;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Divine Dube on 2014/07/15.
 */
public class MeterMeasureClient extends IntentService {

    private static final String TAG = "MeterMeasureClient";
    private static final String CREATE_METER_END_POINT_URL = "http://192.168.56.1/meters.json";

    BagOfValuesArray bva = new BagOfValuesArray();
    Gson gson = new GsonBuilder().serializeNulls().create();
    String jsonOfDb;
    JSONObject holder = new JSONObject();

    public MeterMeasureClient() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        //todo select only those field which have not been pushed to the server so have to have a boolean field in meter.
        Cursor c = getContentResolver().query(MeterReadingsContract.CONTENT_URI, null, null, null, MeterReadingsContract.DEFAULT_SORT);

        int numRows = c.getCount();
        Log.d(TAG, "there are number rows in meter currently is: " + numRows);

        for (int i = 0; i < numRows; i++) { //get the gson representative of the meter db
            c.moveToNext();
            int _id = c.getInt(0);
            String day = c.getString(1);
            String time = c.getString(2);
            int reading = c.getInt(3);
            String note = c.getString(4);
            long createdAt = c.getLong(5);

            bva.setMap(_id, day, time, reading, note, createdAt);
            bva.putArr();
        }
        //TODO look for better ways to do this and the above or is there?
        jsonOfDb = gson.toJson(bva);
        Log.d(TAG, jsonOfDb);

            PullOrPushReadings pushReadings = new PullOrPushReadings(null);
            pushReadings.execute(CREATE_METER_END_POINT_URL);
    } //end of handle intent

    private class PullOrPushReadings extends UrlJsonAsyncTask {


        public PullOrPushReadings(Context context) {
            super(context);
        }
        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urls[0]);
          //  JSONObject meterObj = new JSONObject(); no need for this since i have my object already from GSON
            String response = null;
            JSONObject json = new JSONObject();
            try {
                try {
                    JSONObject jsObj = new JSONObject(jsonOfDb);
                    JSONArray js = jsObj.getJSONArray("meter");
                    JSONObject myFinalJsnObj;
                    int numObjectsInArray = js.length();
                    for (int i = 0; i < numObjectsInArray; i++) { //todo maybe also remove this loop too
                    json.put("success", false);   //todo remove this out of here its constant
                    json.put("info", "big failure");
                    //this is some additional processing
                        Log.d(TAG, "the id of the object at index i is " + js.getJSONObject(i).getString("id")
                                + "the length of the array is " + numObjectsInArray);
                            myFinalJsnObj = js.getJSONObject(i);
                           holder.put("meter", myFinalJsnObj);

                        StringEntity se = new StringEntity(holder.toString());
                        post.setEntity(se);
                        post.setHeader("Accept", "application/json");
                        post.setHeader("Content-Type", "application/json");

                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        response = client.execute(post,responseHandler);
                        json = new JSONObject(response);
                        }
                    } catch (HttpResponseException hre) {
                        hre.printStackTrace();
                        Log.e(TAG, "client protocol " + hre);
                }catch (IOException ioe){
                    ioe.printStackTrace();
                    Log.e(TAG, "IO " + ioe);
                }
            }catch (JSONException jsne){
                jsne.printStackTrace();
                Log.e(TAG, "JSON " + jsne);
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getBoolean("success")){
                    Toast.makeText(context,"Uploaded your reading to the server",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context,"Failed to Uploaded your reading to the server",Toast.LENGTH_LONG).show();
                }
            }catch (JSONException jsne){
                jsne.printStackTrace();
            }finally {
                super.onPostExecute(json);
            }
        }
    }
}