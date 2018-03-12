package com.android.shnellers.hushed.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shnellers.hushed.MainActivity;
import com.android.shnellers.hushed.R;
import com.android.shnellers.hushed.assets.PlacesInitializationList;
import com.android.shnellers.hushed.database.CurrentPlaceDB;
import com.android.shnellers.hushed.database.CustomPlacesDb;
import com.android.shnellers.hushed.database.HushedPlacesDb;
import com.android.shnellers.hushed.database.PlacesDb;
import com.android.shnellers.hushed.dialogs.CustomDialogFragment;
import com.android.shnellers.hushed.dialogs.DialogListener;
import com.android.shnellers.hushed.models.HushedPlace;
import com.android.shnellers.hushed.receivers.HushedItemBroadcastReceiver;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sean on 11/12/17.
 */

public class HushedPlaceAdapter extends RecyclerView.Adapter<HushedPlaceAdapter.ViewHolder> implements DialogListener
{
    public static final int DIALOG_FRAGMENT_RESULT = 6564;

    private Context _mContext;

    private List<HushedPlace> _hushedPlaces;

    private HushedPlacesDb _mDb;
    private CurrentPlaceDB _currentDb;
    private CustomPlacesDb _mCustomPlacesDb;
    private PlacesDb _placesDb;

    public interface OnLongItemClickListener
    {
        boolean onLongItemClicked(int position);
    }

    public HushedPlaceAdapter(Context context, List<HushedPlace> hushedPlaces)
    {
        _mContext = context;
        _hushedPlaces = hushedPlaces;
        _mDb = new HushedPlacesDb(_mContext);
        _currentDb = new CurrentPlaceDB(_mContext);
        _mCustomPlacesDb = new CustomPlacesDb(_mContext);
        _placesDb = new PlacesDb(_mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(_mContext)
                .inflate(R.layout.rv_hushed_item_layout, parent, false);

        return new ViewHolder(view);
    }


    /**
     * Method used to bind the data to the views in the layout.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position)
    {
        final HushedPlace hushedPlace = _hushedPlaces.get(position);
        final String type = hushedPlace.getType();
        final String ringMode = hushedPlace.getRingMode();
        final String userFriendlyName = hushedPlace.getUserFriendlyType();
        final int id = hushedPlace.getId();
        final int icon = PlacesInitializationList.ICON_MAP.get(type) == null ? R.drawable.ic_place : PlacesInitializationList.ICON_MAP.get(type);
        final int constValue = hushedPlace.getConstValue();

        holder._mRelativeLayout.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                String message = "Remove " + userFriendlyName + "?";

                MainActivity activity = (MainActivity) _mContext;

                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                Fragment prev = activity.getSupportFragmentManager().findFragmentById(R.id.dialog_fragment_layout);

                if (prev != null)
                {
                    fragmentTransaction.remove(prev);
                }

                fragmentTransaction.addToBackStack(null);

                CustomDialogFragment dialogFragment = CustomDialogFragment.newInstance(id, message, type, userFriendlyName, position, constValue);
                dialogFragment.setListener(HushedPlaceAdapter.this);
                dialogFragment.show(fragmentTransaction, "Custom Dialog");

                return true;
            }
        });

        holder._placeType.setText(userFriendlyName);
        holder._ringMode.setText(ringMode);
        holder._imageView.setImageResource(icon);
    }

    @Override
    public void onReturnResult(int id, int result, String type, String userFriendlyName, int position, String placeId, int constValue)
    {
        // TODO: Ensure result is remove and not close
        if (result == DIALOG_FRAGMENT_RESULT)
        {
            _hushedPlaces.remove(position);
            notifyDataSetChanged();

            try
            {
                int removedCustom = removeCustomPlace(placeId, type);
                int removedHushed = removeFromHushedPlacesDb(id);
                removeFromCurrentPlacesDb(type);
                addKnownPlaceBackToDb(type, userFriendlyName, constValue);

                broadcastItemsRemovedFromDb(removedCustom, removedHushed);
            }
            catch (SQLiteException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void broadcastItemsRemovedFromDb(int removedCustom, int removedHushed)
    {
        if (removedHushed > 0)
        {
            Toast.makeText(_mContext, R.string.deleted, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HushedItemBroadcastReceiver.HUSHED_ITEM_REMOVED);
            _mContext.sendBroadcast(intent);
        }

    }

    private int removeCustomPlace(String placeId, String type)
    {
        int removed = -1;
        if (placeId != null && placeId.length() > 0)
        {
            removed = _mCustomPlacesDb.remove(type);
            if (removed == -1)
            {
                Toast.makeText(_mContext, "Failed to remove custom place", Toast.LENGTH_LONG).show();
            }
        }

        return removed;
    }

    private int removeFromHushedPlacesDb(int id)
    {
        int delete = _mDb.removeHushedPlaceFromDb(id);

        if (delete == 0)
        {
            Toast.makeText(_mContext, R.string.error_occurred, Toast.LENGTH_LONG).show();
        }

        return delete;
    }

    private void removeFromCurrentPlacesDb(String type)
    {
        String currentPlace = _currentDb.getCurrentPlace();
        if (currentPlace != null && !currentPlace.isEmpty() && currentPlace.equals(type))
        {
            _currentDb.reset();
        }
    }

    private void addKnownPlaceBackToDb(String type, String userFriendlyName, int constValue)
    {
        String isKnownPlace = PlacesInitializationList.KNOWN_PLACES.get(type);
        if (isKnownPlace != null)
        {
            long insert = _placesDb.addPlace(type, userFriendlyName, PlacesInitializationList.ICON_MAP.get(type), constValue);
        }
    }

    @Override
    public int getItemCount()
    {
        if (_hushedPlaces != null && !_hushedPlaces.isEmpty())
        {
            return _hushedPlaces.size();
        }

        return 0;
    }

    /**
     * Updates the list of hushed places. notifyDataSetChanged is used to refresh the
     * recycler view.
     *
     * @param hushedPlaces
     */
    public void updateHushedPlaces(List<HushedPlace> hushedPlaces)
    {
        _hushedPlaces = hushedPlaces;
        this.notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.rel_layout_hushed_place)
        RelativeLayout _mRelativeLayout;

        @BindView(R.id.tv_place_type)
        TextView _placeType;

        @BindView(R.id.tv_ring_mode)
        TextView _ringMode;

        @BindView(R.id.icon)
        ImageView _imageView;

        public ViewHolder(View itemView)
        {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
