package com.divinedube.models;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Divine Dube on 2014/07/10.
 */
public class MeterReadingStatsProvider extends ContentProvider{

    private static final String TAG = MeterReadingStatsProvider.class.getSimpleName();
    DbHelper dbHelper;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MeterReadingStatsContract.STATS_AUTHORITY, MeterReadingStatsContract.TABLE, MeterReadingsContract.METER_DIR);
        sUriMatcher.addURI(MeterReadingStatsContract.STATS_AUTHORITY, MeterReadingStatsContract.TABLE + "/#", MeterReadingsContract.METER_TYPE);
    }


    @Override
    public boolean onCreate(){
      dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MeterReadingStatsContract.TABLE);

       switch (sUriMatcher.match(uri)){
           case MeterReadingsContract.METER_DIR:
               break;
           case MeterReadingsContract.METER_TYPE:
               qb.appendWhere(MeterReadingStatsContract.Column.ID + "=" + uri.getLastPathSegment());
               break;
           default:
               throw new IllegalArgumentException("illegal uri given: " + uri);
       }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
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
        Log.d(TAG, "INSERTING STATS IN DB: meter_states");

        if(sUriMatcher.match(uri) != MeterReadingsContract.METER_DIR) {
            throw new IllegalArgumentException("illegal uri specified  " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        rowId = db.insertWithOnConflict(MeterReadingStatsContract.TABLE,null,contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        if (rowId != -1){
            long id = rowId;
            rUri = ContentUris.withAppendedId(uri,id);

            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rUri;
    }

    //these are not really needed

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "!!!executing!!! delete method in " + TAG);
        int rowId;
        String where;

        switch (sUriMatcher.match(uri)){
            case MeterReadingsContract.METER_DIR:
                where  = (selection == null) ? "1" : selection;
                break;
            case MeterReadingsContract.METER_TYPE:
                where = MeterReadingsContract.Column.ID + "=" + "id" + (TextUtils.isEmpty(selection) ? "" : " and( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri );
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        rowId = db.delete(MeterReadingStatsContract.TABLE, where,selectionArgs);

        if (rowId > 0 ) getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAG, "***EXECUTED*** delete and returned the **DELETED** rows of " + rowId);
        return rowId;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Log.d(TAG, "!!!executing!!! update method in " + TAG);
        int rowId;
        String where;
        switch (sUriMatcher.match(uri)){
            case MeterReadingsContract.METER_DIR:
                where = selection;
                break;
            case MeterReadingsContract.METER_TYPE: //this one i when they have given a Uri with a id
                long id = ContentUris.parseId(uri);
                where = MeterReadingsContract.Column.ID + " = " + id  + (TextUtils.isEmpty(selection) ? "" : " and( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        rowId = db.update(MeterReadingStatsContract.TABLE, contentValues,where,selectionArgs);

        if (rowId > 0 )
            getContext().getContentResolver().notifyChange(uri,null);

        Log.d(TAG, "***EXECUTED*** update and returned the **UPDATED** rows of " + rowId);
        return rowId;
      }

}
