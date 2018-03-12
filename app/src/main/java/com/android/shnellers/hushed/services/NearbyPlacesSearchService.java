package com.android.shnellers.hushed.services;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.shnellers.hushed.R;
import com.android.shnellers.hushed.assets.PlacesInitializationList;
import com.android.shnellers.hushed.components.DataParser;
import com.android.shnellers.hushed.components.DownloadUrl;
import com.android.shnellers.hushed.database.CurrentPlaceDB;
import com.android.shnellers.hushed.database.CustomPlacesDb;
import com.android.shnellers.hushed.database.HushedPlacesDb;
import com.android.shnellers.hushed.database.LastRingModeDb;
import com.android.shnellers.hushed.models.HushedPlace;
import com.android.shnellers.hushed.models.Mode;
import com.android.shnellers.hushed.network.UrlCreator;
import com.android.shnellers.hushed.notification.NotificationCreator;
import com.android.shnellers.hushed.permissions.CheckPermissions;
import com.android.shnellers.hushed.utils.RingerMode;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that performs a search of nearby places and checks to see if the most
 * likely place is listed in the users database. If it is then the ring mode of
 * the device is update to the users preference ring mode.
 *
 * Created by sean on 12/12/17.
 */
public class NearbyPlacesSearchService extends Service
{
    private static final String TAG = NearbyPlacesSearchService.class.getSimpleName();

    private static final int MOST_LIKELY_TYPE = 0;

    public static final String PIPE_SYMBOL = "|";

    private Handler _mHandler;

    private int _mStartMode;

    private CheckPermissions _mCheckPermissions;

    private LocationManager _mLocationManager;

    private List<String> _mHushedPlaces;

    private HushedPlacesDb _db;

    private StringBuilder _typesBuilder;

    private Map<String, HushedPlace> _mHushedPlacesMap;

    private Context _context;

    private PlaceDetectionClient _mPlaceDetecClient;

    private Place _mostLikelyPlace;

    private CurrentPlaceDB _currentDb;

    private CustomPlacesDb _customPlacesDb;

    private AudioManager _audioManager;

    private LastRingModeDb _lastRingModeDd;

    private List _hushedPlaces;
    private Map<String, HushedPlace> _placesMap;

    private HushedPlacesDb _mHushedPlacesDb;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public NearbyPlacesSearchService()
    {

    }

    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate: ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.i(TAG, "onBind: ");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.i(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand: ");
        _mCheckPermissions = new CheckPermissions(this);
        _mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new HushedLocationListener();
        _mHushedPlaces = new ArrayList<>();
        _db = new HushedPlacesDb(this);
        _mHushedPlacesMap = _db.getMapOfHushedPlaces();
        _context = getApplicationContext();
        _mHushedPlaces = _db.getPlacesTypes();
        _mPlaceDetecClient = Places.getPlaceDetectionClient(_context, null);
        _mostLikelyPlace = null;
        _currentDb = new CurrentPlaceDB(_context);
        _audioManager = (AudioManager) _context.getSystemService(Context.AUDIO_SERVICE);
        _lastRingModeDd = new LastRingModeDb(_context);
        _customPlacesDb = new CustomPlacesDb(_context);

        initializeHushedString(_mHushedPlaces);

        if (Build.VERSION.SDK_INT >= 23)
        {

                //_mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                getMostLikelyPlace();

        }


        return _mStartMode;
    }

