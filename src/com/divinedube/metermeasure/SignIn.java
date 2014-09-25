package com.divinedube.metermeasure;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.divinedube.helpers.MeterUtils;
import com.divinedube.http.SignInClient;


/**
 * Created by Divine Dube on 2014/08/11.
 */
public class SignIn extends Activity{

    private static final String TAG = SignIn.class.getSimpleName();
    private SharedPreferences prefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    public void signIn(View view){


        EditText EtEmail = (EditText) findViewById(R.id.sign_in_email);  //todo refactor this used here a The other Sign up class
        EditText EtPassword = (EditText) findViewById(R.id.sign_in_password);

        String email = EtEmail.getText().toString().toLowerCase();
        String password = EtPassword.getText().toString();

        SharedPreferences.Editor writer = prefs.edit();

        Log.d(TAG, "the email is " + email + "and the password is " + password);
        writer.putString("email", email);
        writer.putString("password", password);

        //do not have to validate if it has content because if it passes this then it is  not empty todo refactor the sign up class
        if (MeterUtils.emailValidate(email) && password.length() > 3){
            writer.apply();
            Log.d(TAG, "signing you in");
            Toast.makeText(this, "Signing in", Toast.LENGTH_LONG).show();
            startService(new Intent(this, SignInClient.class));
            startActivity(new Intent(this, MainActivity.class));
        }else {
            Toast.makeText(this, "Please check your email and your password", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sign_up:
                startActivity(new Intent(this, SignUpActivity.class));
                return true;
            default:
                return false;
        }
    }
}