package com.android.shnellers.hushed.components;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by sean on 19/10/17.
 */

public class SnackBarComponent
{
    private View mView;

    private Context mContext;

    public SnackBarComponent(View view, Context context)
    {
        mView = view;

        mContext = context;
    }

    /**
     *
     *
     * @param mainTextStringId
     * @param actionStringId
     * @param listener
     */
    public void showSnackBar(final int mainTextStringId, final int actionStringId, final View.OnClickListener listener)
    {
        Snackbar.make(
                mView.findViewById(android.R.id.content),
                mContext.getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(mContext.getString(actionStringId), listener)
                .show();
    }
}
