package com.well.swipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.well.swipe.service.SwipeService;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipe.utils.SettingHelper;

/**
 * Created by mingwei on 4/9/16.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            if (SettingHelper.getInstance(context).getBoolean(SwipeSetting.SWIPE_TOGGLE, true)) {

                context.startService(new Intent(context, SwipeService.class));
            }
        }

    }
}
