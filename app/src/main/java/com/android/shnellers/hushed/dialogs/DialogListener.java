package com.android.shnellers.hushed.dialogs;

/**
 * Created by sean on 08/01/18.
 */

public interface DialogListener
{
    void onReturnResult(int id, int dialogFragmentResult, String type, String userFriendlyName, int result, String placeId, int _constValue);
}
