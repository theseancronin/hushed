package com.android.shnellers.hushed.components;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.shnellers.hushed.R;
import com.android.shnellers.hushed.adapters.HushedPlaceAdapter;
import com.android.shnellers.hushed.database.HushedPlacesDb;
import com.android.shnellers.hushed.models.HushedPlace;
import com.android.shnellers.hushed.receivers.HushedItemBroadcastReceiver;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sean on 06/12/17.
 */

public class HushedPlacesRecyclerView extends Fragment
{
    private static final String TAG = HushedPlacesRecyclerView.class.getSimpleName();

    @BindView(R.id.rv_hushed_places)
    protected RecyclerView _mRecyclerView;

    private HushedPlaceAdapter _adapter;
    private RecyclerView.LayoutManager _mLayoutManager;

    private HushedPlacesDb _db;
    private List<HushedPlace> _mHushedPlaces;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.hushed_places_recycler_view_layout, container, false);

        ButterKnife.bind(this, view);

        _db = new HushedPlacesDb(view.getContext());

        initialiseHushedPlacesList();

        initialiseRecyclerView(view.getContext());


        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(HushedItemBroadcastReceiver.NEW_DATABASE_ITEM_INSERTED);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }

    private void initialiseHushedPlacesList()
    {
        _mHushedPlaces = _db.getHushedPlaces();
    }


    private void initialiseRecyclerView(Context context)
    {
        _mRecyclerView.setHasFixedSize(true);

        _mLayoutManager = new LinearLayoutManager(context);

        _adapter = new HushedPlaceAdapter(context, _mHushedPlaces);

        _mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        _mRecyclerView.setAdapter(_adapter);

        _mRecyclerView.setLayoutManager(_mLayoutManager);
    }

    private HushedItemBroadcastReceiver receiver = new HushedItemBroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (HushedItemBroadcastReceiver.NEW_DATABASE_ITEM_INSERTED.equals(intent.getAction()))
            {
                _mHushedPlaces = _db.getHushedPlaces();
                _adapter.updateHushedPlaces(_mHushedPlaces);
            }
        }

    };
}
