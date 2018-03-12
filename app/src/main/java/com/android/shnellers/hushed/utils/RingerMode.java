package com.android.shnellers.hushed.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.provider.MediaStore;

import com.android.shnellers.hushed.models.Mode;

import java.util.HashMap;
import java.util.Map;

import static android.media.AudioManager.*;

/**
 * Created by sean on 06/12/17.
 */

public class RingerMode
{
    private RingerMode(){}

    @SuppressLint("UseSparseArrays")
    public static final Map<Integer, Mode> RINGER_MODE = new HashMap<>();

    static
    {
        RINGER_MODE.put(RINGER_MODE_SILENT, new Mode(RINGER_MODE_SILENT, "Silent"));
        RINGER_MODE.put(RINGER_MODE_VIBRATE, new Mode(RINGER_MODE_VIBRATE, "Vibrate"));
        RINGER_MODE.put(RINGER_MODE_NORMAL, new Mode(RINGER_MODE_NORMAL, "Normal"));
    }

    public static void setRingerMode(Context context, String ringerMode)
    {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
        {
            switch (ringerMode)
            {
                case "Silent":
                    audioManager.setRingerMode(RINGER_MODE_SILENT);
                    break;
                case "Vibrate":
                    audioManager.setRingerMode(RINGER_MODE_VIBRATE);
                    break;
                case "Normal":
                    audioManager.setRingerMode(RINGER_MODE_NORMAL);
                    break;
            }
        }
    }
}
