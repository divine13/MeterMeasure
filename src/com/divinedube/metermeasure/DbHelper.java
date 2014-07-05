package com.divinedube.metermeasure;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Divine Dube on 2014/06/27.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context){
        super(context, MeterReadingsContract.DB_NAME, null, MeterReadingsContract.DB_VERSION);
    }
    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format
             ("create table %s (%s integer primary key autoincrement, %s text, %s text, %s integer, %s text, %s integer, %s integer)",

                MeterReadingsContract.TABLE,
                MeterReadingsContract.Column.ID,
                MeterReadingsContract.Column.DAY,
                MeterReadingsContract.Column.TIME,
                MeterReadingsContract.Column.READING,
                MeterReadingsContract.Column.NOTE,
                MeterReadingsContract.Column.CREATED_AT,
                MeterReadingsContract.Column.DAY_DIFF);

       Log.d(TAG, "creating the db with this " + sql + " command in the onCreate method");
        db.execSQL(sql);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MeterReadingsContract.DROP_TABLE);
        onCreate(db);
    }
}
