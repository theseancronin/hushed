package com.android.shnellers.hushed.components;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sean on 06/11/17.
 */

public class DataParser
{
    private static final String TAG = DataParser.class.getSimpleName();

    private static final int MOST_LIKELY_PLACE = 0;

    public List<HashMap<String, String>> parse(String jsonData)
    {
        List<HashMap<String, String>> locationContext = new ArrayList<>();
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        if (jsonData != null)
        {
            try
            {
                Log.d(TAG, "parse: " + jsonData);
                jsonObject = new JSONObject(jsonData);
                jsonArray = jsonObject.getJSONArray("results");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        if (jsonArray != null && jsonArray.length() > 0)
        {
            locationContext = getPlaces(jsonArray);
        }

        return locationContext;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray)
    {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        try
        {
            placeMap = getPlace((JSONObject) jsonArray.get(MOST_LIKELY_PLACE));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        placesList.add(placeMap);
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        StringBuilder types = new StringBuilder();

        try
        {
            if (!googlePlaceJson.isNull("name"))
            {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity"))
            {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            if (!googlePlaceJson.isNull("types"))
            {
                JSONArray array = (JSONArray) googlePlaceJson.getJSONArray("types");
                if (array.length() > 0)
                {
                    for (int t = 0; t < array.length(); t++)
                    {
                        types.append(array.getString(t));
                        if (t < array.length())
                        {
                            types.append("#");
                        }
                    }
                }
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
            googlePlaceMap.put("types", types.toString());
        }
        catch (JSONException e)
        {
            Log.d("getPlace", "Error");
            e.printStackTrace();
        }

        return googlePlaceMap;
    }
}
