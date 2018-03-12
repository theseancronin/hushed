package com.android.shnellers.hushed.database.contracts;

import android.provider.BaseColumns;

/**
 * Created by sean on 01/02/18.
 */

public class CurrentPlaceContract
{
    private CurrentPlaceContract(){}

    public class CurrentEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Current_Place";
        public static final String PLACE = "place";
        public static final String USER_FRIENDLY_NAME = "user_friendly_name";
        public static final String NOTIFIED = "notified";
    }
}
