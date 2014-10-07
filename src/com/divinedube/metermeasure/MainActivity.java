package com.divinedube.metermeasure;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.divinedube.helpers.MeterUtils;
import com.divinedube.helpers.MyPagerAdapter;
import com.divinedube.http.MeterMeasureClient;

import java.util.List;
import java.util.Vector;

public class MainActivity extends FragmentActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences prefs;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //todo add bottom action bar for all other actions
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        this.initialisePaging();  //**init**

        ActionBar actionBar = getActionBar(); //todo put it in method that gives back actionbar
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        if (actionBar != null) {
            actionBar.addTab(actionBar.newTab().setText("Readings").setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText("Stats").setTabListener(tabListener));
        }

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if (getActionBar() != null) {
                    getActionBar().setSelectedNavigationItem(position);
                }
            }
        });
    }

    private void initialisePaging() {
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, FragmentList.class.getName()));
        fragments.add(Fragment.instantiate(this, MeterReadingsDiffFragment.class.getName()));

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this.getSupportFragmentManager(), fragments);
        mPager = (ViewPager) findViewById(R.id.main_pager);

        mPager.setAdapter(myPagerAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        String meterNumber =  prefs.getString("meterNumber", "no meter number set").trim();
        String family = prefs.getString("familyNumber", "0").trim();

        ActionBar actionBar = getActionBar();
        if (actionBar != null){ //support for older devices
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setSubtitle(meterNumber);
        }

        if (!(MeterUtils.isMeterNumberAcceptable(meterNumber)) ){
            MeterUtils.toast(this, "It seems like the meter number that you have set is not correct.Please check");
        }

        if (MeterUtils.isStringNumberLessThan(family, 1)){
            MeterUtils.toast(this, "People in family must be more than 0");
        }
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
//            case R.id.action_stats:
//                startActivity(new Intent(this, MeterStatics.class));
//                return true;
            case R.id.main_action_sign_in:
                if (new MeterUtils().isSignedIn(prefs)) {
                    logoutConfirmDialog();
                    return true;
                }else{
                    startActivity(new Intent(this, SignIn.class));
                    Log.d(TAG, "you signing so we going to take you there");
                    return true;
                }
            case R.id.action_sync_data: //todo must start this automatically every 24 hours
                if(isConnected() && new MeterUtils().isSignedIn(prefs)) {
                    startService(new Intent(this, MeterMeasureClient.class));  //probably bad style and code too
                }else if (!(isConnected())){
                    Toast.makeText(this, "please connect to the Internet to updating readings", Toast.LENGTH_LONG).show();
                }else {
                    MeterUtils.toast(this, "Please Sign in to update readings");
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