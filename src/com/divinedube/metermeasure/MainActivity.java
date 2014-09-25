package com.divinedube.metermeasure;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.divinedube.helpers.MeterUtils;
import com.divinedube.http.MeterMeasureClient;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //todo add bottom action bar for all other actions
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

      //  ActionBar

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item =  menu.findItem(R.id.main_action_sign_in);
        if (new MeterUtils().isSignedIn(prefs)){
            item.setTitle("Logout");
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_new:
                startActivity(new Intent(this, NewMeterReadingsActivity.class));
                return true;
            case R.id.action_about:
                showAboutDialog();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_stats:
                startActivity(new Intent(this, MeterStatics.class));
                return true;
            case R.id.main_action_sign_in:
                if (new MeterUtils().isSignedIn(prefs)) {
//                    Toast.makeText(this, "You are already signed so just refresh your readings", Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "you already in");
//                    return false;
//                    Menu menu = (Menu) findViewById(R.id.main_action_sign_in);
//                    menu. wrong man

                    logoutConfirmDialog();
                    return true;
                }else{
                    startActivity(new Intent(this, SignIn.class));
                    Log.d(TAG, "you signing so we going to take you there");
                    return true;
                }
            case R.id.action_sync_data:
                if(isConnected()) {
                    startService(new Intent(this, MeterMeasureClient.class));  //probably bad style and code too
                }else {
                    Toast.makeText(this, "please connect to the Internet to updating readings", Toast.LENGTH_LONG).show();
                }
            default:
                return false;
        }
    }

    public void showAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.about).setTitle(R.string.motivation)
                .setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

       private  boolean yes;
    public boolean logoutConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Logout").setTitle("Logout?");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                yes = logout();
                dialogInterface.dismiss();
                Toast.makeText(getApplicationContext(), "You are now logged out", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               yes = false; //todo is this needed  to be false
                dialogInterface.cancel();
                Toast.makeText(getApplicationContext(), "Stopped logout process.Whee.. that was close", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return yes;
    }

    public boolean isConnected(){
        //lol conman
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean logout(){ //go and leave no trace
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("rememberToken");
        editor.putString("signedIn", "no");
        editor.remove("familyNumber");
        return editor.commit();
    }
}