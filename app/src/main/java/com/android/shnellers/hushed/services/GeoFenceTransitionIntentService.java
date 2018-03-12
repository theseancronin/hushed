package com.android.shnellers.hushed.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sean on 12/03/18.
 */

public class GeoFenceTransitionIntentService extends IntentService
{
    private static final String TAG = "GeoFenceTransitionIntentService";

    GeoFenceTransitionIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent != null && geofencingEvent.hasError())
        {
            String errorMessage = String.valueOf(geofencingEvent.getErrorCode());
            return;
        }

        int geoFenceTransition = geofencingEvent != null ? geofencingEvent.getGeofenceTransition() : 0;

        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            List<Geofence> triggeringGeoFences = geofencingEvent.getTriggeringGeofences();

            String geoFenceTransitionDetails = getGeoFenceTransitionDetails(
                    this,
                    geoFenceTransition,
                    triggeringGeoFences
            );
        }
    }

    private String getGeoFenceTransitionDetails(Context context, int geoFenceTransition, List<Geofence> triggeringGeoFences)
    {
        String getGeoFencingTransitionDetails = getTransitionsString(geoFenceTransition);

        ArrayList<String> triggeringGeoFencesIdList = new ArrayList<>();

        boolean workEntered = false;

        for (Geofence geofence : triggeringGeoFences) {
            triggeringGeoFencesIdList.add(geofence.getRequestId());

//            if (geofence.getRequestId().equals(Constants.PILZ_IRELAND)) {
//                workEntered = true;
//            }
        }

        String triggeringGeoFenceIdString = TextUtils.join(", ", triggeringGeoFencesIdList);

        if (workEntered) {
            return "PILZ: Don't forget to clock-in";
        } else  {
            return getGeoFencingTransitionDetails + ": " + triggeringGeoFenceIdString;
        }
    }

    private String getTransitionsString(int geoFenceTransition)
    {
        switch (geoFenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "GeoFence transition entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "GeoFence transition exited";
            default:
                return "Unknown GeoFence transition";
        }
    }
}
