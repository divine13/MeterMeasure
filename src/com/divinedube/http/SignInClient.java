package com.divinedube.http;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.divinedube.helpers.JsonUser;
import com.divinedube.helpers.MeterUtils;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Divine Dube on 2014/09/13.
 */
public class SignInClient extends IntentService {
    private static String TAG = "SignInClient";
    SharedPreferences prefs;

    public SignInClient() {
        super("SignInClient");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!(new MeterUtils().isSignedIn(prefs))){
            signIn(prefs);
        }
    }

    private void  signIn(SharedPreferences prefs){

        String email =  new MeterUtils().getEmail(prefs);
        String password = new MeterUtils().getPassword(prefs);
        String resp;

        JsonUser userBean = new JsonUser(email, password);

        userBean.addValues();

        DefaultHttpClient user = new DefaultHttpClient();
        HttpPut put = new HttpPut(SignUpClient.USERS_PATH +"/me/"+ email +"/"+ password+ "/me.json");

        ResponseHandler<String> response = new BasicResponseHandler();

        try{
           resp =  user.execute(put,response);
            Log.d(TAG, " the response from server is " + resp);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
