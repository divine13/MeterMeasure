package com.divinedube.metermeasure;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import android.support.v4.widget.SimpleCursorAdapter;

import com.divinedube.models.MeterReadingsContract;

/**
 * Created by Divine Dube on 2014/06/29.
 */
public class FragmentList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = FragmentList.class.getSimpleName();

    private static final String[] FROM = {
            MeterReadingsContract.Column.ID,
            MeterReadingsContract.Column.DAY,
            MeterReadingsContract.Column.TIME,
            MeterReadingsContract.Column.READING,
            MeterReadingsContract.Column.NOTE
    };

    private static final int[] TO = {
            android.R.id.empty,
            R.id.list_item_textViewDayMain,
            R.id.list_item_textViewTimeMain,
            R.id.list_item_textViewReadingMain,
            R.id.list_item_textViewNoteMain
    };

    private static final int LOADER_ID = 36;
    private SimpleCursorAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        setEmptyText("You currently have no Electricity Meter Readings press the add(+) to add your own");

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, FROM, TO, 0);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_ID,null,this);

    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id != LOADER_ID) return null;
        Log.d(TAG, "onCreate Loader");

        return new android.support.v4.content.CursorLoader(
                getActivity(),MeterReadingsContract.CONTENT_URI, null, null, null, MeterReadingsContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }


//   TODO after a long click let people delete and edit the data
// public void registerForContextMenu(View view) {
//        super.registerForContextMenu(view);
    //check out
//    }
}