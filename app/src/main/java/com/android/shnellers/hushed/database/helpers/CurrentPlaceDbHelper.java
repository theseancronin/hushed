package com.android.shnellers.hushed.database.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.shnellers.hushed.database.contracts.CurrentPlaceContract;

import static com.android.shnellers.hushed.database.contracts.CurrentPlaceContract.CurrentEntry.PLACE;
import static com.android.shnellers.hushed.database.contracts.CurrentPlaceContract.CurrentEntry.TABLE_NAME;
import static com.android.shnellers.hushed.database.contracts.CurrentPlaceContract.CurrentEntry.USER_FRIENDLY_NAME;

/**
 * Created by sean on 01/02/18.
 */

public class CurrentPlaceDbHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "current_place.db";
    private static final int DB_VERSION = 1;

    private static final String CREATE_DB = "CREATE TABLE " + TABLE_NAME + " (" +
            CurrentPlaceContract.CurrentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PLACE + " TEXT," +
            USER_FRIENDLY_NAME + " TEXT)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public CurrentPlaceDbHelper(Context context)
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
