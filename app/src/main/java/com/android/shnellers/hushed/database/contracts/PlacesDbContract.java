package com.android.shnellers.hushed.database.contracts;

import android.provider.BaseColumns;

/**
 * Created by sean on 10/01/18.
 */

public class PlacesDbContract
{
    private PlacesDbContract(){}

    public class UsedPlacesEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "used_places_table";

        public static final String PLACE = "place";

        public static final String USER_FRIENDLY_NAME = "user_friendly_name";

        public static final String ICON = "icon";

        public static final String CONST_VALUE = "const_value";
    }
}
