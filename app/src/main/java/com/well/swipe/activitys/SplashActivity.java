package com.well.swipe.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.telephony.TelephonyManager;

import com.well.swipe.R;
import com.well.swipe.service.SwipeService;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipe.utils.SettingHelper;

/**
 * Created by mingwei on 4/4/16.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (isEmulator(getApplicationContext())) {
            //Process.killProcess(android.os.Process.myPid());
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SettingHelper.getInstance(getBaseContext()).getBoolean(SwipeSetting.SWIPE_TOGGLE, true)) {
                    startService(new Intent(getBaseContext(), SwipeService.class));
                }
                startActivity(new Intent(getBaseContext(), SwipeSettingActivity.class));
                finish();
            }
        }, 1000);
    }

    boolean isEmulator(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei != null && imei.equals("000000000000000")) {
                return true;
            }
            return (Build.MODEL.equals("sdk"))
                    || (Build.MODEL.equals("google_sdk"));
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }
}
