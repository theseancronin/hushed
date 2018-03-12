package com.android.shnellers.hushed.io;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by sean on 05/12/17.
 */

public class PlacesReader
{
    private Context _mContext;

    public PlacesReader(Context context)
    {
        _mContext = context;
    }

    public Properties getPlacesProperties()
    {
        Properties placesProperties = new Properties();

        try
        {
            AssetManager assetManager = _mContext.getAssets();
            InputStream reader = assetManager.open("places_types.properties");
            placesProperties.load(reader);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return placesProperties;
    }
}
