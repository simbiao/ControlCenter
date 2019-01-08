package com.vsun.controlcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vsun.controlcenter.services.ControlCenterService;

/**
 * Created by ubuntu on 17-10-20.
 */

public class BootReceiver extends BroadcastReceiver {

    private String TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(TAG, "system boot completed");
            Intent service = new Intent(context, ControlCenterService.class);
            context.startService(service);
        }
    }
}
