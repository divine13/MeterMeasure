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

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

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


    public void onCreate() {
        super.onCreate();
       Handler mHandler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "service started SignIn ");
        //  s = this; //use this next time //todo try this with toasts
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!(new MeterUtils().isSignedIn(prefs))){
            signIn(prefs);
        }
    }

    private void  signIn(SharedPreferences prefs){

        String password = new MeterUtils().getPassword(prefs);
        String email =  new MeterUtils().getEmail(prefs);
        String meterNumber = "0"; //no need to work hard with prefs  found a better solution check bottom of this mtd
        String peopleInHouse = "0";

        JsonUser userBean = new JsonUser(email, password, meterNumber, peopleInHouse);

        userBean.addValues();

        String jsonUser = MeterUtils.changeT0Json(userBean);

        Log.d(TAG, "the json user looks like this" + jsonUser);

        DefaultHttpClient user = new DefaultHttpClient();
        HttpPost post = new HttpPost(SignUpClient.USERS_PATH+ "/me.json");

        try{
            StringEntity entity = new StringEntity(jsonUser);
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");  //todo move this to Meter utils repeat
            post.setHeader("Content-Type", "application/json");
            ResponseHandler<String> response = new BasicResponseHandler();
           String resp =  user.execute(post,response);
            Log.d(TAG, " the response from server is " + resp);

            if (MeterUtils.easyForSuccessCheck(resp)){
                JSONObject json = new JSONObject(resp).getJSONObject("data").getJSONObject("user");
                String token = json.getString("remember_token");
                meterNumber = json.getString("meter_number");
                peopleInHouse = json.getString("people_in_house");



                Log.d(TAG, "the token is " + token + " the meter number is " + meterNumber +
                        " there are " + peopleInHouse + "living with this person");

                SharedPreferences.Editor writer = prefs.edit();

                writer.putString("rememberToken", token);
                writer.putString("signedIn", "yes" );
                writer.putString("meterNumber", meterNumber); //the number will always either be 0 or the actual number
                writer.putString("familyNumber", peopleInHouse);

                if (writer.commit())
                    toast("You have signed in now you can refresh");
            }else{
                toast("you are not signed in please try again"); //lazy
            }
        }catch (Exception e){ //Bad!
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

    private Context myContext() {
        return this;
    }


}
