package com.divinedube.metermeasure;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Divine Dube on 2014/06/27.
 */
public class MeterReadingsContract {

    //trying to be verbose, this could be a bit shorter but lets be simple and perfect for this one

    public static final String DB_NAME = "meterReadingsTable.db";
    public static final String TABLE = "meter";
    public static final String DEFAULT_SORT = Column.CREATED_AT + " DESC";
    public static final String NORMAL_SORT_ORDER = Column.CREATED_AT + " ASC"; //TODO sort the ordering check in book
    public static final int DB_VERSION = 1;
    public static final String DROP_TABLE = "drop table if exists " + TABLE ;
    public static final String AUTHORITY = "com.divinedube.metermeasure.MeterRecordingsProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);
    public static final String READING_TYPE_ITEM =
            "vnd.android.cursor.type/vnd.com.divinedube.metermeasure.provider.meter";
    public static final String READING_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.com.divinedube.metermeasure.provider.meter";

    public static final int METER_TYPE = 1;
    public static final int METER_DIR = 2;
    public static final String THE_DIFF_SELECTION_STATEMENT = "time=?"; //simple to the eye, it was not

    public class Column{
        public static final String ID = BaseColumns._ID;
        public static final String CREATED_AT = "created_at";
        public static final String DAY = "day";
        public static final String TIME = "time";
        public static final String READING = "reading";
        public static final String NOTE = "note";
        public static final String DAY_DIFF = "day_diff";
    }
}
