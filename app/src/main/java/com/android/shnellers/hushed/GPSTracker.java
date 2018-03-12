package com.android.shnellers.hushed;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.android.shnellers.hushed.components.SnackBarComponent;

/**
 * Created by sean on 12/10/17.
 */

public class GPSTracker extends Service implements LocationListener
{
    private static final String TAG = GPSTracker.class.getSimpleName();

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private static final int REQUEST_PERMISSION_REQUEST_CODE = 5674;

    private Context mContext;

    private LocationManager mLocationManager;

    private Location mLocation;

    private double mLatitude;
    private double mLongitude;

    private SnackBarComponent mSnackBar;

    private View mView;

    private Activity mActivity;

    public GPSTracker (Context context, View view, Activity activity)
    {
        mContext = context;
        getLocation();
        System.out.println("Gps tracker");

        mView = view;

        mActivity = activity;

        mSnackBar = new SnackBarComponent(mView, mContext);
    }

    public Location getLocation()
    {

        try
        {


            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            boolean isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            System.out.println("GPS: " + checkPermission());
            System.out.println("GEtting Locations");

            if (checkPermission())
            {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                );

                if (mLocationManager != null)
                {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (mLocation != null)
                    {
                        mLatitude = mLocation.getLatitude();
                        mLongitude = mLocation.getLongitude();
                    }
                }
            }

        }
        catch (Exception e)
        {

        }

        return mLocation;
    }

    /**
     * Check if the user has granted access to use the devices GPS.
     *
     * @return
     */
    private boolean checkPermission()
    {

        // Get the current permission state.
        int hasLocationAccess = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        );



        return hasLocationAccess == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Function to get latitude
     * */
    public double getLatitude()
    {
        if(mLocation != null)
        {
            mLatitude = mLocation.getLatitude();
        }

        // return latitude
        return mLatitude;
    }


    /**
     * Function to get longitude
     * */
    public double getLongitude()
    {
        if(mLocation != null)
        {
            mLongitude = mLocation.getLongitude();
        }

        // return longitude
        return mLongitude;
    }

    /**
     * Issue the user with a request to allow the application permission
     * to use the devices GPS.
     */
    private void requestPermissions()
    {

        Log.i(TAG, "requestPermissions: ");
        boolean hasLocationAccess = ActivityCompat.shouldShowRequestPermissionRationale(
                mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
        );

        boolean hasNotificationAccess = ActivityCompat.shouldShowRequestPermissionRationale(
                mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
        );

        if (hasLocationAccess && hasNotificationAccess)
        {


            Log.i(TAG, "Displaying permission rationale");
            mSnackBar.showSnackBar(
                    R.string.permission_denied_explanation,
                    R.string.ok,
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // Request permission to use the devices GPS
                            ActivityCompat.requestPermissions(
                                    mActivity,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                                    REQUEST_PERMISSION_REQUEST_CODE
                            );
                        }
                    }
            );
        }
        else
        {
            Log.i(TAG, "requestPermissions: ");

            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                    REQUEST_PERMISSION_REQUEST_CODE
            );
        }


    }

    /**
     * The result of the users choice from the permission request dialog is checked
     * to see if access has been granted.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//
//        Log.i(TAG, "onRequestPermissionsResult");
//
//        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE)
//        {
//            if (grantResults.length <= 0)
//            {
//
//            }
//            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            {
//
//            }
//            else
//            {
//                // Permission Denied
//                mSnackBar.showSnackBar(
//                        R.string.permission_denied_explanation,
//                        R.string.settings,
//                        new View.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(View v)
//                            {
//                                // Build intent that displays the app settings screen
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                                );
//
//                                Uri uri = Uri.fromParts(
//                                        "package",
//                                        android.support.design.BuildConfig.APPLICATION_ID,
//                                        null
//                                );
//
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                startActivity(intent);
//                            }
//                        }
//                );
//
//            }
//        }
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onLocationChanged(Location location)
    {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }
}
