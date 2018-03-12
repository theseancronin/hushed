package com.android.shnellers.hushed.dialogs;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.android.shnellers.hushed.R;

import butterknife.ButterKnife;

/**
 * Created by sean on 08/02/18.
 */

public class ContactsDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener
{
    @SuppressLint("ObsoleteSdkInt")
    private static final String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };

    private static final int[] TO_IDS = {
        R.id.tv_contact
    };

    private ListView _mContactsList;

    private long _mContactId;

    private String _mContactKey;

    private Uri _mContactUri;

    private SimpleCursorAdapter _mSimpleCursorAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.contacts_dialog_layout, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }
}
