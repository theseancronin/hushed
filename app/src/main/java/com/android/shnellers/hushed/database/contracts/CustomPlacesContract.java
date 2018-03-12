package com.android.shnellers.hushed.database.contracts;

import android.provider.BaseColumns;

/**
 * Created by sean on 21/02/18.
 */

public class CustomPlacesContract
{
    private CustomPlacesContract(){}

    public static class CustomEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "custom_places";

        public static final String PLACE_NAME = "place_name";

        public static final String PLACE_ID = "place_id";

        public static final String TYPE_NUMBER = "type_number";

        public static final String ADDRESS = "address";

        public static final String LONGITUDE = "longitude";

        public static final String LATITUDE = "latitude";
    }
}
