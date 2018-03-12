package com.android.shnellers.hushed.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.android.shnellers.hushed.R;

/**
 * Created by sean on 18/01/18.
 */
public class NotificationCreator
{
    public static void createNotification(Context context, final String id, final String type, final String ringMode)
    {
        String formattedStr = String.format("%s detected. Do you wish to turn the phone on %s?", type, ringMode);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Hushed Detected a PlaceModel")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(formattedStr));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null)
        {
            notificationManager.notify(1, builder.build());
        }

    }
}

