package com.example.mediaplayer.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        onReceive(context,new Intent("cha_cha")
        .putExtra("actionname",intent));
    }
}
