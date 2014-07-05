package com.divinedube.metermeasure;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Divine Dube on 2014/07/03.
 */
public class SettingsActivity  extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstance ){
        super.onCreate(savedInstance);
       SettingsFragment settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction().add(android.R.id.content,settingsFragment).commit();
    }
}
