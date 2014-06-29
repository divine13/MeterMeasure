package com.divinedube.metermeasure;

import android.provider.BaseColumns;

/**
 * Created by Divine Dube on 2014/06/27.
 */
public class MeterReadingsContract {

    //trying to be verbose, this could be a bit shorter but lets be simple and perfect for this one

    public static final String DB_NAME = "meterReadingsTable.db";
    public static final String TABLE = "meter";
    public static final String DEFAULT_SORT = Column.CREATED_AT;
    public static final int DB_VERSION = 1;
    public static final String DROP_TABLE = "drop table if exists " + TABLE ;

    public class Column{
        public static final String ID = BaseColumns._ID;
        public static final String CREATED_AT = "created_at";
        public static final String DAY = "day";  //i will make this string for now in the table TODO
        public static final String TIME = "time"; //i will make this int for now in the table  TODO
        public static final String READING = "reading";
        public static final String NOTE = "note";
        public static final String NULL_COLUMN = "null_column";

    }
}
