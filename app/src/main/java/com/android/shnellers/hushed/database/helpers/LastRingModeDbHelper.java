package com.android.shnellers.hushed.database.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.shnellers.hushed.database.contracts.CurrentPlaceContract;
import com.android.shnellers.hushed.database.contracts.LastRingModeContract;

import static com.android.shnellers.hushed.database.contracts.LastRingModeContract.*;
import static com.android.shnellers.hushed.database.contracts.LastRingModeContract.LastRingMode.*;
import static com.android.shnellers.hushed.database.contracts.LastRingModeContract.LastRingMode.RING_MODE;
import static com.android.shnellers.hushed.database.contracts.LastRingModeContract.LastRingMode.TABLE_NAME;
import static com.android.shnellers.hushed.database.contracts.LastRingModeContract.LastRingMode.USER_FRIENDLY_NAME;

/**
 * Created by sean on 12/02/18.
 */

public class LastRingModeDbHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "last_ring_mode.db";
    private static final int DB_VERSION = 1;

    private static final String CREATE_DB = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            RING_MODE + " INTEGER," +
            USER_FRIENDLY_NAME + " TEXT)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public LastRingModeDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (newVersion > oldVersion)
        {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }

    public void reset(SQLiteDatabase db)
    {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}