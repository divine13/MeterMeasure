package com.divinedube.metermeasure;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Divine Dube on 2014/07/03.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;

   @Override
   public void onCreate(Bundle savedInstance){
       super.onCreate(savedInstance);
       addPreferencesFromResource(R.xml.settings);

   }

    @Override
    public void onStop(){
        super.onStop();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link android.content.SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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