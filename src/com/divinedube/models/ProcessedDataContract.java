package com.divinedube.models;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Divine Dube on 2014/07/13.
 */
public class ProcessedDataContract  {

    public static final String TABLE = "p_data";
    public static final String P_DATA_AUTHORITY = "com.divinedube.models.ProcessedDataProvider";
    public static final Uri P_DATA_CONTENT_URI = Uri.parse("content://" + P_DATA_AUTHORITY + "/" + TABLE);
    public static final String DESC_ID_SORT_ORDER = "_id DESC";

    public static final String NULL_SELECTION_STATEMENT = "recharged is null";

    public class Column{
        public static final String ID = BaseColumns._ID;
        public static final String DAY_1 = "day_1";
        public static final String DAY_2 = "day_2";
        public static final String READING_FOR_DAY_1 = "reading_for_day_1";
        public static final String READING_FOR_DAY_2 = "reading_for_day_2";
        public static final String DIFFERENCE = "difference";
        public static final String RECHARGED = "recharged";
    }

}
