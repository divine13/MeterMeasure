package com.divinedube.metermeasure;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.divinedube.helpers.MeterUtils;

import java.util.Calendar;

/**
 * Created by Divine Dube on 2014/07/03.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG =  SettingsFragment.class.getSimpleName() ;
    private SharedPreferences prefs;
    private SharedPreferences preferences;
    private String s;

    @Override
   public void onCreate(Bundle savedInstance){
       super.onCreate(savedInstance);
       addPreferencesFromResource(R.xml.settings);

        Log.d(TAG, "the prefs have changed ");
        EditTextPreference meterNumber = (EditTextPreference) findPreference("meterNumber");
        EditTextPreference familyNumber = (EditTextPreference) findPreference("familyNumber");
        String number = meterNumber.getText();
        String family = familyNumber.getText();
        if (MeterUtils.isMeterNumberAcceptable(number)){
            Log.d(TAG, "value is okay ");
            MeterUtils.toast(getActivity(), "It seems like you entered the right meter number");
        }else{
            Log.d(TAG, "the is bad ");
            MeterUtils.toast(getActivity(), "Your meter number seems to be wrong just double check please");
        }
        if (family == null || MeterUtils.isStringNumberLessThan(family, 1) ){
            MeterUtils.toast(getActivity(), "People in family must be more than 0");
        }
   }

    @Override
    public void onStop(){
        super.onStop();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {  //its not working
//        Log.d(TAG, "the prefs have changed ");
//        EditTextPreference meterNumber = (EditTextPreference) findPreference("meterNumber");
//        String number = meterNumber.getText();
//        if (MeterUtils.isMeterNumberAcceptable(number)){
//            Log.d(TAG, "value is okay ");
//            Toast.makeText(getActivity(), "Okay", Toast.LENGTH_LONG).show();
//        } else{
//            Log.d(TAG, "the is bad ");
//            Toast.makeText(getActivity(), "not Okay", Toast.LENGTH_LONG).show();
//
//        }

    }
//    public class ShowTimePickerDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
//        //might jus extend newMeteReadingsActivity.showTimePickerActivity
//        public Dialog onCreateDialog(Bundle savedInstance){
//            Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
//
//            return new TimePickerDialog(getActivity(),this,hour,minute, DateFormat.is24HourFormat(getActivity()));
//        }
//
//        /**
//         * @param view      The view associated with this listener.
//         * @param hourOfDay The hour that was set.
//         * @param minute    The minute that was set.
//         */
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//        }
//    }
}