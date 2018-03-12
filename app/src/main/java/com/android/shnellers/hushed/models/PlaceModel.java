package com.android.shnellers.hushed.models;

/**
 * A simple place class that holds a Google PlaceModel type and it's
 * user friendly name.
 *
 * Created by sean on 10/01/18.
 */
public class PlaceModel
{
    private String _place;
    private String _userFriendlyName;
    private int _icon;
    private int _constValue;

    /**
     * A simple constructor to initialize a place object.
     * @param place
     * @param userFriendlyName
     * @param icon
     * @param constValue
     */
    public PlaceModel(String place, String userFriendlyName, int icon, int constValue)
    {
        _place = place;
        _userFriendlyName = userFriendlyName;
        _icon = icon;
        _constValue = constValue;
    }

    /**
     * Get the place name.
     *
     * @return
     */
    public String getPlace()
    {
        return _place;
    }

    /**
     * Get the user friendly name of the place.
     *
     * @return
     */
    public String getUserFriendlyName()
    {
        return _userFriendlyName;
    }

    public int getIcon()
    {
        return _icon;
    }

    public int getConstValue()
    {
        return _constValue;
    }
}
