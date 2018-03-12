package com.android.shnellers.hushed.network;


import android.content.Context;
import android.content.res.Resources;

import com.android.shnellers.hushed.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sean on 27/11/17.
 */

public class UrlCreator
{
    private static final String TAG = UrlCreator.class.getSimpleName();

    private static final boolean TESTING = true;

    private static final String SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";

    private static final String OUTPUT = "json?";

    private static final String LOCATION = "location=";

    private static final String RADIUS = "&radius=";

    private static final String KEY = "&key=";

    private static final String TYPE = "&type=";

    private static final int RADIUS_METRES = 15;

    private Context mContext;

    public UrlCreator(Context context)
    {
        mContext = context;
    }

    public String createUrl(double latitude, double longitude, String type) throws UnsupportedEncodingException
    {
        Resources resources = mContext.getResources();
        String apiKey = resources.getString(R.string.google_places_api);

        return SEARCH_URL + OUTPUT +
                LOCATION + latitude + "," + longitude +
                RADIUS + RADIUS_METRES +
                KEY + apiKey;
    }

}
