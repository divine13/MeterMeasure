package com.divinedube.metermeasure;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divinedube.helpers.MeterUtils;
import com.divinedube.models.MeterReadingsContract;
import com.divinedube.models.ProcessedDataContract;

/**
 * Created by Divine Dube on 2014/10/07.
 */
public class FragmentAverage extends Fragment {
    private static final String TAG = FragmentAverage.class.getName();
    double readingOne;
    double readingTwo;
    double readingThree;

    String readingDateOne;
    String readingDateTwo;
    String readingDateThree;
    String[] selectionArgs = {"0"};
    String nothingToShow = "not enough data to do calculations on yet";
    double daysLeftToFinish = 0;



    private String[] theReadingColumn = {
            ProcessedDataContract.Column.DIFFERENCE,
            ProcessedDataContract.Column.DAY_1,
            ProcessedDataContract.Column.DAY_2
    };

    private String[] theRechargeColumn = {
            MeterReadingsContract.Column.READING,
            MeterReadingsContract.Column.DAY,
            MeterReadingsContract.Column.RECHARGED,
            MeterReadingsContract.Column.CREATED_AT
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_average, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ContentResolver contentResolver = getActivity().getContentResolver();


        Cursor cursor = contentResolver.query(ProcessedDataContract.P_DATA_CONTENT_URI,
                theReadingColumn, ProcessedDataContract.NULL_SELECTION_STATEMENT, null, null);

        populateMeterStatisticsCardView(cursor);
        populateMeterUsagesCardView(cursor);
        populateMeterRechargeCardView(contentResolver); //not good because they use separate cursor to same data TODo refactor
    }

    private void populateMeterStatisticsCardView(Cursor cursor) {
        if (cursor.moveToFirst()) {  //init the class variables
            //complicated by design. i hope that i understand it in the future
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 2:
                        if (cursor.move(i)) {   //todo refactor this
                            readingOne = cursor.getDouble(0);
                            String readingDateOneA = cursor.getString(1); //cool! but childish so what! A-B-C-vars
                            String readingDateOneB = cursor.getString(2);
                            readingDateOne = readingDateOneA + " and " + readingDateOneB;
                        }
                        break;
                    case 1:
                        if (cursor.move(i)) { //cursor.move(1)
                            readingTwo = cursor.getDouble(0);
                            String readingDateOneAb = cursor.getString(1);
                            String readingDateOneBb = cursor.getString(2);
                            readingDateTwo = readingDateOneAb + " and " + readingDateOneBb;
                        }
                        break;
                    case 0:
                        if ( cursor.move(i)) { //cursor.move(2)
                            readingThree = cursor.getDouble(0);
                            String readingDateOneAbc = cursor.getString(1);
                            String readingDateOneBbc = cursor.getString(2);
                            readingDateThree = readingDateOneAbc + " and " + readingDateOneBbc;
                        }
                        break;
                }
            }
        }


        TextView tvReadingOne =(TextView) getActivity().findViewById(R.id.reading_one);
        TextView tvReadingTwo = (TextView) getActivity().findViewById(R.id.reading_two);
        TextView tvReadingThree = (TextView) getActivity().findViewById(R.id.reading_three);

        TextView tvReadingDateOne = (TextView) getActivity().findViewById(R.id.reading_date_one);
        TextView tvReadingDateTwo = (TextView) getActivity().findViewById(R.id.reading_date_two);
        TextView tvReadingDateThree = (TextView) getActivity().findViewById(R.id.reading_date_three);

        tvReadingOne.setText(MeterUtils.formatDoubleToTwoDecimalString(readingOne));
        tvReadingTwo.setText(MeterUtils.formatDoubleToTwoDecimalString(readingTwo) );
        tvReadingThree.setText(MeterUtils.formatDoubleToTwoDecimalString(readingThree));

        tvReadingDateOne.setText(readingDateOne);
        tvReadingDateTwo.setText(readingDateTwo);
        tvReadingDateThree.setText(readingDateThree);

    }

    private void populateMeterRechargeCardView(ContentResolver contentResolver){

        Cursor cursor = contentResolver.query(MeterReadingsContract.CONTENT_URI, theRechargeColumn,
                MeterReadingsContract.THE_RECHARGE_STATEMENT ,selectionArgs, MeterReadingsContract.DEFAULT_SORT);

        TextView tvRecharged = (TextView) getActivity().findViewById(R.id.last_time_recharged);
        TextView tvGot = (TextView) getActivity().findViewById(R.id.recharged_statistics_got);
        TextView tvFinishDay = (TextView) getActivity().findViewById(R.id.recharged_statistics_finish);
        if(cursor.moveToFirst()){
            cursor.move(0);
           // double reading =  cursor.getDouble(0);
           // String day = cursor.getString(1);
            String rechargedUnits = cursor.getString(2);
            Log.d(TAG, "the recharge val is " + rechargedUnits);
            long time = cursor.getLong(3);

            String rechargedAmount = MeterUtils.formatDoubleToTwoDecimalString(Double.valueOf(rechargedUnits));
           CharSequence ago =  DateUtils.getRelativeDateTimeString(getActivity(), time,
                   DateUtils.DAY_IN_MILLIS, DateUtils.YEAR_IN_MILLIS, DateUtils.FORMAT_SHOW_WEEKDAY);

             tvRecharged.setText("Recharged with R " + rechargedAmount +", " + ago);
             tvGot.setText("Received " + rechargedUnits + " kW");
            tvFinishDay.setText("will finish in "+(MeterUtils.formatDoubleToTwoDecimalString(daysLeftToFinish)) + " days");
        }else {
            tvRecharged.setText(nothingToShow);
            tvGot.setText(nothingToShow);
            tvFinishDay.setText(nothingToShow);
        }

    }

    private void populateMeterUsagesCardView(Cursor cursor){
        TextView history = (TextView) getActivity().findViewById(R.id.usage_above);
        TextView state = (TextView) getActivity().findViewById(R.id.usage_state);

        if (cursor.moveToFirst()){
            int day = 0;
             do {
                 double reading =  cursor.getDouble(0);
                 day += 1;
                Log.d(TAG, "the first reading is " + reading + " the day is " + day);
                reading += reading;

                 daysLeftToFinish = reading / day; //this gets the days left //its displayed in the method above

                Log.d(TAG, "you have used " + reading + "in the past " + day);
                 history.setText("you have used " + MeterUtils.formatDoubleToTwoDecimalString((reading/2)) + " kW in the past " //todo fix this it gives incorrect values
                         + day + (day == 1 ? " day" : " days" ));
                 state.setText(nothingToShow);

             }while (cursor.moveToNext() && day <= 31);
        }else {
            history.setText(nothingToShow);
            state.setText(nothingToShow);
            Log.d(TAG, nothingToShow);
        }
    }
}