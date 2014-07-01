package com.divinedube.metermeasure;

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
 * Created by Divine Dube on 2014/06/30.
 */
public class MeterRecordingsProvider extends ContentProvider {
    private static final String TAG = MeterReadingsContract.class.getSimpleName();

    private DbHelper dbHelper;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MeterReadingsContract.AUTHORITY, MeterReadingsContract.TABLE, MeterReadingsContract.METER_DIR);
        sUriMatcher.addURI(MeterReadingsContract.AUTHORITY, MeterReadingsContract.TABLE + "/#", MeterReadingsContract.METER_TYPE);
    }

    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     * <p/>
     * <p>You should defer nontrivial initialization (such as opening,
     * upgrading, and scanning databases) until the content provider is used
     * (via {@link #query}, {@link #insert}, etc).  Deferred initialization
     * keeps application startup fast, avoids unnecessary work if the provider
     * turns out not to be needed, and stops database errors (such as a full
     * disk) from halting application launch.
     * <p/>
     * <p>If you use SQLite, {@link android.database.sqlite.SQLiteOpenHelper}
     * is a helpful utility class that makes it easy to manage databases,
     * and will automatically defer opening until first use.  If you do use
     * SQLiteOpenHelper, make sure to avoid calling
     * {@link android.database.sqlite.SQLiteOpenHelper#getReadableDatabase} or
     * {@link android.database.sqlite.SQLiteOpenHelper#getWritableDatabase}
     * from this method.  (Instead, override
     * {@link android.database.sqlite.SQLiteOpenHelper#onOpen} to initialize the
     * database when it is first opened.)
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
       dbHelper = new DbHelper(getContext());
        return true;
    }

    /**
     * Implement this to handle query requests from clients.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     * Example client call:<p>
     * <pre>// Request a specific record.
     * Cursor managedCursor = managedQuery(
     * ContentUris.withAppendedId(Contacts.People.CONTENT_URI, 2),
     * projection,    // Which columns to return.
     * null,          // WHERE clause.
     * null,          // WHERE clause value substitution
     * People.NAME + " ASC");   // Sort order.</pre>
     * Example implementation:<p>
     * <pre>// SQLiteQueryBuilder is a helper class that creates the
     * // proper SQL syntax for us.
     * SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
     * <p/>
     * // Set the table we're querying.
     * qBuilder.setTables(DATABASE_TABLE_NAME);
     * <p/>
     * // If the query ends in a specific record number, we're
     * // being asked for a specific record, so set the
     * // WHERE clause in our query.
     * if((URI_MATCHER.match(uri)) == SPECIFIC_MESSAGE){
     * qBuilder.appendWhere("_id=" + uri.getPathLeafId());
     * }
     * <p/>
     * // Make the query.
     * Cursor c = qBuilder.query(mDb,
     * projection,
     * selection,
     * selectionArgs,
     * groupBy,
     * having,
     * sortOrder);
     * c.setNotificationUri(getContext().getContentResolver(), uri);
     * return c;</pre>
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MeterReadingsContract.TABLE);

        switch (sUriMatcher.match(uri)){
            case MeterReadingsContract.METER_DIR:
                 break;
            case MeterReadingsContract.METER_TYPE:
                 qb.appendWhere(MeterReadingsContract.Column.ID  + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri " + uri);
        }
        String orderBy = (TextUtils.isEmpty(sortOrder)) ? MeterReadingsContract.DEFAULT_SORT : sortOrder;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = qb.query(db,projection,selection,selectionArgs,null,null, orderBy);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * <code>vnd.android.cursor.item</code> for a single record,
     * or <code>vnd.android.cursor.dir/</code> for multiple items.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     * <p>Note that there are no permissions needed for an application to
     * access this information; if your content provider requires read and/or
     * write permissions, or is not exported, all applications can still call
     * this method regardless of their access permissions.  This allows them
     * to retrieve the MIME type for a URI when dispatching intents.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case MeterReadingsContract.METER_DIR:
                Log.d(TAG, "got type" + MeterReadingsContract.READING_TYPE_DIR);
                return  MeterReadingsContract.READING_TYPE_DIR;
            case MeterReadingsContract.METER_TYPE:
                Log.d(TAG, "got type " + MeterReadingsContract.READING_TYPE_ITEM);
                return MeterReadingsContract.READING_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("illegal uri " + uri);
        }
    }

    /**
     * Implement this to handle requests to insert a new row.
     * As a courtesy, call {@link ContentResolver#notifyChange(android.net.Uri, android.database.ContentObserver) notifyChange()}
     * after inserting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "!!!executing!!! insert method in " + TAG);

        Uri myUri = null;
         if (sUriMatcher.match(uri) != MeterReadingsContract.METER_DIR){
             throw new IllegalArgumentException("illegal uri:" + uri);
         }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

       long rowId =  db.insertWithOnConflict(MeterReadingsContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        if (rowId != -1){
            long id = rowId;
            myUri = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "inserted uri" + myUri);

            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "***EXECUTED*** insert and returned the **INSERTED** rows of " + rowId);

        return myUri;
    }

    /**
     * Implement this to handle requests to delete one or more rows.
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.
     * As a courtesy, call {@link ContentResolver#notifyChange(android.net.Uri, android.database.ContentObserver) notifyDelete()}
     * after deleting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     * <p>The implementation is responsible for parsing out a row ID at the end
     * of the URI, if a specific row is being deleted. That is, the client would
     * pass in <code>content://contacts/people/22</code> and the implementation is
     * responsible for parsing the record number (22) when creating a SQL statement.
     *
     * @param uri           The full URI to query, including a row ID (if a specific record is requested).
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs
     * @return The number of rows affected.
     * @throws SQLException
     */
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
        rowId = db.delete(MeterReadingsContract.TABLE, where,selectionArgs);

        if (rowId > 0 ) getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAG, "***EXECUTED*** delete and returned the **DELETED** rows of " + rowId);
        return rowId;

    }

    /**
     * Implement this to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     * As a courtesy, call {@link ContentResolver#notifyChange(android.net.Uri, android.database.ContentObserver) notifyChange()}
     * after updating.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     An optional filter to match rows to update.
     * @param selectionArgs
     * @return the number of rows affected.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        Log.d(TAG, "!!!executing!!! update method in " + TAG);
        int rowId;
        String where;
        switch (sUriMatcher.match(uri)){
            case MeterReadingsContract.METER_DIR:
                where = selection;
                break;
            case MeterReadingsContract.METER_TYPE: //this one i when they have given a Uri with id
                long id = ContentUris.parseId(uri);
                where = MeterReadingsContract.Column.ID + "=" + "id" + (TextUtils.isEmpty(selection) ? "" : " and( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

         rowId = db.update(MeterReadingsContract.TABLE,values,where,selectionArgs);

        if (rowId > 0 )
            getContext().getContentResolver().notifyChange(uri,null);

        Log.d(TAG, "***EXECUTED*** update and returned the **UPDATED** rows of " + rowId);
        return rowId;
    }
}
