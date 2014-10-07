package com.divinedube.metermeasure;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.divinedube.helpers.MeterUtils;
import com.divinedube.models.MeterReadingsContract;

/**
 * Created by Divine Dube on 2014/10/07.
 */
public class AverageFragment extends FragmentActivity{
    double readingOne = 0;
    double readingTwo = 0;
    double readingThree = 0;

    String readingDateOne;
    String readingDateTwo;
    String readingDateThree;

    private String[] theReadingColumn = {MeterReadingsContract.Column.READING, MeterReadingsContract.Column.DAY};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_average);

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MeterReadingsContract.CONTENT_URI, theReadingColumn, null, null, MeterReadingsContract.DEFAULT_SORT);
        if (cursor.moveToFirst()) {  //init the class variables
            //complicated by design. i hope that i understand it in the future
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 0:
                        cursor.move(i);   //todo refactor this
                        readingOne = cursor.getDouble(0);
                        readingDateOne = cursor.getString(0);
                        break;
                    case 1: //
                        cursor.move(i); //cursor.move(1)
                        readingTwo = cursor.getDouble(0);
                        break;
                    case 2:
                        cursor.move(i); //cursor.move(2)
                        readingThree = cursor.getDouble(0);
                        break;
                }
            }
        }


        TextView tvReadingOne = (TextView) findViewById(R.id.reading_one);
        TextView tvReadingTwo = (TextView) findViewById(R.id.reading_two);
        TextView tvReadingThree = (TextView) findViewById(R.id.reading_three);

        TextView tvReadingDateOne = (TextView) findViewById(R.id.reading_date_one);
        TextView tvReadingDateTwo = (TextView) findViewById(R.id.reading_date_two);
        TextView tvReadingDateThree = (TextView) findViewById(R.id.reading_date_three);

    }
}