package com.example.mkz.dataz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent("com.example.mkz.dataz.DatazIntentService");
        intent1.setClass(context,DatazIntentService.class);
        context.startService(intent1);
    }
}
