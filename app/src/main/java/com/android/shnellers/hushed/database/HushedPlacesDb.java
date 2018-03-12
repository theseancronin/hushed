package com.android.shnellers.hushed.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.android.shnellers.hushed.database.contracts.HushedPlacesContract;
import com.android.shnellers.hushed.database.helpers.HushedPlacesDbHelper;
import com.android.shnellers.hushed.models.HushedPlace;
import com.android.shnellers.hushed.models.Mode;
import com.android.shnellers.hushed.utils.RingerMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.BaseColumns._ID;
import static com.android.shnellers.hushed.database.contracts.HushedPlacesContract.*;
import static com.android.shnellers.hushed.database.contracts.HushedPlacesContract.Entry.*;

/**
 * Created by sean on 11/12/17.
 */

public class HushedPlacesDb
{
    private HushedPlacesDbHelper _dbHelper;
    private Context _context;

    public HushedPlacesDb(Context context)
    {
        _context = context;
        _dbHelper = new HushedPlacesDbHelper(context);
    }

    /**
     * Returns the the list of places object containing all details from the
     * database.
     *
     * @return
     */
    public List<HushedPlace> getHushedPlaces()
    {
        List<HushedPlace> hushedPlaces = new ArrayList<>();
        try
        {
            SQLiteDatabase db = _dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + _ID + " DESC";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext())
            {
                String type = cursor.getString(cursor.getColumnIndex(PLACE_TYPE));
                HushedPlace hushedPlace = createHushedPlace(cursor, type);
                hushedPlaces.add(hushedPlace);
            }
            cursor.close();
            db.close();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return hushedPlaces;
    }


    /**
     * Returns the list of places types in user friendly format.
     *
     * @return
     */
    public List<String> getHushedUserFriendlyNames()
    {
        List<String> hushedPlaces = new ArrayList<>();

        try
        {
            SQLiteDatabase db = _dbHelper.getReadableDatabase();

            String query = "SELECT * FROM " + TABLE_NAME;

            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext())
            {
                String type = cursor.getString(cursor.getColumnIndex(USER_FRIENDLY_NAME));
                hushedPlaces.add(type);
            }

            cursor.close();
            db.close();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return hushedPlaces;
    }

    /**
     * Returns the list of places types in none user friendly format.
     *
     * @return
     */
    public List<String> getPlacesTypes()
    {
        List<String> hushedPlaces = new ArrayList<>();

        try
        {
            SQLiteDatabase db = _dbHelper.getReadableDatabase();

            String query = "SELECT * FROM " + TABLE_NAME;

            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext())
            {
                String type = cursor.getString(cursor.getColumnIndex(PLACE_TYPE));
                hushedPlaces.add(type);
            }

            cursor.close();
            db.close();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return hushedPlaces;
    }

    public Map<String, HushedPlace> getMapOfHushedPlaces()
    {
        Map<String, HushedPlace> hushedPlaces = new HashMap<>();

        try
        {
            SQLiteDatabase db = _dbHelper.getReadableDatabase();

            String query = "SELECT * FROM " + TABLE_NAME;

            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext())
            {
                String type = cursor.getString(cursor.getColumnIndex(PLACE_TYPE));
                HushedPlace place = createHushedPlace(cursor, type);
                hushedPlaces.put(type, place);
            }

            cursor.close();
            db.close();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return hushedPlaces;
    }


    /**
     * Remove an item from the database based on the id passed as argument.
     *
     * @param id
     * @return
     * @throws SQLiteException
     */
    public int removeHushedPlaceFromDb(int id) throws SQLiteException
    {
        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        int result = db.delete(TABLE_NAME, _ID + " = " + id, null);
        db.close();
        return result;
    }

    private HushedPlace createHushedPlace(Cursor cursor, String type)
    {
        String userFriendlyName = cursor.getString(cursor.getColumnIndex(USER_FRIENDLY_NAME));
        int ringMode = cursor.getInt(cursor.getColumnIndex(RING_MODE));
        int id = cursor.getInt(cursor.getColumnIndex(_ID));
        int constValue = cursor.getInt(cursor.getColumnIndex(CONST_VALUE));
        Mode mode = RingerMode.RINGER_MODE.get(ringMode);
        String userFriendlyRingMode = mode.getUserFriendlyName();

        return new HushedPlace(id, type, userFriendlyRingMode, userFriendlyName, constValue);
    }
}
