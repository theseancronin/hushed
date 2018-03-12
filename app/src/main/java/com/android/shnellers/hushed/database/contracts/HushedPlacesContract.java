package com.android.shnellers.hushed.database.contracts;

import android.provider.BaseColumns;

/**
 * Created by sean on 06/12/17.
 */

public class HushedPlacesContract
{


    private HushedPlacesContract(){}

    public static class Entry implements BaseColumns
    {
        public static final String TABLE_NAME = "hushed_places";

        public static final String PLACE_TYPE = "place_type";

        public static final String USER_FRIENDLY_NAME = "user_friendly_name";

        public static final String RING_MODE = "ring_mode";

        public static final String CONST_VALUE = "const_value";
    }
}
