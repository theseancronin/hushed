package com.android.shnellers.hushed.database.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.android.shnellers.hushed.database.contracts.CustomPlacesContract.CustomEntry.*;

/**
 * Created by sean on 21/02/18.
 */

public class CustomPlacesDbHelper extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 6;

    private static final String DB_NAME = "CustomPlaces.db";

    private static final String CREATE_DATABASE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PLACE_ID + " TEXT , " +
                    PLACE_NAME + " TEXT, " +
                    TYPE_NUMBER + " INTEGER, " +
                    ADDRESS + " TEXT," +
                    LONGITUDE + " REAL," +
                    LATITUDE + " REAL)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public CustomPlacesDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
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
