package com.divinedube.http;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.divinedube.metermeasure.BagOfValuesArray;

import com.divinedube.metermeasure.MainActivity;
import com.divinedube.metermeasure.MeterReadingsContract;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Divine Dube on 2014/07/15.
 */

  public class MeterMeasureClient extends IntentService {

    private static final String TAG = "MeterMeasureClient";
    public static final String ROOT_URL = "http://192.168.56.1:3000";
    public static final String CREATE_METER_END_POINT_URL = ROOT_URL + "/meters.json";
    public static final String GET_LAST_METER_URL = ROOT_URL + "/meters/last.json;";


    BagOfValuesArray bva = new BagOfValuesArray();
    Gson gson = new GsonBuilder().serializeNulls().create();

    JSONObject holder = new JSONObject();


    public MeterMeasureClient() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        JSONObject jsObj =  getLastReading(TAG, GET_LAST_METER_URL);
        String[] projection = {MeterReadingsContract.Column.CREATED_AT};
        Cursor cursor = getContentResolver().query(MeterReadingsContract.CONTENT_URI,projection,null,null,MeterReadingsContract.NORMAL_SORT_ORDER);
        try {
            if (cursor.moveToLast() && (isConnectedToServer(TAG))){

            String serverLastRecord = jsObj.getJSONObject("data").getJSONObject("meter").getString("made_at");
            Long serverLastRecordAsLong = new Long(serverLastRecord);
           Log.d(TAG, "the server last record is " + serverLastRecord);
            Log.d(TAG, "the date of the last record is " + serverLastRecordAsLong);
//            cursor.move((cursor.getCount())-1);
            long phoneLastRecordAsLong = cursor.getLong(0);
            Log.d(TAG, "we are currently selecting by " + phoneLastRecordAsLong + "count is " + cursor.getCount());
            if (serverLastRecordAsLong > phoneLastRecordAsLong){
                //todo download make method in server to give the record that are currently newer than the phone`s current
                //todo and actually insert them
                Log.d(TAG,"downloading all the newest readings from the server and inserting them in the phone db to keep up with server");
                download(phoneLastRecordAsLong);
            }else if (serverLastRecordAsLong < phoneLastRecordAsLong){
                Log.d(TAG, "true the server data needs uploading and will do just that right now uploading...");
                String[] serverLastRecordAsArr = {serverLastRecord};
                upload(serverLastRecordAsArr);
            }
        }else{
          toastError();
          }
        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    public JSONObject getLastReading(String classTag, String  getLastMeterUrl){
        String  resp; //"{\"success\":false,\"info\":could get last meter reading\"\",\"data\":{\"meter\":{}}}"; // did this so that i could just check once
        JSONObject jsObj = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getLastMeterUrl);
            ResponseHandler<String> getResponse = new BasicResponseHandler();
            resp = httpClient.execute(httpGet, getResponse); // todo make this var Global
            jsObj = new JSONObject(resp); //good because only this one will be created
            Log.d(classTag, "the last val on the server is " + jsObj.toString());
        }catch (IOException ioe){
            ioe.printStackTrace();
            toastError();
        }catch (JSONException jse){
            jse.printStackTrace();
        }
        return jsObj;
    }

    public  boolean isConnectedToServer(String tag){
        boolean yes;
        try {
            JSONObject jsObj = new MeterMeasureClient().getLastReading(tag, GET_LAST_METER_URL);
            jsObj.getJSONObject("data").getJSONObject("meter").getString("made_at");
            yes = true;
        }catch (NullPointerException npe){
            npe.printStackTrace();
            toastError("please make sure that you are truly connected to the internet");
            yes = false;
        } catch (JSONException jsne) {
            jsne.printStackTrace();
            toastError("please make sure that you are truly connected to the internet");
            yes = false;
        }
        return yes;
    }

    public void download(long phoneLastRecord){

        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = ROOT_URL + "/meters" + "/" + phoneLastRecord + "/newer.json";
        String response;
        HttpPut httpPut = new HttpPut(url);
        ResponseHandler<String> stringResponseHandler = new BasicResponseHandler();
        try {
            response = httpClient.execute(httpPut,stringResponseHandler);
            JSONObject jsonObject = new JSONObject(response);
            Log.d(TAG, "Got this from server " + jsonObject.toString());
           JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("meters");  //todo make a method for this
           int length =  jsonArray.length();
            for (int i = 0; i < length; i++){
                JSONObject jsonArrayJSONObject = jsonArray.getJSONObject(i);
                String day = jsonArrayJSONObject.getString("day");
                String time = jsonArrayJSONObject.getString("time");
                double reading = jsonArrayJSONObject.getDouble("reading");
                String note = jsonArrayJSONObject.getString("note");
                String madeAt = jsonArrayJSONObject.getString("made_at");

                ContentValues values = new ContentValues();
                values.put(MeterReadingsContract.Column.DAY, day);
                values.put(MeterReadingsContract.Column.TIME, time);
                values.put(MeterReadingsContract.Column.READING, reading);
                values.put(MeterReadingsContract.Column.NOTE, note);
                values.put(MeterReadingsContract.Column.CREATED_AT, madeAt);
                Uri uri = getContentResolver().insert(MeterReadingsContract.CONTENT_URI, values);
                Log.d(TAG, "inserting " + uri);
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    //todo too complicated or too long
    public void upload(String[] selectionArgs){
        //todo select only those field which have not been pushed to the server so have to have a boolean field in meter.
        Cursor c = getContentResolver().query(MeterReadingsContract.CONTENT_URI, null, MeterReadingsContract.THE_FRESHEST_SELECTION_STATEMENT, selectionArgs, MeterReadingsContract.DEFAULT_SORT);

        int numRows = c.getCount();
        Log.d(TAG, "there are number rows in meter currently is: " + numRows);

        for (int i = 0; i < numRows; i++) { //get the gson representative of the meter db
            c.moveToNext();
            int _id = c.getInt(0); //todo remove this one
            String day = c.getString(1);
            String time = c.getString(2);
            double reading = c.getDouble(3);
            String note = c.getString(4);
            String madeAt = c.getString(5);

            bva.setMap(_id, day, time, reading, note, madeAt);
            bva.putArr();
        }
        String jsonOfDb  = gson.toJson(bva);
        Log.d(TAG, jsonOfDb);
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(CREATE_METER_END_POINT_URL);
        //  JSONObject meterObj = new JSONObject(); no need for this since i have my object already from GSON
        String response;
        JSONObject json = new JSONObject();
        try {     //todo enclose this in if statements
            try {
                JSONObject jsObj = new JSONObject(jsonOfDb); //good because only this one will be created
                JSONArray js = jsObj.getJSONArray("meter");
                JSONObject myFinalJsnObj;
                int numObjectsInArray = js.length();
                for (int i = 0; i < numObjectsInArray; i++) {        //todo maybe also remove this loop too
                    json.put("success", false);                         //todo remove this out of here its constant
                    json.put("info", "big failure");
                    Log.d(TAG, "the id of the object at index i is " + js.getJSONObject(i).getString("id")
                            + "the length of the array is " + numObjectsInArray);
                    myFinalJsnObj = js.getJSONObject(i);
                    holder.put("meter", myFinalJsnObj);

                    StringEntity se = new StringEntity(holder.toString());
                    post.setEntity(se);
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-Type", "application/json");
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    response = client.execute(post,responseHandler);
                    json = new JSONObject(response);
                }

                if (json.getBoolean("success")){
                    Toast.makeText(getBaseContext(),"Uploaded your reading to the server",Toast.LENGTH_LONG).show();
                }else{
                    toastError();
                }
                Log.d(TAG, "UPLOADED");
            } catch (HttpResponseException hre) {
                hre.printStackTrace();
                Log.e(TAG, "client protocol " + hre);
                toastError();
            }catch (IOException ioe){
                ioe.printStackTrace();
                Log.e(TAG, "IO " + ioe);
                toastError();
            }
        }catch (JSONException je){
            je.printStackTrace();
            Log.e(TAG, "JSON " + je);
            toastError();
        }
    }
    private void toastError(){
        Toast.makeText(getApplicationContext(),"Failed to Uploaded your readings",Toast.LENGTH_LONG).show();
    }
    private void toastError(CharSequence msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }
  }