package com.android.shnellers.hushed.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.shnellers.hushed.database.helpers.CustomPlacesDbHelper;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;

import static android.provider.BaseColumns._ID;
import static com.android.shnellers.hushed.database.contracts.CustomPlacesContract.CustomEntry.ADDRESS;
import static com.android.shnellers.hushed.database.contracts.CustomPlacesContract.CustomEntry.LATITUDE;
import static com.android.shnellers.hushed.database.contracts.CustomPlacesContract.CustomEntry.LONGITUDE;
import static com.android.shnellers.hushed.database.contracts.CustomPlacesContract.CustomEntry.PLACE_ID;
import static com.android.shnellers.hushed.database.contracts.CustomPlacesContract.CustomEntry.PLACE_NAME;
import static com.android.shnellers.hushed.database.contracts.CustomPlacesContract.CustomEntry.TABLE_NAME;
import static com.android.shnellers.hushed.database.contracts.CustomPlacesContract.CustomEntry.TYPE_NUMBER;

/**
 * Created by sean on 21/02/18.
 */

public class CustomPlacesDb
{
    private static final long FAILED = -1;

    private CustomPlacesDbHelper _helper;

    public CustomPlacesDb(Context context)
    {
        _helper = new CustomPlacesDbHelper(context);
    }

    public long insert(String placeId, String place, int typeNumber, String address, double longitude, double latitude)
    {
        long result;

        SQLiteDatabase db = _helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PLACE_ID, placeId);
        values.put(PLACE_NAME, place);
        values.put(TYPE_NUMBER, typeNumber);
        values.put(ADDRESS, address);
        values.put(LONGITUDE, longitude);
        values.put(LATITUDE, latitude);

        result = db.insert(TABLE_NAME, null, values);
        db.close();

        return result;
    }

    public int remove (String place)
    {
        SQLiteDatabase db = _helper.getWritableDatabase();
        int result = -1;

        int id = getItemId(place, db);

        if (id > -1)
        {
            result = db.delete(TABLE_NAME, _ID + " = " + id, null);
        }


        db.close();

        return result;
    }

    private int getItemId(String place, SQLiteDatabase db)
    {
        int id = -1;

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + PLACE_NAME + "=?";

        Cursor cursor = db.rawQuery(query, new String[] { place });

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                id = cursor.getInt(cursor.getColumnIndex(_ID));
            }

            cursor.close();
        }

        return id;
    }


    public long existsId(String placeId)
    {
        long result = -1;

        SQLiteDatabase db = _helper.getReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + PLACE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] {placeId});

        if (cursor.getCount() > 0)
        {
            result = 1;
        }
        cursor.close();

        return result;
    }

    public long existsPlace(String place)
    {
        long result = -1;

        SQLiteDatabase db = _helper.getReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + PLACE_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] {place});

        if (cursor.moveToFirst())
        {
            result = 1;
        }
        cursor.close();

        return result;
    }

    public String getPlaceId(String place)
    {
        String placeId = null;

        SQLiteDatabase db = _helper.getReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + PLACE_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] {place});

        if (cursor.moveToFirst())
        {
            placeId = cursor.getString(cursor.getColumnIndex(PLACE_ID));
        }
        cursor.close();

        return placeId;
    }

    public int existsAddress(String address)
    {
        int exists = -1;

        SQLiteDatabase db = _helper.getReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + ADDRESS + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { address });

        if (cursor.moveToFirst())
        {
            exists = 1;
        }
        cursor.close();

        return exists;
    }
}
