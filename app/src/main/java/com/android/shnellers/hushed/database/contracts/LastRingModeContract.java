package com.android.shnellers.hushed.database.contracts;

import android.provider.BaseColumns;

/**
 * Created by sean on 12/02/18.
 */

public class LastRingModeContract
{
    private LastRingModeContract(){}

    public static class LastRingMode implements BaseColumns
    {
        public static final String TABLE_NAME = "last_ring_mode";

        public static final String RING_MODE = "ring_mode";

        public static final String USER_FRIENDLY_NAME = "user_friendly_name";

    }
}
