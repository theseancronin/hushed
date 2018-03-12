package com.android.shnellers.hushed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sean on 20/12/17.
 */

public class HushedItemBroadcastReceiver extends BroadcastReceiver
{
    public static final String NEW_DATABASE_ITEM_INSERTED = "com.android.shnellers.hushed.receivers.NEW_DATABASE_ITEM_INSERTED";

    public static final String HUSHED_ITEM_REMOVED = "com.android.shnellers.hushed.receivers.HUSHED_ITEM_REMOVED";

    @Override
    public void onReceive(Context context, Intent intent)
    {

    }
}
