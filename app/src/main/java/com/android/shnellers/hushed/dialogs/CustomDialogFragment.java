package com.android.shnellers.hushed.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.shnellers.hushed.R;
import com.android.shnellers.hushed.adapters.HushedPlaceAdapter;
import com.android.shnellers.hushed.database.CustomPlacesDb;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sean on 08/01/18.
 */

public class CustomDialogFragment extends DialogFragment
{
    private static final String MESSAGE_KEY = "com.android.shnellers.hushed.dialogs.message";
    private static final String TYPE_KEY = "com.android.shnellers.hushed.dialogs.type";
    private static final String POSITION_KEY = "com.android.shnellers.hushed.dialogs.position";
    private static final String ID_KEY = "com.android.shnellers.hushed.dialogs.id";
    private static final String USER_FRIENDLY_NAME_KEY = "com.android.shnellers.hushed.dialogs.user_friendly_name";
    private static final String CONST_VALUE = "com.android.shnellers.hushed.dialogs.const_value";

    private CustomPlacesDb _customPlacesDb;

    @BindView(R.id.tv_remove_item)
    protected TextView _mTvRemoveItem;

    @BindView(R.id.btn_remove)
    protected Button _btnRemove;

    @BindView(R.id.btn_cancel)
    protected Button _btnCancel;

    private String _mMessage;
    private String _mType;
    private String _mUserFriendlyName;
    private String _placeId;
    private int _mPosition;
    private int _id;
    private int _constValue;

    private DialogListener _mListener;

    public static CustomDialogFragment newInstance(int id, String message, String type, String userFriendlyName, int position, int constValue)
    {
        Bundle args = new Bundle();

        CustomDialogFragment fragment = new CustomDialogFragment();

        args.putString(MESSAGE_KEY, message);
        args.putString(TYPE_KEY, type);
        args.putInt(POSITION_KEY, position);
        args.putInt(ID_KEY, id);
        args.putString(USER_FRIENDLY_NAME_KEY, userFriendlyName);
        args.putInt(CONST_VALUE, constValue);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener (DialogListener listener)
    {
        _mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _mMessage = getArguments().getString(MESSAGE_KEY);
        _mType = getArguments().getString(TYPE_KEY);
        _mPosition = getArguments().getInt(POSITION_KEY);
        _id = getArguments().getInt(ID_KEY);
        _mUserFriendlyName = getArguments().getString(USER_FRIENDLY_NAME_KEY);
        _constValue = getArguments().getInt(CONST_VALUE);
        _customPlacesDb = new CustomPlacesDb(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.remove_hushed_place_dialog, container, false);

        ButterKnife.bind(this, view);

        _mTvRemoveItem.setText(_mMessage);

        return view;
    }

    @OnClick(R.id.btn_remove)
    protected void remove()
    {
        String placeId = null;
        long isCustomPlace = _customPlacesDb.existsPlace(_mType);

        if (isCustomPlace > -1)
        {
            placeId = _customPlacesDb.getPlaceId(_mType);
        }

        _mListener.onReturnResult(_id, HushedPlaceAdapter.DIALOG_FRAGMENT_RESULT, _mType, _mUserFriendlyName, _mPosition, placeId, _constValue);
        dismiss();
    }

    @OnClick(R.id.btn_cancel)
    protected void cancel ()
    {
        dismiss();
    }
}
