package com.android.shnellers.hushed;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.shnellers.hushed.constants.Constants;
import com.android.shnellers.hushed.io.ObjectSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sean on 04/01/18.
 */

public class PlaceTypesPreferences
{
    private static final String TAG = PlaceTypesPreferences.class.getSimpleName();

    public static void initPlacesTypes(FragmentActivity activity)
    {
        Log.d(TAG, "initPlacesTypes: ");
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        List<String> l = new ArrayList<>();
        l.add("School");
        l.add("Movie Theater/Cinema");
        l.add("Supermarket");

        SharedPreferences.Editor editor = preferences.edit();

        try
        {
            editor.putString(Constants.PLACES_TYPES_LIST, ObjectSerializer.serialize((Serializable) l));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        editor.apply();

    }
    @SuppressWarnings("unchecked")
    public static void removePlaceFromGlobalList (FragmentActivity activity, String place)
    {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        preferences.getString(Constants.PLACES_TYPES_LIST, null);

        try
        {
            Object object = ObjectSerializer.deserialize(
                    preferences.getString(Constants.PLACES_TYPES_LIST, ObjectSerializer.serialize(new ArrayList<String>())));

            if (object instanceof List)
            {
                List<String> list = (List<String>) object;
                if (list.contains(place))
                {
                    list.remove(place);
                    SharedPreferences.Editor editor = preferences.edit();
                    try
                    {
                        editor.putString(Constants.PLACES_TYPES_LIST, ObjectSerializer.serialize((Serializable) list));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    editor.apply();
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
