package com.divinedube.metermeasure;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.divinedube.helpers.MeterUtils;
import com.divinedube.http.SignInClient;
import com.divinedube.http.SignUpClient;

import java.util.regex.Pattern;


public class SignUpActivity extends Activity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    SharedPreferences preferences;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up); //todo add bottom action bar for all other actions
    }

    public void signUp(View view){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
         if(new MeterUtils().isSignedIn(preferences)){

            Log.d(TAG,"You already have an account" );
             toast("You already have an account");

            startActivity(new Intent(this, MainActivity.class));
             //todo add auto refresh
        }else if (isValid() && !(new MeterUtils().isSignedIn(preferences))) {
            Log.d(TAG,"we are going register now bra");
            startService(new Intent(this, SignUpClient.class));
            startActivity(new Intent(this, MainActivity.class));
        }else {
            Toast.makeText(this, "please validate your form", Toast.LENGTH_LONG).show();
        }

    }

    private void saveUserInfo(String email, String password){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor writer = prefs.edit();
        writer.putString("email", email);
        writer.putString("password", password);
        writer.apply();
    }

    private boolean isValid(){

        String VALID_EMAIL_REGEX = "/\\A[\\++\\-.]+@[a-z\\d\\-.]+\\.[a-z]+\\z/i";

        EditText EtEmail = (EditText) findViewById(R.id.email);
        EditText EtPassword = (EditText) findViewById(R.id.password);
        EditText EtPassConfirm = (EditText) findViewById(R.id.password_confirm);

        String email = EtEmail.getText().toString().toLowerCase();
        String password = EtPassword.getText().toString();
        String passConfirm = EtPassConfirm.getText().toString();

        Pattern p = Pattern.compile(VALID_EMAIL_REGEX);

        boolean validEmail = p.matcher(email).matches();



        if ( TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passConfirm) || !(stupidValidate(email))  ){
            //just wanted to print more user friendly messages
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passConfirm)) {
                toast("email password and password confirmation can not be empty");
                Log.d(TAG, "email password and password confirmation can not be empty");
            }else if (!stupidValidate(email)){
              toast("your email is not correct");
            }
           return false;
        }else if (password.length() < 4 || passConfirm.length() < 4 ){
           toast("your password must be more than 4 characters ");
            return false;
        }else if (password.equals(passConfirm)){
                Log.d(TAG, "saving the users first");
                saveUserInfo(email, password);
            return true;
        }else {
           Log.d(TAG, "its not valid");
            return false;
        }
    }


    public void toast(CharSequence msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    private boolean stupidValidate(String email){
        return email.contains("@") && email.contains(".");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sign_up:
                startActivity(new Intent(this, SignIn.class));
              return true;
            default:
                return true;
        }
    }
}