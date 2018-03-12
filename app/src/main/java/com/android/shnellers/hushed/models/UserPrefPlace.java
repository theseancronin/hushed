package com.android.shnellers.hushed.models;

/**
 * Created by sean on 24/02/18.
 */

public class UserPrefPlace extends PlaceSuperClass
{
    private String _placeId;
    private String _address;
    private int _typeNumber;
    private double _longitude;
    private double _latitude;

    public UserPrefPlace(String placeId, String place, int id, int typeNumber, String address, double longitude, double latitude)
    {
        super(id, place, typeNumber);
        _placeId = placeId;
        _typeNumber = typeNumber;
        _address = address;
        _longitude = longitude;
        _latitude = latitude;
    }

    public String getPlaceId()
    {
        return _placeId;
    }

    public int getTypeNumber()
    {
        return _typeNumber;
    }

    public String getAddress()
    {
        return _address;
    }

    public double getLongitude()
    {
        return _longitude;
    }

    public double getLatitude()
    {
        return _latitude;
    }
}
