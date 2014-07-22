package com.divinedube.metermeasure;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.divinedube.http.MeterMeasureClient;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //todo add bottom action bar for all other actions
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
                startActivity(new Intent(this, MeterReadingsDiff.class));
                return true;
            case R.id.action_sync_data:
                if(isConnected()) {
                    startService(new Intent(this, MeterMeasureClient.class));  //probably bad style and code too
                }else {
                    Toast.makeText(this, "please connect to the Internet to update readings", Toast.LENGTH_LONG).show();
                }
            default:
                return false;
        }
    }

    public void showAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.about).setTitle(R.string.motivation)
                .setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isConnected(){
        //lol conman
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }
    }
}