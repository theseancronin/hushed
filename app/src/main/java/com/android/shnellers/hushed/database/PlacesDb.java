package com.android.shnellers.hushed.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.android.shnellers.hushed.database.helpers.PlacesDbHelper;
import com.android.shnellers.hushed.models.PlaceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.CONST_VALUE;
import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.ICON;
import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.PLACE;
import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.TABLE_NAME;
import static com.android.shnellers.hushed.database.contracts.PlacesDbContract.UsedPlacesEntry.USER_FRIENDLY_NAME;

/**
 * Created by sean on 10/01/18.
 */

public class PlacesDb
{
    private PlacesDbHelper _dbHelper;
    private Context _context;

    public PlacesDb(Context context)
    {
        _context = context;
        _dbHelper = new PlacesDbHelper(_context);
    }

    /**
     * Returns a list of used places.
     *
     * @return
     * @throws SQLiteException
     */
    public Map<String, PlaceModel> getPlaces() throws SQLiteException
    {
        Map<String, PlaceModel> places = new HashMap<>();

        SQLiteDatabase db = _dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            String place = cursor.getString(cursor.getColumnIndex(PLACE));
            String userFriendlyName = cursor.getString(cursor.getColumnIndex(USER_FRIENDLY_NAME));
            int icon = cursor.getInt(cursor.getColumnIndex(ICON));
            int constValue = cursor.getInt(cursor.getColumnIndex(CONST_VALUE));

            PlaceModel placeObject = new PlaceModel(place, userFriendlyName, icon, constValue);
            places.put(userFriendlyName, placeObject);
        }

        cursor.close();
        db.close();

        return places;
    }

    public List<String> getPlacesAsList() throws SQLiteException
    {
        List<String> places = new ArrayList<>();

        SQLiteDatabase db = _dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            String userFriendlyName = cursor.getString(cursor.getColumnIndex(USER_FRIENDLY_NAME));
            places.add(userFriendlyName);
        }

        cursor.close();
        db.close();

        Collections.sort(places);

        return places;
    }

    /**
     * Inserts an item into the database.
     *
     * @param place
     * @param userFriendlyName
     * @param constValue
     * @return insert determines whether item added to database or not.
     */
    public long addPlace(final String place, final String userFriendlyName, final int icon, int constValue) throws SQLiteException
    {
        SQLiteDatabase db = _dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PLACE, place);
        contentValues.put(USER_FRIENDLY_NAME, userFriendlyName);
        contentValues.put(ICON, icon);
        contentValues.put(CONST_VALUE, constValue);

        long insert = db.insert(TABLE_NAME, null, contentValues);

        db.close();

        return insert;
    }

    /**
     * Removes an item into the database.
     *
     * @param place
     * @return delete determines whether item removed successfully.
     */
    public long removePlace(final String place) throws SQLiteException
    {
        SQLiteDatabase db = _dbHelper.getWritableDatabase();

        int delete = db.delete(TABLE_NAME, PLACE + " = '" + place + "'", null);

        db.close();

        return delete;
    }
}
