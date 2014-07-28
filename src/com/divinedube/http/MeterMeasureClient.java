package com.divinedube.http;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.divinedube.metermeasure.BagOfValuesArray;

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
import android.os.Handler;

/**
 * Created by Divine Dube on 2014/07/15.
 */

  public class MeterMeasureClient extends IntentService {

    private static final String TAG = "MeterMeasureClient";
    public static final String ROOT_URL = "http://10.0.2.2:3000";
    public static final String CREATE_METER_END_POINT_URL = ROOT_URL + "/meters.json";
//    public static final String GET_LAST_METER_URL = ROOT_URL + "/meters/last.json";
    private  String PHONE_ID = generateId();
    Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
    }

    //todo these dont need to be Global vars
    BagOfValuesArray bva = new BagOfValuesArray();
    Gson gson = new GsonBuilder().serializeNulls().create();

    JSONObject holder = new JSONObject();

    public MeterMeasureClient() {
        super(TAG);
    }
    private String generateId(){

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if(tel.getDeviceId() != null) {
            Integer integer = Integer.valueOf(tel.getDeviceId());
            double val = integer / 5379.973;
            return "devI" + val + "D";
        }else{
            String id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            return id;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //if check is != 0 download
        //else upload those that have not been uploaded\
        toast("phone id " + PHONE_ID); // must actually use the meter id customer ID on vip card
        int checkNum = check();
        if (isConnectedToServer(TAG, checkNum)) {
            Log.d(TAG, "the check num is " + checkNum);
            if (checkNum > 0) {
                 Log.d(TAG, checkNum +" new ones are available");
                download(PHONE_ID);
                //todo might have to upload here it wont hurt i promise
                 toast("added " + checkNum + "meters readings");
            } else {
                String[] notUploaded = {"false"};
                upload(notUploaded);
                toast("You are up to date.Syncing with other devices");
            }
        }
        //todo refactor this to use the boolean field
        //i must parse the the phone id check if the phone id on the
        //i want to upload when the phone db has some data that is not uploaded
//        JSONObject jsObj =  getLastReading(TAG, GET_LAST_METER_URL);
//        String[] projection = {MeterReadingsContract.Column.CREATED_AT};
//        Cursor cursor = getContentResolver().query(MeterReadingsContract.CONTENT_URI,projection,null,null,MeterReadingsContract.NORMAL_SORT_ORDER);
//        int count = cursor.getCount();
//        try {
//            if ((cursor.moveToLast()) && (isConnectedToServer(TAG))){
//
//            String serverLastRecord = jsObj.getJSONObject("data").getJSONObject("meter").getString("made_at");
//            Long serverLastRecordAsLong = new Long(serverLastRecord);
//
//
//
//           Log.d(TAG, "the server last record is " + serverLastRecord);
//            Log.d(TAG, "the date of the last record is " + serverLastRecordAsLong);
//
//            long phoneLastRecordAsLong = cursor.getLong(0);                                             //*#** Didi motivated worX **#*//
//            Log.d(TAG, "we are currently selecting by " + phoneLastRecordAsLong + "count is " + count);
//            if (serverLastRecordAsLong > phoneLastRecordAsLong){
//                //todo download make method in server to give the record that are currently newer than the phone`s current
//                //todo and actually insert them
//                Log.d(TAG,"downloading all the newest readings from the server and inserting them in the phone db to keep up with server");
//                download(phoneLastRecordAsLong);
//            }else if (serverLastRecordAsLong < phoneLastRecordAsLong){
//                Log.d(TAG, "true the server data needs uploading and will do just that right now uploading...");
//                String[] serverLastRecordAsArr = {"false"};
//                upload(serverLastRecordAsArr);
//            }
//        }else if (count <= 0){
//                Log.d(TAG, "just downloading");
//             download(0L);
//          }
//            else {
//                toast("Nothing was updated");
//                Log.d(TAG, "Nothing was updated ");
//            }
//        }catch (JSONException jse){
//            jse.printStackTrace();
//        }
    }

    public JSONObject getLastReading(String classTag, String  getLastMeterUrl){
        String  resp;
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

    private int check(){
        String resp;
        String url = ROOT_URL + "/meters/" + PHONE_ID + "/check_for_me.json";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(url);
        ResponseHandler<String> getResponseHandler = new BasicResponseHandler();
        try {
           resp = httpClient.execute(httpPut,getResponseHandler);
            Log.d(TAG, "the check response is " + resp);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return Integer.valueOf(resp);
    }

    public  boolean isConnectedToServer(String tag, int check){
        boolean yes;
//        boolean yes;
//        try {
//            JSONObject jsObj = new MeterMeasureClient().getLastReading(tag, GET_LAST_METER_URL);
//            jsObj.getJSONObject("data").getJSONObject("meter").getString("made_at");
//            yes = true;
//        }catch (NullPointerException npe){
//            npe.printStackTrace();
//            toast("please make sure that you are truly connected to the internet");
//            yes = false;
//        } catch (JSONException jsne) {
//            jsne.printStackTrace();
//            toast("please make sure that you are truly connected to the internet");
//            yes = false;
//        }
//        return yes;
        if (check >= 0){
            Log.d(tag, "Man you are connected to the server ");
            yes = true;
        }else {
            Log.d(tag, "could not connect to the server");
            toast("Please make sure you are connected to the internet.Try going on Google with your browser");
            yes = false;
        }
         return yes;
    }

    public void download(String phoneID){

        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = ROOT_URL + "/meters/"+ phoneID + "/newer.json";
        String response;
        HttpPut httpPut = new HttpPut(url);
        ResponseHandler<String> stringResponseHandler = new BasicResponseHandler();
        try {
            response = httpClient.execute(httpPut,stringResponseHandler);
            JSONObject jsonObject = new JSONObject(response);
            Log.d(TAG, "Got this from server " + jsonObject.toString());
           JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("meters");  //digging for the wanted values
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
                values.put(MeterReadingsContract.Column.UPLOADED, 1);
                Uri uri = getContentResolver().insert(MeterReadingsContract.CONTENT_URI, values);
                Log.d(TAG, "inserted " + uri);
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    public void upload(String[] selectionArgs){
        //todo select only those field which have not been pushed to the server so have to have a boolean field in meter.
        Cursor c = getContentResolver().query(MeterReadingsContract.CONTENT_URI, null,
                MeterReadingsContract.THE_FRESHEST_SELECTION_STATEMENT, selectionArgs, MeterReadingsContract.DEFAULT_SORT);

        int numRows = c.getCount();
        Log.d(TAG, "there are number rows in meter currently is: " + numRows);

        for (int i = 0; i < numRows; i++) {
            c.moveToNext();
            int _id = c.getInt(0); //todo remove this one
            String day = c.getString(1);
            String time = c.getString(2);
            double reading = c.getDouble(3);
            String note = c.getString(4);
            String madeAt = c.getString(5);

            bva.setMap(_id, day, time, reading, note, madeAt, PHONE_ID);
            bva.putArr();

        } //get the gson representative of the meter db
        String jsonOfDb  = gson.toJson(bva);
        Log.d(TAG, jsonOfDb);
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(CREATE_METER_END_POINT_URL);
        String response;
        JSONObject json = new JSONObject();
        try {
            try {
                JSONObject jsObj = new JSONObject(jsonOfDb); //efficient because only this one will be created
                JSONArray js = jsObj.getJSONArray("meter");
                JSONObject myFinalJsnObj;


                int numObjectsInArray = js.length();
                for (int i = 0; i < numObjectsInArray; i++) {       //*#** Didi motivated worX **#*//
                    json.put("success", false);
                    json.put("info", "big failure");

                    Log.d(TAG, "the _id of the object at index i is " + js.getJSONObject(i).getString("_id")
                            + "the length of the array is " + numObjectsInArray);
                    myFinalJsnObj = js.getJSONObject(i);
                    holder.put("meter", myFinalJsnObj);
                    setUploaded(holder);
                    StringEntity se = new StringEntity(holder.toString());
                    post.setEntity(se);
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-Type", "application/json");
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    response = client.execute(post,responseHandler);
                    json = new JSONObject(response);
                    Log.d(TAG, "the returned json object form server is " + json.toString());
                }

                if (json.getBoolean("success")){
                    toast("Uploaded your reading to the server");
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
            if (json.toString().equals("{\"meter\":[]}")){
                toast("your readings are up to date");
                Log.d(TAG, "your readings are up to date" );
            }
        }
    }

    private void setUploaded(JSONObject jsonObject){
        try {
            int _id = jsonObject.getJSONObject("meter").getInt("_id");
            Log.d(TAG, "the id of the uploaded val is " + _id);
           ContentValues values = new ContentValues();
            boolean uploaded = jsonObject.getJSONObject("meter").getBoolean("uploaded");
            Log.d(TAG, "is it true? " + uploaded);
            values.put(MeterReadingsContract.Column.UPLOADED, uploaded);

           Integer integer = new Integer(_id);
           String[] selection = {integer.toString()};
            getContentResolver().update(MeterReadingsContract.CONTENT_URI,values,"_id = ?",selection);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    private void toastError(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(myContext(), "Failed to Uploaded your readings", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void toast(final CharSequence msg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(myContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
    private Context myContext(){

        return this;
    }
  }


