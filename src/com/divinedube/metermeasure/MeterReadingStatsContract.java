package com.divinedube.metermeasure;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Divine Dube on 2014/07/10.
 */

//TODO I think this can be done in a more OOP way
public class MeterReadingStatsContract {

    public static final String TABLE = "meter_stats";
    public static final String STATS_AUTHORITY = "com.divinedube.metermeasure.MeterReadingStatsProvider";
    public static final Uri STATES_CONTENT_URI = Uri.parse("content://" + STATS_AUTHORITY + "/" + TABLE);


    public class Column{
        public static final String ID = BaseColumns._ID;
        public static final String NAME_FOR_DAY_1 = "name_for_day_1";
        public static final String NAME_FOR_DAY_2 = "name_for_day_2";
        public static final String READING_FOR_DAY_1 = "reading_for_day_1";
        public static final String READING_FOR_DAY_2 = "reading_for_day_2";
        public static final String DIFFERENCE = "difference ";
    }
}