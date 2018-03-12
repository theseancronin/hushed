package com.android.shnellers.hushed.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.shnellers.hushed.database.contracts.LastRingModeContract;
import com.android.shnellers.hushed.database.helpers.LastRingModeDbHelper;
import com.android.shnellers.hushed.utils.StringUtils;

import static android.provider.BaseColumns._ID;
import static com.android.shnellers.hushed.database.contracts.LastRingModeContract.*;
import static com.android.shnellers.hushed.database.contracts.LastRingModeContract.LastRingMode.*;
import static com.android.shnellers.hushed.database.contracts.LastRingModeContract.LastRingMode.TABLE_NAME;
import static com.android.shnellers.hushed.database.contracts.LastRingModeContract.LastRingMode.USER_FRIENDLY_NAME;

/**
 * Created by sean on 12/02/18.
 */

public class LastRingModeDb
{
    private static final long FAILED = -1;
    private static final String ITEM_ID = _ID + " = 1";

    private LastRingModeDbHelper _helper;

    private Context _context;

    public LastRingModeDb(Context context)
    {
        _context = context;
        _helper = new LastRingModeDbHelper(context);
    }

    public long insert (int ringMode, String userFriendlyName)
    {
        long result = FAILED;

        SQLiteDatabase db = _helper.getWritableDatabase();
        final String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
        {
            ContentValues values = new ContentValues();
            values.put(RING_MODE, ringMode);
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

    public boolean existsInDb(int ringMode, String userFriendlyName)
    {
        SQLiteDatabase db = _helper.getReadableDatabase();

        final String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                int lastRingMode = cursor.getInt(cursor.getColumnIndex(RING_MODE));

                if (lastRingMode == ringMode)
                {
                    return true;
                }
            }

            cursor.close();
        }

        return false;
    }

    public int getLastRingMode()
    {
        SQLiteDatabase db = _helper.getReadableDatabase();
        int lastRingMode = -1;
        final String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                lastRingMode = cursor.getInt(cursor.getColumnIndex(RING_MODE));
            }
            cursor.close();
        }
        return lastRingMode;
    }

    public void reset()
    {
        SQLiteDatabase db = _helper.getWritableDatabase();
        _helper.reset(db);
    }
}
