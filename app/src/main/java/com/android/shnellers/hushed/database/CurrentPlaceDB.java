package com.android.shnellers.hushed.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.shnellers.hushed.database.contracts.CurrentPlaceContract;
import com.android.shnellers.hushed.database.helpers.CurrentPlaceDbHelper;
import com.android.shnellers.hushed.utils.StringUtils;

import static android.provider.BaseColumns._ID;
import static com.android.shnellers.hushed.database.contracts.CurrentPlaceContract.CurrentEntry.PLACE;
import static com.android.shnellers.hushed.database.contracts.CurrentPlaceContract.CurrentEntry.TABLE_NAME;
import static com.android.shnellers.hushed.database.contracts.CurrentPlaceContract.CurrentEntry.USER_FRIENDLY_NAME;

/**
 * Created by sean on 01/02/18.
 */

public class CurrentPlaceDB
{
    private static final long INSERTED = 1;
    private static final long FAILED = -1;
    public static final String ITEM_ID = _ID + " = 1";

    private CurrentPlaceDbHelper _helper;

    private Context _context;

    public CurrentPlaceDB(Context context)
    {
        _context = context;
        _helper = new CurrentPlaceDbHelper(context);
    }

    public long insert (String place, String userFriendlyName)
    {
        long result = FAILED;

        SQLiteDatabase db = _helper.getWritableDatabase();
        final String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
        {
            ContentValues values = new ContentValues();
            values.put(PLACE, place);
            values.put(USER_FRIENDLY_NAME, userFriendlyName);

            if (cursor.moveToFirst())
            {
                result = db.update(TABLE_NAME, values, ITEM_ID, null);
            }
            else
            {
                result = db.insert(TABLE_NAME, null, values);
            }
            cursor.close();
        }
        db.close();

        return result;
    }

    public boolean existsInDb(String place, String userFriendlyName)
    {
        SQLiteDatabase db = _helper.getReadableDatabase();

        final String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                String currentPlace = cursor.getString(cursor.getColumnIndex(PLACE));

                if (currentPlace.equals(place))
                {
                    return true;
                }
            }

            cursor.close();
        }

        return false;
    }

    public String getCurrentPlace()
    {
        SQLiteDatabase db = _helper.getReadableDatabase();
        String currentPlace = StringUtils.EMPTY_STRING;
        final String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                currentPlace = cursor.getString(cursor.getColumnIndex(PLACE));
            }
            cursor.close();
        }
        return currentPlace;
    }

    public void reset()
    {
        SQLiteDatabase db = _helper.getWritableDatabase();
        _helper.reset(db);
    }
}
