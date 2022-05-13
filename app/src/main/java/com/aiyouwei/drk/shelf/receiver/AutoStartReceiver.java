package com.aiyouwei.drk.shelf.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.aiyouwei.drk.shelf.MainActivity;

/*
开机自启
 */
public class AutoStartReceiver extends BroadcastReceiver {
//    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
    }
}
