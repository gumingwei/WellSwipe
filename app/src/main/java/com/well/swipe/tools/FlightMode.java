package com.well.swipe.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Settings;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/27/16.
 */
public class FlightMode {

    public static boolean getAirplaneMode(Context context) {
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0);
        return (isAirplaneMode == 1) ? true : false;
    }

    public static void setAirplaneModeOn(Context context, boolean enabling) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, enabling ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enabling);
        context.sendBroadcast(intent);
    }


    public static BitmapDrawable getDrawableState(Context context) {
        if (getAirplaneMode(context)) {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_airplane_mode_on);
        } else {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_airplane_mode_off);
        }
    }
}
