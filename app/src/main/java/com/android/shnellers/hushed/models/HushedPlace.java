package com.android.shnellers.hushed.models;

/**
 * Created by sean on 11/12/17.
 */

public class HushedPlace extends PlaceSuperClass
{
    private String _ringMode;
    private String _userFriendlyType;

    public HushedPlace(final int id, final String type, final String ringMode, final String userFriendlyType, int constValue)
    {
        super(id, type, constValue);
        _ringMode = ringMode;
        _userFriendlyType = userFriendlyType;
    }

    public String getUserFriendlyType()
    {
        return _userFriendlyType;
    }

    public String getRingMode()
    {
        return _ringMode;
    }
}
