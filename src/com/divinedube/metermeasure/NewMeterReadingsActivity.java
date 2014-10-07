package com.divinedube.metermeasure;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.divinedube.helpers.MeterUtils;
import com.divinedube.threads.UpdateRecharge;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Divine Dube on 2014/06/28.
 */
   //TODO this class is getting too big segment it a bit yeah mate ?
   //TODO might have to spawn some threads for some methods here
public class NewMeterReadingsActivity extends FragmentActivity { //TODO create another class for this listener

    private final static String TAG = NewMeterReadingsActivity.class.getSimpleName();

   Button mButtonTextDay;
    Button mButtonTextTime;
    EditText mEditTextReading;
    EditText mEditTextNote;

    String day; //i will make this a spinner of all the days and add date
    String time; //i will make time a string because i don`t have to manipulate it
    double reading;
    String note;
    String selectedItem;

    long newRowId;
    ContentValues values;
    Spinner spinner;
    MeterUtils util = new MeterUtils();

    //todo refactor this deeply specially with the class calender
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_new);
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR_OF_DAY);           //TODO refactor to use MeterUtils
        int minute = cal.get(Calendar.MINUTE);              //might have to make these global
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);    // 06
        int month = cal.get(Calendar.MONTH);

        mButtonTextDay = (Button) findViewById(R.id.buttonDay);
        mButtonTextTime = (Button) findViewById(R.id.buttonTime);
        mEditTextReading = (EditText) findViewById(R.id.editTextReading);
        mEditTextReading.requestFocus();
        mEditTextNote = (EditText) findViewById(R.id.editTextNote);
//       spinner = (Spinner) findViewById(R.id.spinner); //todo might just take out all of it just use cal date utils
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String CheckTimeOrCurrent = prefs.getString("time", hour + ":" + minute);  //TODO refactor to use MeterUtils

        mButtonTextDay.setText(formattedDay(dayOfMonth) + " " +
                cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));  // 26/06

        mButtonTextTime.setText(CheckTimeOrCurrent);
        //spinner.getItemAtPosition()
//        ArrayAdapter<CharSequence> spinnerAdapter =
//                ArrayAdapter.createFromResource(this,R.array.week_days_array,android.R.layout.simple_spinner_item);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        ArrayAdapter<CharSequence> spinnerAdapter =
//                ArrayAdapter.createFromResource(this,R.array.week_days_array,android.R.layout.simple_spinner_item);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner.setAdapter(spinnerAdapter);
//        spinner.setOnItemSelectedListener(this);
//        AdapterView<?> parent = spinner;

//        parent.setSelection(util.getDayOfWeek() -1);
    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//        //get the item
//        //make sure its a string
//        //add it to day
//
//        Log.d(TAG, "int the method of the onItemSelected ");
//         selectedItem =  parent.getItemAtPosition(position).toString();
//        Toast.makeText(this,"You Selected " + selectedItem , Toast.LENGTH_LONG).show();
//    }

    public void saveDatas(MenuItem item) {
        save();
    }

    //todo fix this make this static
    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        public Dialog onCreateDialog(Bundle savedInstance){
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),this,hour,minute, DateFormat.is24HourFormat(getActivity()));
        }

        /**
         * @param view      The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute    The minute that was set.
         */
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            String twoDecMin = "";
            Integer min = (Integer) minute;
            String minAsS = min.toString();

            Log.d(TAG, "string val for min is " + minAsS);
            if (min == 0){
                twoDecMin = minAsS + "0";
            }
            else if ((minAsS.length()) == 1){
                twoDecMin =  "0" + min;
            }else {
                twoDecMin = minAsS;
            }

            mButtonTextTime.setText(hourOfDay + ":" + twoDecMin);
        }

    }


    public void showTimePickerDialog(View view){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"timePicker");
    }

    private class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        public Dialog onCreateDialog(Bundle savedInstance){
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int monthOfYear = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),this, year, monthOfYear,day );
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

            String sMonthOfYear = MeterUtils.intMonthToString(monthOfYear);  //todo refactor this and remove the deprecated above

            String sDayOfMonth = formattedDay(dayOfMonth);
            Log.d(TAG, "the month is " + sMonthOfYear);

            mButtonTextDay.setText(sDayOfMonth + " " + sMonthOfYear);
        }
    }

    public void showDatePickerDialog(View view){
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        datePickerDialogFragment.show(getFragmentManager(), "datePicker");
    }

    private String formattedDay(int dayOfMonth){
        if (dayOfMonth < 10){
            return  "0" + dayOfMonth;
        }else {
           return  "" + dayOfMonth;
        }
    }

    //
    public void saveData(View view){
       save();
    }

    private void save(){
        try {
            day =  mButtonTextDay.getText().toString();
            time = mButtonTextTime.getText().toString();
            reading = Double.valueOf(mEditTextReading.getText().toString());
            note = mEditTextNote.getText().toString();
            UpdateRecharge updateRecharge = new UpdateRecharge(this, reading, day,time, note);
            updateRecharge.start(); //stating the thread. could have implemented this better  but just want to play with threads
            Toast.makeText(this, "Your Meter Readings have been saved", Toast.LENGTH_LONG).show();
            finish();
        }catch (NumberFormatException e){
            e.printStackTrace();
            Toast.makeText(this, "Eish! Please add the reading before saving.", Toast.LENGTH_LONG).show();
        }
    }
   // @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//            Log.d(TAG, "onNothingSelected was called");
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                save();
            default:
                return false;
        }
    }
}
