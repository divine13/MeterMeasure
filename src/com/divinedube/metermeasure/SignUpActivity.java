package com.divinedube.metermeasure;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.divinedube.helpers.MeterUtils;
import com.divinedube.http.SignUpClient;


public class SignUpActivity extends Activity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up); //todo add bottom action bar for all other actions

       ActionBar bar = getActionBar();

        assert bar != null;
        bar.setDisplayHomeAsUpEnabled(true);


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

    private void saveUserInfo(String email, String password, String meterNumber, int familyPeopleNumber){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor writer = prefs.edit();
        writer.putString("email", email);
        writer.putString("password", password);
        writer.putString("meterNumber", meterNumber);
        writer.putInt("familyNumber", familyPeopleNumber);
        writer.apply();
    }

    private boolean isValid(){


        EditText etEmail = (EditText) findViewById(R.id.email);  //todo refactor this used here a The other Sign in class
        EditText etPassword = (EditText) findViewById(R.id.password);
        EditText etPassConfirm = (EditText) findViewById(R.id.password_confirm);
        EditText etMeterNumber = (EditText) findViewById(R.id.meter_number);
        EditText etFamilyPeopleNumber = (EditText) findViewById(R.id.family_number);

        String email = etEmail.getText().toString().toLowerCase();
        String password = etPassword.getText().toString();
        String passConfirm = etPassConfirm.getText().toString();
        String meterNumber = etMeterNumber.getText().toString().trim();
        String stringFamilyPeopleNumber = etFamilyPeopleNumber.getText().toString().trim();
        int familyPeopleNumber; // if this is zero then it will fail

       if (stringFamilyPeopleNumber.length() < 1){
           stringFamilyPeopleNumber = "0";
       }
       familyPeopleNumber  = Integer.valueOf(stringFamilyPeopleNumber);


        if ( TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passConfirm)
                || !(MeterUtils.emailValidate(email))  ){
            //just wanted to print more user friendly messages
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passConfirm)) {
                toast("email password and password confirmation can not be empty");
                Log.d(TAG, "email password and password confirmation can not be empty");
            }else if (!MeterUtils.emailValidate(email)){
              toast("your email is not correct");
            }
           return false;
        }else if (password.length() < 4 || passConfirm.length() < 4 || familyPeopleNumber < 1
                || !(isMeterNumberAcceptable(meterNumber)) ){ //the fail will be here
            if (familyPeopleNumber < 1){
                toast("number of people in family must be more than 1 ");
            }else if(!(isMeterNumberAcceptable(meterNumber))){
                toast("Meter number must be equal to 11 digits or just leave it blank.");
            }else {
                toast("your password must be more than 4 characters  ");
            }
            return false;
        }else if (password.equals(passConfirm)){

                Log.d(TAG, "saving the users first");
                saveUserInfo(email, password, meterNumber(meterNumber), familyPeopleNumber );
            return true;
        }else {
           Log.d(TAG, "its not valid");
            return false;
        }

    }

    private boolean isStringNumberAndLessThan(String number, int compareInt){ //no need for this but....
        if (MeterUtils.isStringNumber(number)){
            int numberAsInt = Integer.valueOf(number);
            return numberAsInt < compareInt;
        }else
            return false;
    }


    public void toast(CharSequence msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        return true;
    }

    private String meterNumber(String meterNumber){
        String number;
        if (meterNumber.length() != 11 || meterNumber.equals("") || !(MeterUtils.isStringNumber(meterNumber))){
           if (meterNumber.equals("")){
               toast("You can always add you meter number in settings");
           }else if(meterNumber.length() != 11){
               toast("Meter number must be equal to 11 numbers");
           }else {
               toast("Your meter number must be numbers only");
           }
            number = "0";
        }else {
            number = meterNumber;
        }

        return number;
    }

    private boolean isMeterNumberAcceptable(String number){

        return number.length() == 0 || number.length() == 11;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sign_in:
                startActivity(new Intent(this, SignIn.class));
              return true;
            default:
                return true;
        }
    }
}