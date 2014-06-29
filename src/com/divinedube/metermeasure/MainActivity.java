package com.divinedube.metermeasure;

import android.app.ListActivity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ListActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] FROM = {
            MeterReadingsContract.Column.DAY,
            MeterReadingsContract.Column.TIME,
            MeterReadingsContract.Column.READING,
            MeterReadingsContract.Column.NOTE
    };

    private static final int[] TO = {
            R.id.list_item_textViewDayMain,
            R.id.list_item_textViewTimeMain,
            R.id.list_item_textViewReadingMain,
            R.id.list_item_textViewNoteMain
    };

    private static final int LOADER_ID = 101;

    private SimpleCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c =
                db.query(MeterReadingsContract.TABLE, FROM, null, null, null, null, MeterReadingsContract.DEFAULT_SORT);


        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item, c, FROM, TO, 0);

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                startActivity(new Intent(this, NewMeterReadingsActivity.class));
            default:
                return false;
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
       if (id != LOADER_ID) return null;

        Log.d(TAG, "onCreate Loader");

       return new CursorLoader(this);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoad Finished");
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}