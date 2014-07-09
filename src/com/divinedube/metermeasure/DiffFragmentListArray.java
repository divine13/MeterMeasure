package com.divinedube.metermeasure;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * Created by Divine Dube on 2014/07/06.
 */
public class DiffFragmentListArray extends ListFragment implements LoaderManager.LoaderCallbacks<ArrayMap> {

    private static String TAG= DiffFragmentListArray.class.getSimpleName();

    private static final  int[] TO = {R.id.list_item_day_one, R.id.list_item_day_two};

    SharedPreferences preferences;
    MeterUtils util;
    Cursor mCursor;
    double reading;
    String defaultTime;
    String day;
    ArrayMap<String, Double> arrayMap;
    private ArrayAdapter mArrayAdapter;


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defaultTime = preferences.getString("time", "");
        util = new MeterUtils();
        String currentTime = util.getCurrentTime();

        if (defaultTime.equals(currentTime) || TextUtils.isEmpty(currentTime)) {
            setEmptyText("Please add the time, that you always check your meter box readings to your settings");
        } else {
            String[] selectionArgs = {defaultTime};
            mCursor = getActivity().getContentResolver().query
                    (
                            MeterReadingsContract.CONTENT_URI, null,
                            MeterReadingsContract.THE_DIFF_SELECTION_STATEMENT,
                            selectionArgs,
                            MeterReadingsContract.NORMAL_SORT_ORDER
                    );
            int noC = mCursor.getCount();

            while (mCursor.moveToNext()) {
                int i = mCursor.getPosition();
                i = i + 1;
                Log.d(TAG, "now At " + i);
                double r = 0;
                reading = mCursor.getDouble(3);
                day = mCursor.getString(1);
                arrayMap = new ArrayMap<String, Double>();
                arrayMap.put(day, reading);
                Log.d(TAG, " got  " + reading + " for  " + day);
            }



           // mArrayAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_meter_reading_diff,arrayMap);
        }
    }// end onCreate

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<ArrayMap> onCreateLoader(int id, Bundle args) {
       return null;
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p/>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link android.database.Cursor}
     * and you place it in a {@link android.widget.CursorAdapter}, use
     * the {@link android.widget.CursorAdapter#CursorAdapter(android.content.Context,
     * android.database.Cursor, int)} constructor <em>without</em> passing
     * in either {@link android.widget.CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link android.widget.CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link android.database.Cursor} from a {@link android.content.CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link android.widget.CursorAdapter}, you should use the
     * {@link android.widget.CursorAdapter#swapCursor(android.database.Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ArrayMap> loader, ArrayMap data) {

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<ArrayMap> loader) {

    }
}
