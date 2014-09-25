package com.divinedube.metermeasure;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.divinedube.helpers.MeterUtils;

/**
 * Created by Divine Dube on 2014/07/04.
 */

/* todo: add thread for this class and make sure that the data loads change it into a intent service
   todo: to be called when new meter reading have been added add color to the recharged ones. change the layout to a
  todo: one line layout(every thing on online visually )*/

    //todo run this class with threads
public class MeterReadingsDiffFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    SharedPreferences prefs;

    double reading;
    String defaultTime;
    String day;
    String currentTime;
    Cursor mCursor;
    SimpleCursorAdapter mAdapter;
    //select all rows with the the default time and then just get the reading
    private final String TAG = MeterReadingsDiffFragment.class.getSimpleName();

    private static final String[] FROM = {
            ProcessedDataContract.Column.ID,
            ProcessedDataContract.Column.DAY_1,
            ProcessedDataContract.Column.DAY_2,
            ProcessedDataContract.Column.READING_FOR_DAY_1,
            ProcessedDataContract.Column.READING_FOR_DAY_2,
            ProcessedDataContract.Column.DIFFERENCE
    };

    private static final int[] TO = {
            android.R.id.empty,
            R.id.list_item_day_one,
            R.id.list_item_day_two,
            R.id.list_item_val0,
            R.id.list_item_val,
            R.id.list_item_reading
    };

    private static final int LOADER_ID = 3;


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onCreate(savedInstance);
        MeterUtils tool = new MeterUtils();
        currentTime = tool.getCurrentTime();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Cursor statsCursor = getActivity().getContentResolver().query
                (MeterReadingStatsContract.STATES_CONTENT_URI, null, null, null, null);

        int fff = statsCursor.getCount();

        defaultTime = prefs.getString("time", currentTime);
        if (defaultTime.equals(currentTime) || TextUtils.isEmpty(defaultTime)) {
            Toast.makeText(getActivity(), "please set the default time that you always check your meter readings",
                    Toast.LENGTH_LONG).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), "Generating your meter reading stats using  this " + defaultTime + "  time",
                    Toast.LENGTH_LONG).show();
        }
        String[] selectionArgs = {defaultTime};      //*** Didi motivated worX ***//

        mCursor = getActivity().getContentResolver().query
                (
                        MeterReadingsContract.CONTENT_URI, null,
                        MeterReadingsContract.THE_DIFF_SELECTION_STATEMENT,
                        selectionArgs,
                        MeterReadingsContract.DEFAULT_SORT
                );


        int noC = mCursor.getCount();
        ContentValues values = new ContentValues();
        while (mCursor.moveToNext()) {
            int i = mCursor.getPosition();
            Log.d(TAG, "now At " + i);
            reading = mCursor.getDouble(3);
            day = mCursor.getString(1);
            values.put(MeterReadingStatsContract.Column.ID, i);
            values.put(MeterReadingStatsContract.Column.READING_FOR_DAY_1, reading);
            values.put(MeterReadingStatsContract.Column.NAME_FOR_DAY_1, day);
            Log.d(TAG, " got  " + reading + " for  " + day);
            if (values.size() == 0) {
                Toast.makeText(getActivity(), "please record your meter reading first before checking your meter stats", Toast.LENGTH_LONG);
                getActivity().finish();
            } else {
                getActivity().getContentResolver().insert(MeterReadingStatsContract.STATES_CONTENT_URI, values);
            }
        }
       Toast.makeText(getActivity(), "number of columns returned: " + noC, Toast.LENGTH_LONG).show(); //Todo remove this in production

        Cursor statsCursorsSecond = getActivity().getContentResolver().query(MeterReadingStatsContract.STATES_CONTENT_URI, null, null, null, null);


        Log.d(TAG, "meter stats has " + fff + " rows");
        for (int i = 0; i < fff; i++) {  //to subtract the values in the meter_stats
            int pos = statsCursor.getPosition() + 1;
            double val2;
            String day2;
            if (statsCursorsSecond.moveToPosition(i + 1)) {
                val2 = statsCursorsSecond.getDouble(3);
                day2 = statsCursorsSecond.getString(1);
                Log.d(TAG, "the second row value is " + val2 + " on " + day2);
            } else {
                val2 = 0;
                day2 = "";
            }
            statsCursor.moveToNext();
            double val = statsCursor.getDouble(3);
            String day = statsCursor.getString(1);
            if (statsCursorsSecond.isAfterLast()) break; //clean data starts here

            ContentValues processedValues = new ContentValues();
            processedValues.put(ProcessedDataContract.Column.ID, pos);
            processedValues.put(ProcessedDataContract.Column.DAY_1, day);
            processedValues.put(ProcessedDataContract.Column.DAY_2, day2);
            processedValues.put(ProcessedDataContract.Column.READING_FOR_DAY_1, val); //bad
            processedValues.put(ProcessedDataContract.Column.READING_FOR_DAY_2, val2);
            val -= val2; //bad vars
            if (val < 0) processedValues.put(ProcessedDataContract.Column.RECHARGED, true);
            processedValues.put(ProcessedDataContract.Column.DIFFERENCE, val); //bad
            Uri uri = getActivity().getContentResolver().insert(ProcessedDataContract.P_DATA_CONTENT_URI, processedValues);
            Log.d(TAG, "the diff is: " + val + " of " + day + " and " + day2);
            Log.d(TAG, "the uri for the inserted data is " + uri);

            mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_meter_reading_diff, null, FROM, TO, 0);
            setListAdapter(mAdapter);

            getLoaderManager().initLoader(LOADER_ID, null, this);
            Log.d(TAG, "just loaded the and inserted the new values ");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_meter_reading_diff, null, FROM, TO, 0);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);
        Log.d(TAG, "just loaded the and inserted the new values ");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id != LOADER_ID) return null;
        return new CursorLoader(getActivity(), ProcessedDataContract.P_DATA_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}

