package com.android.shnellers.hushed.permissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.android.shnellers.hushed.MainActivity;
import com.android.shnellers.hushed.R;

/**
 * Created by sean on 03/12/17.
 */

public class CheckPermissions
{
    private static final String TAG = CheckPermissions.class.getSimpleName();

    private Context _mContext;

    public CheckPermissions(Context context)
    {
        _mContext = context;
    }

    /**
     * Check if the user has granted access to use the devices GPS.
     *
     * @return
     */
    public boolean checkPermission()
    {

        // Get the current permission state.
        int hasLocationAccess = ActivityCompat.checkSelfPermission(
                _mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
        );

        int hasNotificationAccess = ActivityCompat.checkSelfPermission(
                _mContext,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
        );

        return hasLocationAccess == PackageManager.PERMISSION_GRANTED && hasNotificationAccess == PackageManager.PERMISSION_GRANTED;
    }

}
