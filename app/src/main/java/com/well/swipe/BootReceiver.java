package com.well.swipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.well.swipe.service.SwipeService;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipecomm.utils.SettingHelper;

/**
 * Created by mingwei on 4/9/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
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
