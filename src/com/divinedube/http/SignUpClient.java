package com.divinedube.http;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.divinedube.helpers.JsonUser;
import com.divinedube.helpers.MeterUtils;
import com.google.gson.Gson;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Divine Dube on 2014/08/11.
 */

//pass this class intent of the user details
 public class SignUpClient  extends IntentService{

    protected static final String USERS_PATH = MeterUtils.ROOT_URL +  "/users";
    private String TAG = "SignUpClient";
    String response;
    SharedPreferences prefs;
    Handler mHandler;
   //public boolean success;



    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        //  s = this; //use this next time //todo try this with toasts
    }

    public SignUpClient() {
        super("SignUpClient");
       // Log.d(TAG, "In the Sign up client ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String email = prefs.getString("email", "zero");
        String password = prefs.getString("password", "zero");

        if (!(new MeterUtils().isSignedIn(prefs)) ){
            Log.d(TAG, "Signing Up");
            signUp(email, password);
            toast("You are now Signed up");
        }
    }

    private void signUp(String email, String password){
        Log.d(TAG, "Trying to sign up");
        DefaultHttpClient user = new DefaultHttpClient();
        HttpPost post = new HttpPost(USERS_PATH);


        JsonUser userInJson = new JsonUser(email, password);
        userInJson.addValues();

        Gson json = new Gson();

        String userJson = json .toJson(userInJson);
        Log.d(TAG, "user is like this " + userJson);

        try {
            StringEntity se = new StringEntity(userJson);
            post.setEntity(se);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            post.setHeader("Accept", "application/json");  //todo move this to Meter utils
            post.setHeader("Content-Type", "application/json");

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = user.execute(post, responseHandler);
            Log.d(TAG, "got this from server: " + response);

            SharedPreferences.Editor writer = prefs.edit();
            if (MeterUtils.easyForSuccessCheck(response)){
                String token = getRememberTokenFor(response);

                writer.putString("signedIn", "yes" ); //todo remove this its a duplicate
                writer.putString("rememberToken", token);
                boolean pushed = writer.commit();
                Log.d(TAG, "did i write successfully " +  pushed);
                toast("you are in man");
                Log.d(TAG, "you are in man getting your token and the token is " + token);

            }else{
                writer.putString("SignedIn", "no");
               boolean pushed = writer.commit();

                Log.d(TAG, "did i write successfully the no val " +  pushed);

                toast("You are not in man");
                Log.d(TAG, "You are not in man");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }





    private void toast(final CharSequence msg){ //todo move this to utils
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

    private String getRememberTokenFor(String response){
      String rememberToken = "nothing";
        Log.d(TAG, response);
        try {
           JSONObject jsonObject = new JSONObject(response);

           JSONObject obj =  jsonObject.getJSONObject("data").getJSONObject("user");
            rememberToken = obj.getString("remember_token");

        }catch (JSONException e){
            e.printStackTrace();
        }
        return  rememberToken;
    }

}
