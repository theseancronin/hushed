package com.android.shnellers.hushed.models;

/**
 * Created by sean on 21/02/18.
 */

public class PlaceSuperClass
{
    private String _type;

    private int _id;

    private int _constValue;

    public PlaceSuperClass(){}

    public PlaceSuperClass(int id, String type, int constValue)
    {
        _type = type;
        _id = id;
        _constValue = constValue;
    }

    public int getId()
    {
        return _id;
    }

    public String getType()
    {
        return _type;
    }

    public int getConstValue()
    {
        return _constValue;
    }
}
