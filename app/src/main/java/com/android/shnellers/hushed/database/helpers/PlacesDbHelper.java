package com.android.shnellers.hushed.database.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.shnellers.hushed.constants.Constants;

import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.CONST_VALUE;
import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.ICON;
import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.TABLE_NAME;
import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.PLACE;
import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.USER_FRIENDLY_NAME;

/**
 * A helper class for creating and updating the UserPlaces database that holds places that
 * have been hushed. They will be removed if the user deletes a place from the list of
 * hushed places.
 *
 * Created by sean on 10/01/18.
 */
public class PlacesDbHelper extends SQLiteOpenHelper
{
    private static final String TAG = PlacesDbHelper.class.getSimpleName();

    private static final int VERSION = 2;

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            PLACE + " TEXT, " +
            USER_FRIENDLY_NAME + " TEXT," +
            ICON + " INTEGER," +
            CONST_VALUE + " INTEGER)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public PlacesDbHelper(Context context)
    {
        super(context, Constants.PLACES_DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
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
}
