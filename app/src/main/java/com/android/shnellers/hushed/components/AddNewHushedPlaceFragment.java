package com.android.shnellers.hushed.components;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shnellers.hushed.R;
import com.android.shnellers.hushed.assets.PlacesInitializationList;
import com.android.shnellers.hushed.constants.Constants;
import com.android.shnellers.hushed.database.CustomPlacesDb;
import com.android.shnellers.hushed.database.PlacesDb;
import com.android.shnellers.hushed.database.contracts.HushedPlacesContract;
import com.android.shnellers.hushed.database.HushedPlacesDb;
import com.android.shnellers.hushed.database.helpers.HushedPlacesDbHelper;
import com.android.shnellers.hushed.models.PlaceModel;
import com.android.shnellers.hushed.models.UserPrefPlace;
import com.android.shnellers.hushed.receivers.HushedItemBroadcastReceiver;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;
import static com.android.shnellers.hushed.database.contracts.HushedPlacesContract.Entry.PLACE_TYPE;
import static com.android.shnellers.hushed.database.contracts.HushedPlacesContract.Entry.RING_MODE;
import static com.android.shnellers.hushed.database.contracts.HushedPlacesContract.Entry.USER_FRIENDLY_NAME;

/**
 * Created by sean on 03/12/17.
 */