    private void getMostLikelyPlace()
    {
        if (_mCheckPermissions.checkPermission())
        {
            final Task<PlaceLikelihoodBufferResponse> placeResult = _mPlaceDetecClient.getCurrentPlace(null);

            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>()
            {
                boolean activeHushedLocation = false;

                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task)
                {
                    List<Place> placesList = new ArrayList<>();
                    PlaceLikelihoodBufferResponse  likelyPlaces = task.getResult();

                    for (PlaceLikelihood placeLikelihood : likelyPlaces)
                    {
                        placesList.add(placeLikelihood.getPlace());
                    }
                    Place mostLikely = placesList.get(0);
                    int mostLikelyType = mostLikely.getPlaceTypes().get(0);
                    String typeString = PlacesInitializationList.TYPES_MAP.get(mostLikelyType);
                    String placeName = mostLikely.getName().toString();
                    String address = mostLikely.getAddress().toString();

//                    if (typeString != null && typeString.length() > 0)
//                    {
                        activeHushedLocation = searchPlacesTypesForHushedType(typeString, placeName, address);
//                    }

                    if (!activeHushedLocation && _currentDb.getCurrentPlace() != null)
                    {
                        _currentDb.reset();
                        restoreLastRingMode();
                    }

                    System.out.println("NAME: " + mostLikely.getAddress().toString());
                        likelyPlaces.release();


                    }
            });
        }

    }

    /**
     * Restores the ring mode of the device stored when a hushed place was
     * detected.
     */
    private void restoreLastRingMode()
    {
        if (audioManagerIsNotNull())
        {
            int lastRingMode = _lastRingModeDd.getLastRingMode();
            _audioManager.setRingerMode(lastRingMode);
        }
    }

    private boolean searchPlacesTypesForHushedType(String type, String placeName, String address)
    {
        boolean activeHushedLocation = false;
        int existsAddress = _customPlacesDb.existsAddress(address);

        System.out.println("SEARCHING " + existsAddress);
        if (_hushedPlaces != null && !_hushedPlaces.isEmpty() && (_hushedPlaces.contains(type) || existsAddress == 1))
        {
            System.out.println("Hushing Placeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            HushedPlace place = _placesMap.get(type);
            if (place != null)
            {
                activeHushedLocation = true;
                String placeType = place.getType();
                String userFriendlyName = place.getUserFriendlyType();
                String userFriendlyRingMode = place.getRingMode();

                if (!isPlaceAlreadyInDb(placeType, userFriendlyName))
                {
                    String id = "detected.place.id";
                    storeCurrentPlace(placeType, userFriendlyName);
                    storeLastRingMode();
                    RingerMode.setRingerMode(_context, userFriendlyRingMode);
                    NotificationCreator.createNotification(_context, id, place.getUserFriendlyType(), place.getRingMode());
                }
                else if (isPlaceAlreadyInDb(placeType, userFriendlyName))
                {
                    if (_audioManager != null)
                    {
                        int ringerMode = _audioManager.getRingerMode();
                        Mode mode = RingerMode.RINGER_MODE.get(ringerMode);
                        if (!userFriendlyRingMode.equals(mode.getUserFriendlyName()))
                        {
                            RingerMode.setRingerMode(_context, userFriendlyRingMode);
                        }
                    }
                }
            }

        }


        return activeHushedLocation;
    }

            /**
         * Takes the current ring mode of the device and stores it in the database. This will
         * then be restored when the user leaves a hushed place.
         */
        private void storeLastRingMode()
        {
            if (audioManagerIsNotNull())
            {
                int ringMode = _audioManager.getRingerMode();
                Mode mode = RingerMode.RINGER_MODE.get(ringMode);
                _lastRingModeDd.insert(ringMode, mode.getUserFriendlyName());
            }
        }



        private void storeCurrentPlace(String type, String userFriendlyName)
        {
            long result = _currentDb.insert(type, userFriendlyName);

            // TODO: Inform user of result
            if (result < 0)
            {
                System.out.println("FAILED");
            }
            else
            {
                System.out.println("INSERTED");
            }
        }

        private boolean isPlaceAlreadyInDb(final String type, final String userFriendlyName)
        {

            return _currentDb.existsInDb(type, userFriendlyName);
        }

        private boolean audioManagerIsNotNull()
        {
            return _audioManager != null;
        }

    private void initializeHushedString(List<String> hushedPlaces)
    {
        if (hushedPlaces != null && !hushedPlaces.isEmpty())
        {
            _typesBuilder= new StringBuilder();
            for (int typeIndex = 0; typeIndex < hushedPlaces.size(); typeIndex++)
            {
                _typesBuilder.append(hushedPlaces.get(typeIndex));

                if (typeIndex < (hushedPlaces.size() - 1))
                {
                    _typesBuilder.append(PIPE_SYMBOL);
                }
            }
        }
    }


    private class HushedLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            final int URL_INDEX = 0;
            UrlCreator urlCreator = new UrlCreator(getApplicationContext());

            if (_typesBuilder != null)
            {
                String url = null;
                try
                {
                    url = urlCreator.createUrl(latitude, longitude, _typesBuilder.toString());
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                Object[] dataTransfer = new Object[1];
                dataTransfer[URL_INDEX] = url;

                if (_mHushedPlaces != null && !_mHushedPlaces.isEmpty())
                {
                 //   GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(getApplicationContext(), _mHushedPlaces, _mHushedPlacesMap);
                 //   getNearbyPlacesData.execute(dataTransfer);
                }
            }

            _mLocationManager.removeUpdates(this);
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

//    private static class GetNearbyPlacesData extends AsyncTask<Object, String, String>
//    {
//        private String _googlePlacesData;
//        private String _url;
//        private List _hushedPlaces;
//        private Map<String, HushedPlace> _placesMap;
//
//        private HushedPlacesDb _mHushedPlacesDb;
//        private Context _context;
//
//        private LastRingModeDb _lastRingModeDd;
//        private AudioManager _audioManager;
//
//        public GetNearbyPlacesData(Context applicationContext, List<String> mHushedPlaces, Map<String, HushedPlace> hushedPlacesMap)
//        {
//            _context = applicationContext;
//
//            _hushedPlaces = mHushedPlaces;
//            _placesMap = hushedPlacesMap;
//            _lastRingModeDd = new LastRingModeDb(_context);
//            _audioManager = (AudioManager) _context.getSystemService(Context.AUDIO_SERVICE);
//        }
//
//        @Override
//        protected String doInBackground(Object... params)
//        {
//            try {
//
//                _url = (String) params[0];
//                Log.d("GetNearbyPlacesData", "URL entered: " + _url);
//                DownloadUrl downloadUrl = new DownloadUrl();
//                _googlePlacesData = downloadUrl.readUrl(_url);
//            }
//            catch (Exception e)
//            {
//                Log.d("GooglePlacesReadTask", e.toString());
//            }
//            return _googlePlacesData;
//        }
//
//        @Override
//        protected void onPostExecute(String result)
//        {
//            Log.d("GooglePlacesReadTask", "onPostExecute Entered");
//
//            DataParser dataParser = new DataParser();
//            List<HashMap<String, String>> nearbyPlacesList =  dataParser.parse(result);
//            ShowNearbyPlaces(nearbyPlacesList);
//
//        }
//
//
//        @TargetApi(Build.VERSION_CODES.O)
//        private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList)
//        {
//            boolean activeHushedLocation = false;
//            System.out.println(nearbyPlacesList.toString());
//            for (int i = 0; i < nearbyPlacesList.size(); i++)
//            {
//                MarkerOptions markerOptions = new MarkerOptions();
//                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
//
//                if (googlePlace != null)
//                {
//                    double lat = Double.parseDouble(googlePlace.get(_context.getString(R.string.latitude)));
//                    double lng = Double.parseDouble(googlePlace.get(_context.getString(R.string.longitude)));
//                    String placeName = googlePlace.get(_context.getString(R.string.place_name));
//                    String vicinity = googlePlace.get(_context.getString(R.string.vicinity));
//                    String typesString = googlePlace.get(_context.getString(R.string.types));
//                    String[] types = null;
//
//                    if (typesString != null && typesString.length() > 0)
//                    {
//                        types = typesString.split(_context.getString(R.string.hash_symbol));
//                        String mostLikelyType = types[MOST_LIKELY_TYPE];
//                        activeHushedLocation = searchPlacesTypesForHushedType(mostLikelyType, placeName);
//                    }
//
//                    if (!activeHushedLocation && _currentDb.getCurrentPlace() != null)
//                    {
//                        _currentDb.reset();
//                        //restoreLastRingMode();
//                    }
//
//                    LatLng latLng = new LatLng(lat, lng);
//                    markerOptions.position(latLng);
//                    markerOptions.title(placeName + " : " + vicinity);
//                    System.out.println("Name: " + placeName + " | Type: " + Arrays.toString(types));
//
//
//                    //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                }
//
//            }
//        }
//
//        private boolean searchPlacesTypesForHushedType(String type, String placeName)
//        {
//            boolean activeHushedLocation = false;
//
//            if (_hushedPlaces != null && !_hushedPlaces.isEmpty() && _hushedPlaces.contains(type))
//            {
//                HushedPlace place = _placesMap.get(type);
//                if (place != null)
//                {
//                    activeHushedLocation = true;
//                    String placeType = place.getType();
//                    String userFriendlyName = place.getUserFriendlyType();
//                    String userFriendlyRingMode = place.getRingMode();
//
//                    if (!isPlaceAlreadyInDb(placeType, userFriendlyName))
//                    {
//                        String id = "detected.place.id";
//                        storeCurrentPlace(placeType, userFriendlyName);
//                        storeLastRingMode();
//                        RingerMode.setRingerMode(_context, userFriendlyRingMode);
//                        NotificationCreator.createNotification(_context, id, place.getUserFriendlyType(), place.getRingMode());
//                    }
//                    else if (isPlaceAlreadyInDb(placeType, userFriendlyName))
//                    {
//                        if (_audioManager != null)
//                        {
//                            int ringerMode = _audioManager.getRingerMode();
//                            Mode mode = RingerMode.RINGER_MODE.get(ringerMode);
//                            if (!userFriendlyRingMode.equals(mode.getUserFriendlyName()))
//                            {
//                                RingerMode.setRingerMode(_context, userFriendlyRingMode);
//                            }
//                        }
//                    }
//                }
//
//            }
//
//
//            return activeHushedLocation;
//        }
//
//        /**
//         * Takes the current ring mode of the device and stores it in the database. This will
//         * then be restored when the user leaves a hushed place.
//         */
//        private void storeLastRingMode()
//        {
//            if (audioManagerIsNotNull())
//            {
//                int ringMode = _audioManager.getRingerMode();
//                Mode mode = RingerMode.RINGER_MODE.get(ringMode);
//                _lastRingModeDd.insert(ringMode, mode.getUserFriendlyName());
//            }
//        }
//
//
//
//        private void storeCurrentPlace(String type, String userFriendlyName)
//        {
//            long result = _currentDb.insert(type, userFriendlyName);
//
//            // TODO: Inform user of result
//            if (result < 0)
//            {
//                System.out.println("FAILED");
//            }
//            else
//            {
//                System.out.println("INSERTED");
//            }
//        }
//
//        private boolean isPlaceAlreadyInDb(final String type, final String userFriendlyName)
//        {
//
//            return _currentDb.existsInDb(type, userFriendlyName);
//        }
//
//        private boolean audioManagerIsNotNull()
//        {
//            return _audioManager != null;
//        }
//    }
}
