package com.divinedube.metermeasure;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;


/**
 * Created by Divine Dube on 2014/06/28.
 */
   //TODO this class is getting too big segment it a bit yeah mate ?
   //TODO might have to spawn some threads for some methods here
public class NewMeterReadingsActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener { //TODO create another class for this listener

    private final static String TAG = NewMeterReadingsActivity.class.getSimpleName();

    EditText mEditTextDay;
    EditText mEditTextTime;
    EditText mEditTextReading;
    EditText mEditTextNote;

    String day; //i will make this a spinner of all the days and add date
    String time; //i will make time a string because i don`t have to manipulate it
    double reading;
    String note;
    String selectedItem;

    long newRowId;
    ContentValues values;
    SQLiteDatabase db;
    Spinner spinner;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_new);
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);              //might have to make these global
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); // 06
        int month = cal.get(Calendar.MONTH);

        mEditTextDay = (EditText) findViewById(R.id.editTextDay);
        mEditTextTime = (EditText) findViewById(R.id.editTextTime);
        mEditTextReading = (EditText) findViewById(R.id.editTextReading);
        mEditTextNote = (EditText) findViewById(R.id.editTextNote);
        spinner = (Spinner) findViewById(R.id.spinner); //might just take out all of it just use cal date utils

        mEditTextDay.setText(dayOfMonth + " " + DateUtils.getMonthString(month, DateUtils.LENGTH_SHORT ));  // 26/06
        mEditTextTime.setText(hour + ":" + minute);//todo change this to shared prefs preferences time set by user
        //spinner.getItemAtPosition()
        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(this,R.array.week_days_array,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter); //i would like this to display the default day which is today
        spinner.setOnItemSelectedListener(this);
    }
    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //get the item
        //make sure its a string
        //add it to day
        Log.d(TAG, "int the method of the onItemSelected ");
         selectedItem =  parent.getItemAtPosition(position).toString();
        Toast.makeText(this,"You Selected " + selectedItem , Toast.LENGTH_LONG).show();
    }

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
            mEditTextTime.setText(hourOfDay + ":" +  minute);
        }

    }
    public void showTimePickerDialog(View view){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"time picker");
    }

    public void saveData(View view){ //I will run this on its thread some time later for now lets get the basics down
        try {
            day =  mEditTextDay.getText().toString() + " " + selectedItem;
            time = mEditTextTime.getText().toString();
            reading = Double.valueOf(mEditTextReading.getText().toString());
            note = mEditTextNote.getText().toString();

            DbHelper dbHelper = new DbHelper(this);
            db = dbHelper.getWritableDatabase();

             values = new ContentValues();
           // values.put(MeterReadingsContract.Column.ID, newRowId);
            values.put(MeterReadingsContract.Column.DAY, day);
            values.put(MeterReadingsContract.Column.TIME, time);
            values.put(MeterReadingsContract.Column.READING, reading);
            values.put(MeterReadingsContract.Column.NOTE, note);
            values.put(MeterReadingsContract.Column.CREATED_AT, System.currentTimeMillis());
            //todo there must be a column for differences in readings
            //must have settings activity where i can get the time

             getContentResolver().insert(MeterReadingsContract.CONTENT_URI, values);

                Log.d(TAG, "inserting "  + time + reading + note + " into db " +
                        MeterReadingsContract.TABLE + " at "+System.currentTimeMillis());
            //newRowId = db.insert(MeterReadingsContract.TABLE, null, values);
            Toast.makeText(this, "Your Meter Readings have been saved", Toast.LENGTH_LONG).show();
            finish();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Eish!.Could not save Meter Readings", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            Log.d(TAG, "onNothingSelected was called");
    }


}
