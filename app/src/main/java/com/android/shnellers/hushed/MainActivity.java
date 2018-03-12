package com.android.shnellers.hushed;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.android.shnellers.hushed.assets.PlacesInitializationList;
import com.android.shnellers.hushed.constants.Constants;
import com.android.shnellers.hushed.database.PlacesDb;
import com.android.shnellers.hushed.io.ObjectSerializer;
import com.android.shnellers.hushed.permissions.CheckPermissions;
import com.android.shnellers.hushed.services.GeoFenceTransitionIntentService;
import com.android.shnellers.hushed.services.NearbyPlacesSearchService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.android.shnellers.hushed.constants.Constants.GEO_FENCE_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION_REQUEST_CODE = 4649;

    private static final String HOME_ADDRESS = "31 Grange Way";

    private CheckPermissions _mCheckPermissions;

    private GoogleApiClient _mGoogleApiClient;

    private GeofencingClient _mGeofencingClient;

    private ArrayList<Geofence> _mGeofenceList;

    private FusedLocationProviderClient _mFusedLocationClient;

    @BindView(R.id.add_hushed_place)
    protected FloatingActionButton _mAddHushedPlace;

    @BindView(R.id.hidden)
    protected RelativeLayout _hidden;

    @BindView(R.id.bottom_panel)
    protected RelativeLayout _bottomPanel;

    private NotificationManager _mNotificationManager;

    private LocationRequest mLocationRequest;

    private double _mLatitude;
    private double _mLongitude;

    private boolean _isPanelShown;

    private RelativeLayout.LayoutParams _paramsBottomPanel;

    private PendingIntent _mNearbySearchPendingIntent;

    private PendingIntent _mGeofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        _mCheckPermissions = new CheckPermissions(this);
        _hidden.setVisibility(View.INVISIBLE);
        _mNearbySearchPendingIntent = null;

        final float scale = this.getResources().getDisplayMetrics().density;
        int pixels = (int) (100 * scale + 0.5f);

        _paramsBottomPanel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, pixels);
        _paramsBottomPanel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        _paramsBottomPanel.addRule(RelativeLayout.ABOVE, R.id.hidden);
        _bottomPanel.setLayoutParams(_paramsBottomPanel);
        _isPanelShown = false;

        _mGeofenceList = new ArrayList<>();
        _mGeofencingClient = LocationServices.getGeofencingClient(this);
        _mGeofencePendingIntent = null;

        setupGoogleApiClient();

        _mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        _mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        startNearbySearchService();
        startGeoFencingService();

    }


    /**
     * The alarm manager is used to wake the application every minute and monitor what type
     * of place the user is in.
     */
    private void startNearbySearchService()
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long interval = 1000 * 60;

        Intent intent = new Intent(this, NearbyPlacesSearchService.class);
        intent.putExtra("longitude", _mLongitude);
        intent.putExtra("latitude", _mLatitude);

        PendingIntent pendingIntent = PendingIntent.getService(this, Constants.NEARBY_SEARCH_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null)
        {
            System.out.println("Starting Service");
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
        }
    }

    private void startGeoFencingService()
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long interval = 1000 * 60;

        Intent intent = new Intent(this, GeoFenceTransitionIntentService.class);
        intent.putExtra("longitude", _mLongitude);
        intent.putExtra("latitude", _mLatitude);

        PendingIntent pendingIntent = PendingIntent.getService(this, GEO_FENCE_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null)
        {
            System.out.println("Starting GEO_FENCING");
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
        }
    }

    /**
     * On start up of the application we must check if the user has granted
     * the application access to the devices GPS. GPS is required for the
     * application to work so GPS must be requested if it has been previously
     * denied.
     */
    @Override
    protected void onStart()
    {
        super.onStart();

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (!_mCheckPermissions.checkPermission() || !_mNotificationManager.isNotificationPolicyAccessGranted())
            {
                if (!_mCheckPermissions.checkPermission())
                {
                    requestPermissions();
                }

                if (!_mNotificationManager.isNotificationPolicyAccessGranted())
                {
                    requestAccessToDoNotDisturbSettings();
                }

            }
        }
        if (!_mGoogleApiClient.isConnected())
        {
            _mGoogleApiClient.connect();
        }
    }


    @OnClick(R.id.add_hushed_place)
    protected void slideUpAndDown()
    {
        Animation animation;

        if (!_isPanelShown)
        {
            animation = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            _hidden.startAnimation(animation);
            _hidden.setVisibility(View.VISIBLE);
            _isPanelShown = true;
            setBottomPanelAboveHiddenPanel();
        }
        else
        {
            _hidden.setVisibility(View.INVISIBLE);
            animation = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
            _hidden.startAnimation(animation);
            _isPanelShown = false;
            setBottomPanelOnParentBottom();
        }
    }

    /**
     *
     */
    private void setBottomPanelAboveHiddenPanel()
    {
        _paramsBottomPanel.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        _bottomPanel.setLayoutParams(_paramsBottomPanel);
        _mAddHushedPlace.setImageResource(R.drawable.ic_expand_more_white);
    }

    /**
     *
     */
    private void setBottomPanelOnParentBottom()
    {
        _paramsBottomPanel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        _bottomPanel.setLayoutParams(_paramsBottomPanel);
        _mAddHushedPlace.setImageResource(R.drawable.ic_expand_less_white);
    }

    /**
     * Inflates an alert dialog that requests the user to allow the application
     * access to the devices do not disturb settings. The Do Not Disturb Settings
     * allow the application to automatically set the devices ringer mode to
     * silent, vibrate or to ring.
     */
    private void requestAccessToDoNotDisturbSettings()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            AlertDialog.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            }
            else
            {
                builder = new AlertDialog.Builder(this);
            }

            builder.setTitle(R.string.ringer_mode_dialog_messge)
                    .setMessage(R.string.dialog_permission_do_not_disturb)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    })

                    .show();
        }

    }

    @Override
    protected void onStop()
    {
        if (_mGoogleApiClient != null && _mGoogleApiClient.isConnected())
        {
            _mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    /**
     * Setup the Google API Client.
     */
    private void setupGoogleApiClient()
    {
        _mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        _mGoogleApiClient.connect();
    }

    private void showCurrentPlace()
    {
        if (_mCheckPermissions.checkPermission())
        {

            @SuppressWarnings("MissingPermission") final PendingResult<PlaceLikelihoodBuffer> placeResult = Places.PlaceDetectionApi.getCurrentPlace(_mGoogleApiClient, null);

            placeResult.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>()
            {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods)
                {
                    Log.i(TAG, "onResult: ");
                    PlaceLikelihood placeLikelihood = placeLikelihoods.get(0);

                    for (PlaceLikelihood place : placeLikelihoods)
                    {
                        Log.i(TAG, "PlaceModel Likely: " + place.getPlace().getName());
                    }

                    String content = "";

                    if (placeLikelihood.getPlace() != null && !TextUtils.isEmpty(placeLikelihood.getPlace().getName()))
                    {
                        if (placeLikelihood.getPlace().getName().toString().equals(HOME_ADDRESS))
                        {
                            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


                            if (audioManager != null && audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
                            {
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            }
                        }
                        content = "Most likely place: " + placeLikelihood.getPlace().getName() + "\n";
                    }
                    else
                    {
                        content += "Percent change of being there: " + (int) (placeLikelihood.getLikelihood() * 100) + "%";
                    }


                }
            });

            Log.i(TAG, "PAST");
        }
        else
        {
            requestPermissions();
        }
    }


    /**
     * Issue the user with a request to allow the application permission
     * to use the devices GPS.
     */
    private void requestPermissions()
    {

        Log.i(TAG, "requestPermissions: ");
        boolean hasLocationAccess = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        );

        boolean hasNotificationAccess = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
        );

        if (hasLocationAccess && hasNotificationAccess)
        {


            Log.i(TAG, "Displaying permission rationale");
            showSnackBar(
                    R.string.permission_denied_explanation,
                    R.string.ok,
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // Request permission to use the devices GPS
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NOTIFICATION_POLICY},
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
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                    REQUEST_PERMISSION_REQUEST_CODE
            );
        }


    }

    /**
     * The result of the users choice from the permission request dialog is checked
     * to see if access has been granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i(TAG, "onRequestPermissionsResult");

        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length <= 0)
            {

            }
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
            else
            {
                // Permission Denied
                showSnackBar(
                        R.string.permission_denied_explanation,
                        R.string.settings,
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                // Build intent that displays the app settings screen
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                );

                                Uri uri = Uri.fromParts(
                                        "package",
                                        android.support.design.BuildConfig.APPLICATION_ID,
                                        null
                                );

                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                            }
                        }
                );

            }
        }
    }

    /**
     *
     *
     * @param mainTextStringId
     * @param actionStringId
     * @param listener
     */
    private void showSnackBar(final int mainTextStringId, final int actionStringId, final View.OnClickListener listener)
    {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener)
                .show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }


}
