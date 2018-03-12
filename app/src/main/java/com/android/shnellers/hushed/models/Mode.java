package com.android.shnellers.hushed.models;

/**
 * Created by sean on 12/02/18.
 */

public class Mode
{
    private int _ringMode;
    private String _userFriendlyName;

    public Mode(final int ringMode, final String userFriendlyName)
    {
        _ringMode = ringMode;
        _userFriendlyName = userFriendlyName;
    }

    public int getRingMode()
    {
        return _ringMode;
    }

    public String getUserFriendlyName()
    {
        return _userFriendlyName;
    }
}
