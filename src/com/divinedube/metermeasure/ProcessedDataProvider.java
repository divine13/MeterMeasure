package com.divinedube.metermeasure;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Divine Dube on 2014/07/13.
 */
public class ProcessedDataProvider extends ContentProvider{

    DbHelper dbHelper;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static String TAG = ProcessedDataContract.class.getSimpleName();

    static {
        sUriMatcher.addURI(ProcessedDataContract.P_DATA_AUTHORITY,ProcessedDataContract.TABLE,MeterReadingsContract.METER_DIR);
        sUriMatcher.addURI(ProcessedDataContract.P_DATA_AUTHORITY,ProcessedDataContract.TABLE + "/#",MeterReadingsContract.METER_TYPE);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return  true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ProcessedDataContract.TABLE);

        switch (sUriMatcher.match(uri)){
            case MeterReadingsContract.METER_DIR:
                break;
            case MeterReadingsContract.METER_TYPE:
                qb.appendWhere(ProcessedDataContract.Column.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal Uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = qb.query(db,projection,selection,selectionArgs,null,null,null);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        Log.d(TAG, "searched the db with Uri of: " + uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
       Uri rUri = null;
        long rowId;

        Log.d(TAG, "INSERTING Processed Data IN DB: p_data");

        if (sUriMatcher.match(uri) != MeterReadingsContract.METER_DIR){
            throw new IllegalArgumentException("Illegal Uri: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
      rowId =   db.insertWithOnConflict(ProcessedDataContract.TABLE,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE); //Todo change the CONFLICT_REPLACE

        if (rowId != -1){
            long id = rowId;
            rUri = ContentUris.withAppendedId(uri,id);

            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "the Uri is: " + rUri);
         return rUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
