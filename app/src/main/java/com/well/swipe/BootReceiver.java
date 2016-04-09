package com.well.swipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.well.swipe.service.SwipeService;

/**
 * Created by mingwei on 4/9/16.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SwipeService.class));
    }
}
