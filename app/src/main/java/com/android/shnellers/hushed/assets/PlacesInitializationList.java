package com.android.shnellers.hushed.assets;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;

import com.android.shnellers.hushed.R;
import com.android.shnellers.hushed.database.PlacesDb;
import com.android.shnellers.hushed.models.PlaceModel;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.location.places.Place.*;

/**
 * Created by sean on 05/12/17.
 */

public class PlacesInitializationList
{
    private Context _mContext;

    private PlacesDb _mPlacesDb;

    public PlacesInitializationList(Context context)
    {
        _mContext = context;
        _mPlacesDb = new PlacesDb(_mContext);
    }

    private static List<PlaceModel> PLACES_LIST;

    public static Map<String, Integer> ICON_MAP;

    public static Map<String, String> KNOWN_PLACES;

    public static Map<Integer, String> TYPES_MAP;

    static
    {
        PLACES_LIST = new ArrayList<>();
        PLACES_LIST.add(new PlaceModel("school", "School", R.drawable.school, TYPE_SCHOOL));
        PLACES_LIST.add(new PlaceModel("movie_theater", "Movie Theater/Cinema", R.drawable.movie_theater, TYPE_MOVIE_THEATER));
        PLACES_LIST.add(new PlaceModel("airport", "Airport", R.drawable.airport, TYPE_AIRPORT));
        PLACES_LIST.add(new PlaceModel("art_gallery", "Art Gallery", R.drawable.art_gallery, TYPE_ART_GALLERY));
        PLACES_LIST.add(new PlaceModel("bank", "Bank", R.drawable.bank, TYPE_BANK));
        PLACES_LIST.add(new PlaceModel("bar", "Bar", R.drawable.bar, TYPE_BAR));
        PLACES_LIST.add(new PlaceModel("beauty_salon", "Beauty Salon", R.drawable.beauty_salon, TYPE_BEAUTY_SALON));
        PLACES_LIST.add(new PlaceModel("bus_station", "Bus Station", R.drawable.bus_station, TYPE_BUS_STATION));
        PLACES_LIST.add(new PlaceModel("cemetery", "Cemetery", R.drawable.cemetery, TYPE_CEMETERY));
        PLACES_LIST.add(new PlaceModel("church", "Church", R.drawable.church, TYPE_CHURCH));
        PLACES_LIST.add(new PlaceModel("courthouse", "Courthouse", R.drawable.courthouse, TYPE_COURTHOUSE));
        PLACES_LIST.add(new PlaceModel("dentist", "Dentist", R.drawable.dentist, TYPE_DENTIST));
        PLACES_LIST.add(new PlaceModel("doctor", "Doctor", R.drawable.doctor, TYPE_DOCTOR));
        PLACES_LIST.add(new PlaceModel("embassy", "Embassy", R.drawable.embassy, TYPE_EMBASSY));
        PLACES_LIST.add(new PlaceModel("fire_station", "Fire Station", R.drawable.fire_station, TYPE_FIRE_STATION));
        PLACES_LIST.add(new PlaceModel("funeral_home", "Funeral Home", R.drawable.funeral_home, TYPE_FUNERAL_HOME));
        PLACES_LIST.add(new PlaceModel("hospital", "Hospital", R.drawable.hospital, TYPE_HOSPITAL));
        PLACES_LIST.add(new PlaceModel("library", "Library", R.drawable.library, TYPE_LIBRARY));
        PLACES_LIST.add(new PlaceModel("mosque", "Mosque", R.drawable.mosque, TYPE_MOSQUE));
        PLACES_LIST.add(new PlaceModel("museum", "Museum", R.drawable.museum, TYPE_MUSEUM));
        PLACES_LIST.add(new PlaceModel("police", "Police Station", R.drawable.police, TYPE_POLICE));
        PLACES_LIST.add(new PlaceModel("synagogue", "Synagogue", R.drawable.synagogue, TYPE_SYNAGOGUE));
        PLACES_LIST.add(new PlaceModel("university", "University", R.drawable.university, TYPE_UNIVERSITY));
        PLACES_LIST.add(new PlaceModel("gym", "Gym", R.drawable.gym, TYPE_GYM));

        ICON_MAP = new HashMap<>();
        ICON_MAP.put("school", R.drawable.school);
        ICON_MAP.put("movie_theater", R.drawable.movie_theater);
        ICON_MAP.put("airport", R.drawable.airport);
        ICON_MAP.put("art_gallery", R.drawable.art_gallery);
        ICON_MAP.put("bank", R.drawable.bank);
        ICON_MAP.put("bar", R.drawable.bar);
        ICON_MAP.put("beauty_salon", R.drawable.beauty_salon);
        ICON_MAP.put("bus_station", R.drawable.bus_station);
        ICON_MAP.put("cemetery", R.drawable.cemetery);
        ICON_MAP.put("church", R.drawable.church);
        ICON_MAP.put("courthouse", R.drawable.courthouse);
        ICON_MAP.put("dentist", R.drawable.dentist);
        ICON_MAP.put("doctor", R.drawable.doctor);
        ICON_MAP.put("embassy", R.drawable.embassy);
        ICON_MAP.put("fire_station", R.drawable.fire_station);
        ICON_MAP.put("funeral_home", R.drawable.funeral_home);
        ICON_MAP.put("hospital", R.drawable.hospital);
        ICON_MAP.put("library", R.drawable.library);
        ICON_MAP.put("mosque", R.drawable.mosque);
        ICON_MAP.put("museum", R.drawable.museum);
        ICON_MAP.put("police", R.drawable.police);
        ICON_MAP.put("synagogue", R.drawable.synagogue);
        ICON_MAP.put("university", R.drawable.university);
        ICON_MAP.put("gym", R.drawable.gym);

        KNOWN_PLACES = new HashMap<>();
        KNOWN_PLACES.put("school", "school");
        KNOWN_PLACES.put("movie_theater", "movie_theater");
        KNOWN_PLACES.put("airport", "airport");
        KNOWN_PLACES.put("art_gallery", "art_gallery");
        KNOWN_PLACES.put("bank", "bank");
        KNOWN_PLACES.put("bar", "bar");
        KNOWN_PLACES.put("beauty_salon", "beauty_salon");
        KNOWN_PLACES.put("bus_station", "bus_station");
        KNOWN_PLACES.put("cemetery", "cemetery");
        KNOWN_PLACES.put("church", "church");
        KNOWN_PLACES.put("courthouse", "courthouse");
        KNOWN_PLACES.put("dentist", "dentist");
        KNOWN_PLACES.put("doctor", "doctor");
        KNOWN_PLACES.put("embassy", "embassy");
        KNOWN_PLACES.put("fire_station", "fire_station");
        KNOWN_PLACES.put("funeral_home", "funeral_home");
        KNOWN_PLACES.put("hospital", "hospital");
        KNOWN_PLACES.put("library", "library");
        KNOWN_PLACES.put("mosque", "mosque");
        KNOWN_PLACES.put("museum", "museum");
        KNOWN_PLACES.put("police", "police");
        KNOWN_PLACES.put("synagogue", "synagogue");
        KNOWN_PLACES.put("university", "university");
        KNOWN_PLACES.put("gym", "gym");

        TYPES_MAP = new HashMap<>();
        TYPES_MAP.put(TYPE_SCHOOL, "school");
        TYPES_MAP.put(TYPE_MOVIE_THEATER, "movie_theater");
        TYPES_MAP.put(TYPE_AIRPORT, "airport");
        TYPES_MAP.put(TYPE_ART_GALLERY, "art_gallery");
        TYPES_MAP.put(TYPE_BANK, "bank");
        TYPES_MAP.put(TYPE_BAR, "bar");
        TYPES_MAP.put(TYPE_BEAUTY_SALON, "beauty_salon");
        TYPES_MAP.put(TYPE_BUS_STATION, "bus_station");
        TYPES_MAP.put(TYPE_CEMETERY, "cemetery");
        TYPES_MAP.put(TYPE_CHURCH, "church");
        TYPES_MAP.put(TYPE_COURTHOUSE, "courthouse");
        TYPES_MAP.put(TYPE_DENTIST, "dentist");
        TYPES_MAP.put(TYPE_DOCTOR, "doctor");
        TYPES_MAP.put(TYPE_EMBASSY, "embassy");
        TYPES_MAP.put(TYPE_FIRE_STATION, "fire_station");
        TYPES_MAP.put(TYPE_FUNERAL_HOME, "funeral_home");
        TYPES_MAP.put(TYPE_HOSPITAL, "hospital");
        TYPES_MAP.put(TYPE_LIBRARY, "library");
        TYPES_MAP.put(TYPE_MOSQUE, "mosque");
        TYPES_MAP.put(TYPE_MUSEUM, "museum");
        TYPES_MAP.put(TYPE_POLICE, "police");
        TYPES_MAP.put(TYPE_SYNAGOGUE, "synagogue");
        TYPES_MAP.put(TYPE_UNIVERSITY, "university");
        TYPES_MAP.put(TYPE_GYM, "gym");


    }



    public void initializePlacesDatabase()
    {
        for (PlaceModel place : PLACES_LIST)
        {
            _mPlacesDb.addPlace(place.getPlace(), place.getUserFriendlyName(), ICON_MAP.get(place.getPlace()), place.getConstValue());
        }
    }

}