public class AddNewHushedPlaceFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    private static final String TAG = AddNewHushedPlaceFragment.class.getSimpleName();

    public static final String NO_MORE_PLACES_TO_HUSH = "No more places...";

    private static final int PLACE_PICKER_REQUEST = 11;

    private static final int IS_INITIALIZED = 1;

    private static final int NOT_INITIALIZED = -1;

    private ArrayAdapter<String> _mSpinnerAdapter;

    private String _mCurSelectedPlace;

    private PlaceModel _mPlace;

    private String _mUserFriendlySelectedPlace;
    private List<String> _mHushedUserFriendlyNames;

    private SQLiteOpenHelper _mDbHelper;

    private HushedPlacesDb _mHushedPlacesDb;

    private CustomPlacesDb _customPlacesDb;

    private PlacesDb _mPlacesDb;

    private Context _mContext;

    private int _mRingerMode;

    private int _mItemSelectedIndex;

    private List<String> _listOfPlaces;

    private Map<String, PlaceModel> _mPlacesMap;

    private UserPrefPlace _userPrefPlace;

    @BindView(R.id.spinner_places_types)
    protected Spinner _mPlacesTypes;

    @BindView(R.id.btn_save)
    protected Button _mSaveBtn;

    @BindView(R.id.btn_types)
    protected Button _mTypesBtn;

    @BindView(R.id.btn_map)
    protected Button _mMapBtn;

    @BindView(R.id.btn_select_from_map)
    protected ImageButton _mSelectFromMap;

    @BindView(R.id.radio_group)
    protected RadioGroup _mRadioGroup;

    @BindView(R.id.rl_spinner)
    protected RelativeLayout _spinnerLayout;

    @BindView(R.id.rl_map_layout)
    protected RelativeLayout _mapLayout;

    @BindView(R.id.tv_custom_place)
    protected TextView _customPlace;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initializeApplicationData();
    }

    private void initializeApplicationData()
    {

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        int initialized = sharedPreferences.getInt(Constants.INITIALIZED, NOT_INITIALIZED);

        if (initialized == NOT_INITIALIZED)
        {
            PlacesInitializationList placesInitializationList = new PlacesInitializationList(getContext());
            placesInitializationList.initializePlacesDatabase();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constants.INITIALIZED, IS_INITIALIZED);
            editor.apply();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View _mView = inflater.inflate(R.layout.add_new_hushed_place, container, false);

        ButterKnife.bind(this, _mView);

        _mContext = _mView.getContext();
        _mDbHelper = new HushedPlacesDbHelper(_mContext);
        _mHushedPlacesDb = new HushedPlacesDb(_mView.getContext());
        _mHushedUserFriendlyNames = _mHushedPlacesDb.getHushedUserFriendlyNames();
        _mRingerMode = RINGER_MODE_SILENT;
        _mPlacesDb = new PlacesDb(_mContext);
        _mPlacesMap = _mPlacesDb.getPlaces();
        _listOfPlaces = _mPlacesDb.getPlacesAsList();
        _customPlacesDb = new CustomPlacesDb(_mContext);
        _userPrefPlace = null;
        init();

        initializeArrayAdapter();

        setupView();
        _mPlacesTypes.setOnItemSelectedListener(this);

        return _mView;
    }

    private void setupView()
    {
        _mTypesBtn.setClickable(false);
        setButtonActiveColor(_mTypesBtn);
        _mMapBtn.setClickable(true);
        setButtonInActiveColor(_mMapBtn);
    }

    private void setButtonActiveColor(Button button)
    {
        button.setBackgroundTintList(_mContext.getResources().getColorStateList(R.color.colorPrimaryLight, null));
    }

    private void setButtonInActiveColor(Button button)
    {
        button.setBackgroundTintList(_mContext.getResources().getColorStateList(R.color.colorAccent, null));
    }

    private void init()
    {
        _listOfPlaces = _mPlacesDb.getPlacesAsList();
        if (_listOfPlaces.size() == 0)
        {
            _listOfPlaces.add(NO_MORE_PLACES_TO_HUSH);
        }

        _mPlacesMap = _mPlacesDb.getPlaces();
    }

    private void initializeArrayAdapter()
    {
        // Initialize an array adapter with the list of items and default spinner layout
        _mSpinnerAdapter = new ArrayAdapter<>(
                _mContext,
                android.R.layout.simple_spinner_item,
                _listOfPlaces);
        // Specify the layout to use when the list of choices appear
        _mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _mPlacesTypes.setAdapter(_mSpinnerAdapter);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(HushedItemBroadcastReceiver.HUSHED_ITEM_REMOVED);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * Called when an item is selected in the menu.
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if (!_listOfPlaces.contains(NO_MORE_PLACES_TO_HUSH))
        {
            _mUserFriendlySelectedPlace = parent.getItemAtPosition(position).toString();
            _mPlace = _mPlacesMap.get(_mUserFriendlySelectedPlace);
            _mCurSelectedPlace = _mPlace.getPlace();
            _mItemSelectedIndex = position;
        }
    }



    /**
     * Called when nothing selected.
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    @OnClick({R.id.mode_silent, R.id.mode_vibrate})
    public void onRadioButtonSelected(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId())
        {
            case R.id.mode_silent:
                if (checked)
                {
                    setRingerMode(RINGER_MODE_SILENT);
                }
                break;
            case R.id.mode_vibrate:
                if (checked)
                {
                    setRingerMode(RINGER_MODE_VIBRATE);
                }
                break;
        }
    }

    private void setRingerMode(final int ringerMode)
    {
        _mRingerMode = ringerMode;
    }

    @OnClick(R.id.btn_save)
    protected void addNewPlaceToHush()
    {

        if (!_mMapBtn.isClickable() && _userPrefPlace != null)
        {
            String place = _userPrefPlace.getType();
            String placeId = _userPrefPlace.getPlaceId();
            String address = _userPrefPlace.getAddress();
            double longitude = _userPrefPlace.getLongitude();
            double latitude = _userPrefPlace.getLatitude();
            int typeNumber = _userPrefPlace.getTypeNumber();
            long exists = _customPlacesDb.existsId(placeId);

            if (exists == -1)
            {
                addPlaceToHushedDb(place, place);
                addPlaceToCustomDb(placeId, place, typeNumber, address, longitude, latitude);
                resetTextView();
                _userPrefPlace = null;
            }
            else
            {
                String message = place + " already exists";
                Toast.makeText(_mContext, message, Toast.LENGTH_LONG).show();
            }

        }
        else if (!_mTypesBtn.isClickable() && _mCurSelectedPlace != null)
        {
            addPlaceToHushedDb(_mCurSelectedPlace, _mUserFriendlySelectedPlace);
        }

    }

    private void resetTextView()
    {
        _customPlace.setText(getString(R.string.select_from_map));
        resetRadioGroup();
    }

    /**
     * Adds a place to the custom database of places the user has selected
     * from the map.
     * @param placeId
     * @param place
     * @param typeNumber
     * @param address
     * @param longitude
     * @param latitude
     */
    private void addPlaceToCustomDb(String placeId, String place, int typeNumber, String address, double longitude, double latitude)
    {
        long insert = _customPlacesDb.insert(placeId, place, typeNumber, address, longitude, latitude);
    }

    /**
     * Add a place to the database that contains all of the users places
     * they want to be hushed.
     *
     * @param place
     * @param userFriendlyName
     */
    public void addPlaceToHushedDb(String place, String userFriendlyName)
    {
        try
        {
            // TODO: Move insert to db class
            SQLiteDatabase db = _mDbHelper.getWritableDatabase();
            int ringerMode = getRingerMode();
            ContentValues values = new ContentValues();
            values.put(PLACE_TYPE, place);
            values.put(USER_FRIENDLY_NAME, userFriendlyName);
            values.put(RING_MODE, ringerMode);
            long insert = db.insert(HushedPlacesContract.Entry.TABLE_NAME, null, values);

            if (insert > -1)
            {

                broadcastNewItemInsertedIntoDB();
                updateSpinner();
                if (!_mTypesBtn.isClickable())
                {
                    removePlaceFromDb();
                }

                initializeArrayAdapter();
                resetRadioGroup();
            }

            db.close();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Remove the place type from the list of options.
     */
    private void updateSpinner()
    {
        _listOfPlaces.remove(_mUserFriendlySelectedPlace);
        _mSpinnerAdapter.remove(_mUserFriendlySelectedPlace);
        if (_listOfPlaces.size() == 0)
        {
            addEmptyListPlaceholder();
        }

        _mSpinnerAdapter.notifyDataSetChanged();
        _mCurSelectedPlace = null;
        initializeArrayAdapter();
    }

    /**
     * Remove the places added from the global places database.
     */
    private void removePlaceFromDb()
    {
        long remove = _mPlacesDb.removePlace(_mPlace.getPlace());
        _mPlacesMap.remove(_mPlace.getUserFriendlyName());
        _mPlace = null;

        if (remove == -1)
        {
            Toast.makeText(_mContext, R.string.error_occurred, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Resets the radio group to the default Silent.
     */
    private void resetRadioGroup()
    {
        int childCount = _mRadioGroup.getChildCount();

        if (childCount > 0)
        {
            for (int radioIndex = 0; radioIndex < childCount; radioIndex++)
            {
                RadioButton radioButton = (RadioButton) _mRadioGroup.getChildAt(radioIndex);
                if (!radioButton.getText().equals(getString(R.string.silent)))
                {
                    radioButton.setChecked(false);
                }
                else
                {
                    setRingerMode(RINGER_MODE_SILENT);
                    radioButton.setChecked(true);
                }
            }
        }
    }

    /**
     * When an item has been successfully inserted into the database, the result is
     * broadcast so that the UI can be updated with the new data.
     */
    private void broadcastNewItemInsertedIntoDB()
    {
        Intent intent = new Intent(HushedItemBroadcastReceiver.NEW_DATABASE_ITEM_INSERTED);
        getActivity().sendBroadcast(intent);
    }

    /**
     * When the list is empty a default placeholder is added informing the user that
     * no more places are available to be hushed.
     */
    private void addEmptyListPlaceholder()
    {

        if (!_listOfPlaces.contains(NO_MORE_PLACES_TO_HUSH))
        {
            _listOfPlaces.add(NO_MORE_PLACES_TO_HUSH);
        }
    }

    private void removeEmptyListPlaceholder()
    {
        if (_listOfPlaces.contains(NO_MORE_PLACES_TO_HUSH))
        {
            _listOfPlaces.remove(NO_MORE_PLACES_TO_HUSH);
        }
    }


    private int getRingerMode()
    {
        return _mRingerMode;
    }

    private HushedItemBroadcastReceiver receiver = new HushedItemBroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
        if (HushedItemBroadcastReceiver.HUSHED_ITEM_REMOVED.equals(intent.getAction()))
        {
            init();
            initializeArrayAdapter();
        }
        }
    };

    @OnClick({R.id.btn_types, R.id.btn_map})
    protected void typesBtnClick(Button button)
    {
        int id = button.getId();
        boolean isClickable = button.isClickable();

        if (id == R.id.btn_types && isClickable)
        {

            setButtonClickable(_mTypesBtn, false);
            setButtonClickable(_mMapBtn, true);
            setButtonActiveColor(_mTypesBtn);
            setButtonInActiveColor(_mMapBtn);
            updateView(_mTypesBtn);
        }
        else if (id == R.id.btn_map && isClickable)
        {
            setButtonClickable(_mTypesBtn, true);
            setButtonClickable(_mMapBtn, false);
            setButtonActiveColor(_mMapBtn);
            setButtonInActiveColor(_mTypesBtn);
            updateView(_mMapBtn);
        }

    }


    private void setActiveButton(Button button1, Button button2, boolean isClickable)
    {
        setButtonClickable(button1, false);
        setButtonClickable(button2, true);
    }

    private void updateView(Button button)
    {
        if (button.getId() == _mTypesBtn.getId())
        {
            _spinnerLayout.setVisibility(View.VISIBLE);
            _mapLayout.setVisibility(View.GONE);
        }
        else
        {
            _spinnerLayout.setVisibility(View.GONE);
            _mapLayout.setVisibility(View.VISIBLE);
        }
    }


    private void setButtonClickable(Button button, boolean isClickable)
    {
        button.setClickable(isClickable);
    }

    @OnClick(R.id.btn_select_from_map)
    protected void launchMap()
    {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try
        {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
       if (requestCode == PLACE_PICKER_REQUEST)
       {
           if (resultCode == Activity.RESULT_OK)
           {
               Place place = PlacePicker.getPlace(_mContext, data);
               setCustomPlace(place);
           }
       }
    }

    private void setCustomPlace(Place place)
    {
        String placeId = place.getId();
        String name = place.getName().toString();
        Integer typeNumber = place.getPlaceTypes().get(0);
        String address = place.getAddress().toString();
        double longitude = place.getLatLng().longitude;
        double latitude = place.getLatLng().latitude;

        _customPlace.setText(name);
        System.out.println(placeId);
        _userPrefPlace = new UserPrefPlace(placeId, name, typeNumber, typeNumber, address, longitude, latitude);
    }
}
