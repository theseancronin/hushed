package com.android.shnellers.hushed.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.android.shnellers.hushed.components.DataParser;
import com.android.shnellers.hushed.components.DownloadUrl;
import com.android.shnellers.hushed.database.HushedPlacesDb;
import com.android.shnellers.hushed.notification.NotificationCreator;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sean on 31/10/17.
 */


