package com.android.shnellers.hushed.database.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.shnellers.hushed.constants.Constants;
import com.android.shnellers.hushed.database.contracts.HushedPlacesContract;

/**
 * Created by sean on 06/12/17.
 */

public class HushedPlacesDbHelper extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 6;

    private static final String CREATE_DATABASE =
            "CREATE TABLE " + HushedPlacesContract.Entry.TABLE_NAME + " (" +
                HushedPlacesContract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HushedPlacesContract.Entry.PLACE_TYPE + " TEXT, " +
                HushedPlacesContract.Entry.USER_FRIENDLY_NAME + " TEXT, " +
                HushedPlacesContract.Entry.RING_MODE + " INTEGER," +
                HushedPlacesContract.Entry.CONST_VALUE + "  INTEGER)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + HushedPlacesContract.Entry.TABLE_NAME;

    public HushedPlacesDbHelper(Context context)
    {
        super(context, Constants.DB_NAME, null, DB_VERSION);
    }

    /**
     * Creates the database table.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_DATABASE);
    }

    /**
     * Called when the table needs to be updated. The old table is removed and the
     * new version is created. Can also be used to clear the values in the database.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (newVersion > oldVersion)
        {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }
}
