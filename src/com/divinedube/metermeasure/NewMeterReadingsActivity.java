package com.divinedube.metermeasure;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable; //might need this
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Divine Dube on 2014/06/28.
 */
public class NewMeterReadingsActivity extends Activity{

    private final static String TAG = NewMeterReadingsActivity.class.getSimpleName();

    EditText mEditTextDay;
    EditText mEditTextTime;
    EditText mEditTextReading;
    EditText mEditTextNote;

    String day; //i will make this a spinner of all the days and add date
    String time; //i will make time a string because i don`t have to manipulate it
    double reading;
    String note;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_new);

        mEditTextDay = (EditText) findViewById(R.id.editTextDay);
        mEditTextTime = (EditText) findViewById(R.id.editTextTime);
        mEditTextReading = (EditText) findViewById(R.id.editTextReading);
        mEditTextNote = (EditText) findViewById(R.id.editTextNote);

    }

    public void saveData(View view){ //I will run this on its thread some time later for now lets get the basics down
        try {
            day = mEditTextDay.getText().toString();
            time = mEditTextTime.getText().toString();
            reading = Double.valueOf(mEditTextReading.getText().toString());
            note = mEditTextNote.getText().toString();

            DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(MeterReadingsContract.Column.ID, BaseColumns._ID);
            values.put(MeterReadingsContract.Column.TIME, time);
            values.put(MeterReadingsContract.Column.READING, reading);
            values.put(MeterReadingsContract.Column.NOTE, note);
            values.put(MeterReadingsContract.Column.CREATED_AT, Build.TIME);

            long newRowId;

                Log.d(TAG, "inserting " + BaseColumns._ID + time + reading + note + " into db " +
                        MeterReadingsContract.TABLE);

                newRowId = db.insert(MeterReadingsContract.TABLE,MeterReadingsContract.Column.NULL_COLUMN, values);

            Toast.makeText(this, "Your Meter Readings have been saved", Toast.LENGTH_LONG).show();

        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            Toast.makeText(this, "Eish!.Could not save Meter Readings", Toast.LENGTH_LONG).show();
        }
    }
}
